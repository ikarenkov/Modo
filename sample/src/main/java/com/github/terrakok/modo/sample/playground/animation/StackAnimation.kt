package com.github.terrakok.modo.sample.playground.animation

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.SaveableContent
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.animation.StackTransitionType
import com.github.terrakok.modo.animation.calculateStackTransitionType
import com.github.terrakok.modo.model.lifecycleDependency
import com.github.terrakok.modo.stack.StackState
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.job

/**
 * Stack animation composable that renders screens with customizable animations.
 *
 * ## Animation Queueing
 *
 * By default, navigation changes during an ongoing animation are queued and batched to prevent
 * animation interruptions and ensure smooth transitions.
 *
 * **Example:**
 * 1. Initial state: (A)
 * 2. Navigation to (A, B) starts → animation (A) → (A, B) begins
 * 3. During animation, stack changes to (A, B, C) → queued
 * 4. Stack changes again to (A, B, C, D) → latest state queued
 * 5. Animation (A) → (A, B) completes
 * 6. System animates directly from (A, B) → (A, B, C, D), skipping intermediate state (A, B, C)
 *
 * @param modifier the modifier to apply to the animation container.
 * @param animator the [StackAnimator] to use for animating screen transitions. Default is fade + slide.
 * @param animationSpec the animation spec to control duration and easing. Default is tween(300ms).
 * @param waitForAnimationCompletion if true, queues navigation changes during animation (default);
 * if false, immediately processes changes, potentially interrupting the current animation.
 * @param content the content to render for each screen. Default uses SaveableContent with manual resume/pause.
 */
@OptIn(ExperimentalModoApi::class)
@Composable
fun StackState.StackAnimation(
    modifier: Modifier = Modifier,
    animator: StackAnimator = fade() + slide(),
    animationSpec: FiniteAnimationSpec<Float> = tween(durationMillis = 300),
    waitForAnimationCompletion: Boolean = true,
    content: @Composable (Screen) -> Unit = { it.SaveableContent(manualResumePause = true) }
) {
    var animationScreens by rememberAnimationItems(isDialogs = false, waitForAnimationCompletion = waitForAnimationCompletion)
    var animationDialogs by rememberAnimationItems(isDialogs = true, waitForAnimationCompletion = waitForAnimationCompletion)

    // Single animation state for all screens in the transition
    val hasAnimatingScreens by remember {
        derivedStateOf {
            animationScreens.any { it.value.isAnimating } ||
                animationDialogs.any { it.value.isAnimating }
        }
    }
    // Shared animation progress reused by every screen rendered in this StackAnimation
    var animationProgress by remember { mutableFloatStateOf(1f) }
    // Tracks whether a coroutine-driven animation is currently running
    var animationRunning by remember { mutableStateOf(false) }
    if (hasAnimatingScreens && !animationRunning) {
        // Next composition should render the new screens at the start of the animation
        animationProgress = 0f
        animationRunning = true
    }

    // TODO: make a sample of stack where multiple items, stack is Idle and dialog is entering

    // FIXME: when stack initial state is set there is no callback for screen shown for initial idle state

    // Single LaunchedEffect to drive the animation for all screens
    LaunchedEffect(hasAnimatingScreens) {
        if (hasAnimatingScreens) {
            try {
                // Notify all screens that animation started
                animationScreens.forEach { handleAnimationStart(it.value) }
                animationDialogs.forEach { handleAnimationStart(it.value) }

                // Drive a single transition coroutine that updates shared progress 0f -> 1f
                animate(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = animationSpec
                ) { value, _ ->
                    animationProgress = value
                }
                animationProgress = 1f

                // Animation finished - notify screens and cleanup
                animationScreens.forEach { handleAnimationFinish(it.value) }
                animationDialogs.forEach { handleAnimationFinish(it.value) }

                // Update animation items: remove exiting, mark others as not animating
                animationScreens = animationScreens.removeExitingAndMarkIdle()
                animationDialogs = animationDialogs.removeExitingAndMarkIdle()
            } finally {
                // Handle cancellation if needed
                if (currentCoroutineContext().job.isCancelled) {
                    // TODO: test interruption. Maybe we needt it in case of running animation and rotation of screen, we need to mark screen as shown
//                    animationScreens.values.handleAnimationFinish()
//                    animationDialogs.values.handleAnimationFinish()
                }
                animationRunning = false
            }
        } else {
            animationScreens.values.forEach { item ->
                if (item.isInitial) {
                    // It is okay to call it multiple times, f.e. if rotate screen
                    item.screen.lifecycleDependency()?.showTransitionFinished()
                }
            }
            // Idle state renders as fully visible
            animationProgress = 1f
            animationRunning = false
        }
    }

    // Render all animation items
    Box(modifier = modifier) {
        animationScreens.forEach { (screenKey, item) ->
            key(screenKey) {
                AnimatedScreen(
                    item = item,
                    animator = animator,
                    progress = animationProgress,
                    content = content
                )
            }
        }
        animationDialogs.forEach { (screenKey, item) ->
            key(screenKey) {
                AnimatedScreen(
                    item = item,
                    animator = animator,
                    progress = animationProgress,
                    content = content
                )
            }
        }
    }
}

private fun handleAnimationStart(item: AnimationItem) {
    val lifecycleDependency = item.screen.lifecycleDependency()
    when (item.animationPhase) {
        ScreenAnimationPhase.EXIT -> lifecycleDependency?.hideTransitionStarted()
        // Trigger it as soon as animation start because it already visible
        ScreenAnimationPhase.IDLE -> lifecycleDependency?.showTransitionFinished()
        ScreenAnimationPhase.ENTER -> {}
    }
}

private fun handleAnimationFinish(item: AnimationItem) {
    val lifecycleDependency = item.screen.lifecycleDependency()
    when (item.animationPhase) {
        ScreenAnimationPhase.ENTER -> lifecycleDependency?.showTransitionFinished()
        ScreenAnimationPhase.EXIT, ScreenAnimationPhase.IDLE -> {}
    }
}

/**
 * Removes exiting screens and marks remaining screens as idle (not animating).
 * Also clears oldStack to prevent memory leaks.
 */
private fun Map<ScreenKey, AnimationItem>.removeExitingAndMarkIdle(): Map<ScreenKey, AnimationItem> {
    return buildMap(size) {
        this@removeExitingAndMarkIdle.forEach { (screenKey, item) ->
            if (!item.animationPhase.isExit) {
                put(
                    screenKey,
                    item.copy(
                        animationPhase = ScreenAnimationPhase.IDLE,
                        isAnimating = false,
                        // Clear to prevent memory leaks
                        oldStack = emptyList()
                    )
                )
            }
        }
    }
}

//@OptIn(ExperimentalModoApi::class)
//@Composable
//fun ComposeRendererScope<StackState>.rememberAnimationItems(): MutableState<Map<ScreenKey, AnimationItem>> {
//    val currentScreen = screen
//
//    // Track animation items (screens currently being rendered/animated)
//    var animationItems = remember { mutableStateOf<Map<ScreenKey, AnimationItem>>(emptyMap()) }
//    var currentStackState by remember { mutableStateOf<StackState?>(null) }
//
//    // Detect stack changes and update animation items
//    if (newState != currentStackState) {
//        assert(oldState == this.oldState)
//        val oldState = currentStackState
//        currentStackState = newState
//
//        // Calculate transition type using Modo's existing logic
//        val transitionType = calculateStackTransitionType()
//
//        val newItems = calculateAnimationItems(
//            transitionType = transitionType,
//            oldScreen = oldState?.stack?.dropLastWhile { it is DialogScreen }?.lastOrNull(),
//            newScreen = currentScreen
//        )
//
//        // Update animation items (simplified - can be enhanced to queue animations)
//        animationItems.value = newItems
//    }
//    return animationItems
//}

/**
 * Helper function to process stack transition and generate animation items.
 * Extracted to avoid code duplication.
 */
@OptIn(ExperimentalModoApi::class)
private fun processStackTransition(
    oldStack: List<Screen>,
    newStack: List<Screen>,
    isDialogs: Boolean
): Map<ScreenKey, AnimationItem> {
    val transitionType = calculateStackTransitionType(
        oldStack = oldStack,
        newStack = newStack,
        firstScreenIdle = !isDialogs
    )

    return calculateAnimationItems(
        transitionType = transitionType,
        oldStack = oldStack,
        newStack = newStack,
        oldScreen = oldStack.lastOrNull(),
        newScreen = newStack.lastOrNull()
    )
}

/**
 * Remembers animation items without queueing - allows overlapping animations.
 * When navigation happens during animation, immediately starts new animation.
 */
@OptIn(ExperimentalModoApi::class)
@Composable
private fun StackState.rememberAnimationItemsSimple(
    isDialogs: Boolean = false
): MutableState<Map<ScreenKey, AnimationItem>> {
    val filteredNewStack = remember(stack) {
        if (isDialogs) {
            stack.takeLastWhile { it is DialogScreen }
        } else {
            stack.dropLastWhile { it is DialogScreen }
        }
    }

    val animationItems = remember { mutableStateOf<Map<ScreenKey, AnimationItem>>(emptyMap()) }
    var currentStack by remember { mutableStateOf<List<Screen>>(emptyList()) }

    // Detect stack changes and update animation items immediately
    if (filteredNewStack != currentStack) {
        val oldStack = currentStack
        currentStack = filteredNewStack

        animationItems.value = processStackTransition(
            oldStack = oldStack,
            newStack = filteredNewStack,
            isDialogs = isDialogs
        )
    }

    return animationItems
}

/**
 * Remembers animation items with queueing - prevents overlapping animations.
 *
 * When navigation happens during animation, queues the change until current animation finishes.
 * This ensures smooth transitions by batching rapid navigation changes. For example:
 * - Stack changes: A → (A,B) → (A,B,C) → (A,B,C,D)
 * - If animation A→B is running when changes to (A,B,C) and (A,B,C,D) occur
 * - The system completes A→B animation first
 * - Then animates from final state of A→B directly to (A,B,C,D)
 * - Intermediate state (A,B,C) is skipped, avoiding animation interruption
 */
@OptIn(ExperimentalModoApi::class)
@Composable
private fun StackState.rememberAnimationItemsQueued(
    isDialogs: Boolean = false
): MutableState<Map<ScreenKey, AnimationItem>> {
    val filteredNewStack = remember(stack) {
        if (isDialogs) {
            stack.takeLastWhile { it is DialogScreen }
        } else {
            stack.dropLastWhile { it is DialogScreen }
        }
    }

    val animationItems = remember { mutableStateOf<Map<ScreenKey, AnimationItem>>(emptyMap()) }
    var currentStack by remember { mutableStateOf<List<Screen>>(emptyList()) }
    var visibleStack by remember { mutableStateOf<List<Screen>>(emptyList()) }
    var pendingStack by remember { mutableStateOf<List<Screen>?>(null) }

    val hasAnimatingScreens by remember {
        derivedStateOf {
            animationItems.value.any { it.value.isAnimating }
        }
    }

    // Detect stack changes
    if (filteredNewStack != currentStack) {
        if (hasAnimatingScreens) {
            // Animation in progress - queue this change
            pendingStack = filteredNewStack
            currentStack = filteredNewStack
        } else {
            // No animation - process immediately
            val oldStack = visibleStack
            currentStack = filteredNewStack
            visibleStack = filteredNewStack

            animationItems.value = processStackTransition(
                oldStack = oldStack,
                newStack = filteredNewStack,
                isDialogs = isDialogs
            )
        }
    }

    // Process queued stack change when animations finish
    LaunchedEffect(hasAnimatingScreens, pendingStack) {
        if (!hasAnimatingScreens && pendingStack != null) {
            val queuedStack = pendingStack!!
            pendingStack = null

            val oldStack = visibleStack
            visibleStack = queuedStack

            animationItems.value = processStackTransition(
                oldStack = oldStack,
                newStack = queuedStack,
                isDialogs = isDialogs
            )
        }
    }

    return animationItems
}

/**
 * Remembers animation items with optional queueing.
 * Delegates to either simple or queued implementation based on parameter.
 *
 * @param isDialogs whether to track dialog screens or regular screens
 * @param waitForAnimationCompletion if true, queues navigation changes during animation;
 * if false, immediately processes changes (may interrupt current animation)
 */
@OptIn(ExperimentalModoApi::class)
@Composable
fun StackState.rememberAnimationItems(
    isDialogs: Boolean = false,
    waitForAnimationCompletion: Boolean = true
): MutableState<Map<ScreenKey, AnimationItem>> {
    return if (waitForAnimationCompletion) {
        rememberAnimationItemsQueued(isDialogs)
    } else {
        rememberAnimationItemsSimple(isDialogs)
    }
}

@OptIn(ExperimentalModoApi::class)
@Composable
fun StackState.rememberDialogsAnimationItems(): MutableState<Map<ScreenKey, AnimationItem>> {
    val filteredNewStack = remember(this) {
        stack.takeLastWhile { it is DialogScreen }
    }
    val currentScreen = filteredNewStack.lastOrNull()

    // Track animation items (screens currently being rendered/animated)
    val animationItems = remember { mutableStateOf<Map<ScreenKey, AnimationItem>>(emptyMap()) }
    var currentStack by remember { mutableStateOf<List<Screen>>(emptyList()) }

    // Detect stack changes and update animation items
    if (filteredNewStack != currentStack) {
        val oldStack = currentStack
        currentStack = filteredNewStack

        // Calculate transition type using Modo's existing logic
        val transitionType = calculateStackTransitionType(
            oldStack = oldStack,
            newStack = filteredNewStack,
        )

        val newItems = calculateAnimationItems(
            transitionType = transitionType,
            oldStack = oldStack,
            newStack = filteredNewStack,
            oldScreen = oldStack.lastOrNull(),
            newScreen = currentScreen
        )

        // Update animation items (simplified - can be enhanced to queue animations)
        animationItems.value = newItems
    }
    return animationItems
}

/**
 * Renders a single animated screen using the provided animator.
 * Receives animation progress from the parent StackAnimation.
 */
@Composable
private fun AnimatedScreen(
    item: AnimationItem,
    animator: StackAnimator,
    progress: Float,
    content: @Composable (Screen) -> Unit
) {
    val context = remember(item) {
        StackAnimationContext(
            screen = item.screen,
            oldStack = item.oldStack,
            newStack = item.newStack,
            direction = item.animationPhase,
            isInitial = item.isInitial
        )
    }

    // Use provided progress (1f for initial/idle, 0f-1f for animating)
    val effectiveProgress = if (context.isInitial || item.animationPhase == ScreenAnimationPhase.IDLE) {
        1f
    } else {
        progress
    }

    // TODO: think about custom animation per screen by using some interface-marker for screen
    animator(
        progress = effectiveProgress,
        context = context
    ) { modifier ->
        Box(modifier = modifier) {
            content(item.screen)
        }
    }
}

/**
 * Calculate animation items based on transition type
 */
@OptIn(ExperimentalModoApi::class)
private fun calculateAnimationItems(
    transitionType: StackTransitionType,
    oldStack: List<Screen>,
    newStack: List<Screen>,
    oldScreen: Screen?,
    newScreen: Screen?,
): Map<ScreenKey, AnimationItem> {
    if (newScreen == null) {
        return if (oldScreen == null) {
            emptyMap()
        } else {
            mapOf(
                oldScreen.screenKey to
                    AnimationItem(
                        screen = oldScreen,
                        oldStack = emptyList(),
                        newStack = newStack,
                        animationPhase = ScreenAnimationPhase.EXIT,
                        isInitial = false,
                        isAnimating = true
                    )
            )
        }
    }

    return when (transitionType) {
        StackTransitionType.Idle -> {
            // No animation or initial render
            mapOf(
                newScreen.screenKey to AnimationItem(
                    screen = newScreen,
                    oldStack = oldStack,
                    newStack = newStack,
                    animationPhase = ScreenAnimationPhase.IDLE,
                    isInitial = oldScreen == null,
                    isAnimating = false
                )
            )
        }

        StackTransitionType.Pop -> {
            // Going back: previous screen enters from back, current exits to front
            buildMap {
                put(
                    newScreen.screenKey,
                    AnimationItem(
                        screen = newScreen,
                        oldStack = oldStack,
                        newStack = newStack,
                        animationPhase = ScreenAnimationPhase.ENTER,
                        isInitial = false,
                        isAnimating = true
                    )
                )
                if (oldScreen != null) {
                    put(
                        oldScreen.screenKey,
                        AnimationItem(
                            screen = oldScreen,
                            oldStack = oldStack,
                            newStack = newStack,
                            animationPhase = ScreenAnimationPhase.EXIT,
                            isInitial = false,
                            isAnimating = true
                        )
                    )
                }
            }
        }

        StackTransitionType.Push,
        StackTransitionType.Replace -> {
            // Going forward: new screen enters from front, old exits to back
            buildMap {
                if (oldScreen != null && oldScreen.screenKey != newScreen.screenKey) {
                    put(
                        oldScreen.screenKey,
                        AnimationItem(
                            screen = oldScreen,
                            oldStack = oldStack,
                            newStack = newStack,
                            animationPhase = ScreenAnimationPhase.EXIT,
                            isInitial = false,
                            isAnimating = true
                        )
                    )
                }
                put(
                    newScreen.screenKey,
                    AnimationItem(
                        screen = newScreen,
                        oldStack = oldStack,
                        newStack = newStack,
                        animationPhase = ScreenAnimationPhase.ENTER,
                        isInitial = false,
                        isAnimating = true
                    )
                )
            }
        }
    }
}

/**
 * Represents the lifecycle state of a screen during transition.
 * Defines what is happening to THIS specific screen.
 */
enum class ScreenAnimationPhase {
    /** Screen is appearing/entering composition */
    ENTER,

    /** Screen is disappearing/exiting composition */
    EXIT,

    /** No animation - screen is static */
    IDLE;

    val isExit: Boolean
        get() = this == EXIT

    val isEnter: Boolean
        get() = this == ENTER
}

/**
 * Represents a screen with its animation state
 */
data class AnimationItem(
    val screen: Screen,
    val oldStack: List<Screen>,
    val newStack: List<Screen>,
    val animationPhase: ScreenAnimationPhase,
    // True when this item is shown
    val isInitial: Boolean,
    val isAnimating: Boolean
) {
    init {
        assert(!(animationPhase == ScreenAnimationPhase.IDLE && isAnimating))
        assert(!(isInitial && animationPhase != ScreenAnimationPhase.IDLE))
    }
}
