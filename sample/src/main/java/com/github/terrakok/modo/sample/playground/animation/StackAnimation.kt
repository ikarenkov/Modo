package com.github.terrakok.modo.sample.playground.animation

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.SaveableContent
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.animation.StackTransitionType
import com.github.terrakok.modo.animation.calculateStackTransitionType
import com.github.terrakok.modo.model.lifecycleDependency
import com.github.terrakok.modo.sample.logs.logcat
import com.github.terrakok.modo.stack.StackScreenNew
import com.github.terrakok.modo.stack.StackState
import com.github.terrakok.modo.stack.dispatch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import logcat.logcat

// FIXME: when navigate forward while predictive back is running there is no animation, just jump to current state
/**
 * Implementation that relies on rememberAnimationItems and changes input stack.
 * There are no problem when back executed, but there are bugs when back is cancelled several times (blank screen, ignoring input and so on).
 */
@OptIn(ExperimentalModoApi::class)
@Composable
fun StackScreenNew.PredictiveBackStackAnimationPOC(
    modifier: Modifier = Modifier,
    animator: StackAnimator = fade() + slide(),
    animationSpec: FiniteAnimationSpec<Float> = tween(durationMillis = 1500),
    waitForAnimationCompletion: Boolean = true,
    predictiveBackDesiredStack: (List<Screen>) -> List<Screen> = {
        it.dropLast(1)
    },
    onBack: (() -> Unit)? = {
        dispatch { StackState(predictiveBackDesiredStack(it.stack)) }
    },
    content: @Composable (Screen) -> Unit = { it.SaveableContent(manualResumePause = true) }
) {
    val stack = navigationState.stack
    val predictiveBackDesiredStack: MutableState<List<Screen>?> = remember { mutableStateOf(null) }
    val predictiveProgressState = remember { mutableFloatStateOf(0f) }
    val predictiveBackAnimationItems = remember { mutableStateOf<List<AnimationItem>?>(null) }

    val autoAnimationScreensState = rememberAnimationItems(
        navigationState.stack,
        waitForAnimationCompletion = waitForAnimationCompletion,
    )
    val autoAnimationState = autoLaunchAnimation(
        animationScreenItems = autoAnimationScreensState,
        animationSpec = animationSpec,
    )

    var predictiveAnimationJob: Job? by remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope()
    PredictiveBackCallbacks(
        enabled = stack.size > 1,
        onBackStarted = {
            logcat("PredictiveBackCallbacks") { "onBackStarted, $it" }
            predictiveAnimationJob?.cancel()
            predictiveBackDesiredStack.value = predictiveBackDesiredStack(navigationState.stack)
            predictiveBackAnimationItems.value = predictiveBackDesiredStack.value?.let { newStack ->
                calculateStackAnimationItems(
                    oldStack = stack,
                    stack = newStack
                )
            }
            predictiveProgressState.floatValue = it.progress
        },
        onBackProgressed = {
            predictiveAnimationJob?.cancel()
            logcat("PredictiveBackCallbacks") { "onBackProgressed, $it" }
            predictiveProgressState.floatValue = it.progress
        },
        onBackPressed = {
            logcat("PredictiveBackCallbacks") { "onBackPressed" }
            predictiveAnimationJob = coroutineScope.launch {
                try {
                    // Doing back before to let autoanimation recalculate items
                    onBack?.invoke()
                    animate(
                        initialValue = predictiveProgressState.floatValue,
                        targetValue = 1f,
                        animationSpec = animationSpec
                    ) { value, _ ->
                        predictiveProgressState.floatValue = value
                    }
                } finally {
                    predictiveBackDesiredStack.value = null
                    predictiveBackAnimationItems.value = null
                    // TODO: cancel autoanimation. Test it by running autoanimation for 3s, and this animation for 1 seccond
                    autoAnimationScreensState.value = calculateStackAnimationItems(navigationState.stack, navigationState.stack)
                }
            }
            // TODO: can simply use predictiveBackDesiredStack to calculete desired idle stack
//            predictiveBackAnimationItems.value = predictiveBackAnimationItems.value!!.removeExitingAndMarkIdle()
//            predictiveBackDesiredStack.value = null
        },
        onBackCancelled = {
            logcat("PredictiveBackCallbacks") { "onBackCancelled" }
            predictiveAnimationJob = coroutineScope.launch {
                try {
                    animate(
                        initialValue = predictiveProgressState.floatValue,
                        targetValue = 0f,
                        animationSpec = animationSpec
                    ) { value, _ ->
                        logcat("PredictiveBackCallbacks") { "close animation progressed $value" }
                        predictiveProgressState.floatValue = value
                    }
                } catch (e: CancellationException) {
                    logcat("PredictiveBackCallbacks") { "Animation cancelled" }
                    throw e
                } finally {
                    logcat("PredictiveBackCallbacks") { "Animation finally block - clearing state" }
                    predictiveProgressState.floatValue = 0f
                }
            }
        }
    )
    // TODO: make a sample of stack where multiple items, stack is Idle and dialog is entering

    LaunchedEffect(predictiveBackAnimationItems.value) {
        logcat("StackAnimation") { "predictiveBackAnimationItems: ${predictiveBackAnimationItems.value}" }
    }

    val actualItems = remember {
        derivedStateOf {
            predictiveBackAnimationItems.value ?: autoAnimationScreensState.value
        }
    }

    val actualAnimationProgress = remember {
        derivedStateOf {
            if (predictiveBackAnimationItems.value != null) {
                logcat("StackAnimation") { "actualAnimationProgress predictiveBackAnimationProgress: ${predictiveProgressState.floatValue}" }
                predictiveProgressState.floatValue
            } else {
                logcat("StackAnimation") { "actualAnimationProgress autoAnimationProgress: ${autoAnimationState.floatValue}" }
                autoAnimationState.floatValue
            }
        }
    }

    RenderAnimationItems(modifier, actualItems.value, animator, actualAnimationProgress.value, content)
}

// FIXME: when navigate forward while predictive back is running there is no animation, just jump to current state
/**
 * Implementation that relies on rememberAnimationItems and changes input stack.
 * There are no problem when back executed, but there are bugs when back is cancelled several times (blank screen, ignoring input and so on).
 */
@OptIn(ExperimentalModoApi::class)
@Composable
fun StackScreenNew.PredictiveBackStackAnimationPOCV2(
    modifier: Modifier = Modifier,
    animator: StackAnimator = fade() + slide(),
    animationSpec: FiniteAnimationSpec<Float> = tween(durationMillis = 1500),
    waitForAnimationCompletion: Boolean = true,
    predictiveBackDesiredStack: (List<Screen>) -> List<Screen> = {
        it.dropLast(1)
    },
    onBack: (() -> Unit)? = {
        dispatch { StackState(predictiveBackDesiredStack(it.stack)) }
    },
    content: @Composable (Screen) -> Unit = { it.SaveableContent(manualResumePause = true) }
) {
    val stack = navigationState.stack
    val predictiveBackDesiredStack: MutableState<List<Screen>?> = remember { mutableStateOf(null) }
    val predictiveProgressState = remember { mutableFloatStateOf(0f) }
    val predictiveBackItemsState = remember { mutableStateOf<List<AnimationItem>?>(null) }

    var predictiveAnimationJob: Job? by remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope()
    PredictiveBackCallbacks(
        enabled = stack.size > 1,
        onBackStarted = {
            logcat("PredictiveBackCallbacks") { "onBackStarted, $it" }
            predictiveAnimationJob?.cancel()
            predictiveBackDesiredStack.value = predictiveBackDesiredStack(navigationState.stack)
            predictiveBackItemsState.value = predictiveBackDesiredStack.value?.let { newStack ->
                calculateStackAnimationItems(
                    oldStack = stack,
                    stack = newStack
                )
            }
            predictiveProgressState.floatValue = it.progress
        },
        onBackProgressed = {
            predictiveAnimationJob?.cancel()
            logcat("PredictiveBackCallbacks") { "onBackProgressed, $it" }
            predictiveProgressState.floatValue = it.progress
        },
        onBackPressed = {
            logcat("PredictiveBackCallbacks") { "onBackPressed" }
            predictiveAnimationJob = coroutineScope.launch {
                try {
                    // Doing back before to let autoanimation recalculate items
                    onBack?.invoke()
                    animate(
                        initialValue = predictiveProgressState.floatValue,
                        targetValue = 1f,
                        animationSpec = animationSpec
                    ) { value, _ ->
                        predictiveProgressState.floatValue = value
                    }
                } finally {
                    predictiveBackDesiredStack.value = null
                    predictiveBackItemsState.value = null
                    // TODO: cancel autoanimation. Test it by running autoanimation for 3s, and this animation for 1 seccond
                }
            }
            // TODO: can simply use predictiveBackDesiredStack to calculete desired idle stack
//            predictiveBackAnimationItems.value = predictiveBackAnimationItems.value!!.removeExitingAndMarkIdle()
//            predictiveBackDesiredStack.value = null
        },
        onBackCancelled = {
            logcat("PredictiveBackCallbacks") { "onBackCancelled" }
            predictiveAnimationJob = coroutineScope.launch {
                try {
                    animate(
                        initialValue = predictiveProgressState.floatValue,
                        targetValue = 0f,
                        animationSpec = animationSpec
                    ) { value, _ ->
                        logcat("PredictiveBackCallbacks") { "close animation progressed $value" }
                        predictiveProgressState.floatValue = value
                    }
                } catch (e: CancellationException) {
                    logcat("PredictiveBackCallbacks") { "Animation cancelled" }
                    throw e
                } finally {
                    logcat("PredictiveBackCallbacks") { "Animation finally block - clearing state" }
                    predictiveProgressState.floatValue = 0f
//                    predictiveBackDesiredStack.value = null
//                    predictiveBackItemsState.value = null
                }
            }
        }
    )
    // TODO: make a sample of stack where multiple items, stack is Idle and dialog is entering

    LaunchedEffect(predictiveBackItemsState.value) {
        logcat("StackAnimation") { "predictiveBackAnimationItems: ${predictiveBackItemsState.value}" }
    }

    val predictiveBackItems = predictiveBackItemsState.value
    val (screenItems, progress) = if (predictiveBackItems != null) {
        predictiveBackItems to predictiveProgressState.floatValue
    } else {
        // We keep default behavior for case when we are not in predictive back.
        // Putting it into different if branch also helps to display current state without any animation,
        // because it resets since it leaves composition.
        val autoAnimationScreensState = rememberAnimationItems(
            navigationState.stack,
            waitForAnimationCompletion = waitForAnimationCompletion,
        )
        val animationProgressState = autoLaunchAnimation(
            animationScreenItems = autoAnimationScreensState,
            animationSpec = animationSpec,
        )
        autoAnimationScreensState.value to animationProgressState.floatValue
    }
    RenderAnimationItems(modifier, screenItems, animator, progress, content)
}

// TODO: make a sample of stack where multiple items, stack is Idle and dialog is entering
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
    val animationScreenItems = rememberAnimationItems(
        stack = stack,
        waitForAnimationCompletion = waitForAnimationCompletion,
    )

    val animationProgressState = autoLaunchAnimation(
        animationScreenItems = animationScreenItems,
        animationSpec = animationSpec,
    )

    RenderAnimationItems(modifier, animationScreenItems.value, animator, animationProgressState.floatValue, content)
}

@Composable
private fun autoLaunchAnimation(
    animationScreenItems: MutableState<List<AnimationItem>>,
    animationSpec: FiniteAnimationSpec<Float>,
): FloatState {
    // Single animation state for all screens in the transition
    val hasAnimatingScreens by remember {
        derivedStateOf {
            animationScreenItems.value.any { it.isAnimating }
        }
    }
    // Shared animation progress reused by every screen rendered in this StackAnimation
    val animationProgressState = remember { mutableFloatStateOf(1f) }
    // Tracks whether a coroutine-driven animation is currently running
    val animationRunningState = remember { mutableStateOf(false) }
    if (hasAnimatingScreens && !animationRunningState.value) {
        // Next composition should render the new screens at the start of the animation
        animationProgressState.floatValue = 0f
        animationRunningState.value = true
    }
    LaunchedEffect(hasAnimatingScreens) {
        logcat("StackAnimation") {
            "autoLaunchAnimation, hasAnimatingScreens: $hasAnimatingScreens, animationRunningState: ${animationRunningState.value}"
        }
        if (hasAnimatingScreens) {
            try {
                // Notify all screens that animation started
                animationScreenItems.value.forEach { handleAnimationStart(it) }

                // Drive a single transition coroutine that updates shared progress 0f -> 1f
                animate(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = animationSpec
                ) { value, _ ->
                    animationProgressState.floatValue = value
                }
                animationProgressState.floatValue = 1f

                // Animation finished - notify screens and cleanup
                animationScreenItems.value.forEach { handleAnimationFinish(it) }

                // Update animation items: remove exiting, mark others as not animating
                animationScreenItems.value = animationScreenItems.value.removeExitingAndMarkIdle()
            } finally {
                // Handle cancellation if needed
                if (currentCoroutineContext().job.isCancelled) {
                    // TODO: test interruption. Maybe we needt it in case of running animation and rotation of screen, we need to mark screen as shown
//                    animationScreens.values.handleAnimationFinish()
//                    animationDialogs.values.handleAnimationFinish()
                }
                animationRunningState.value = false
            }
        } else {
            animationScreenItems.value.forEach { item ->
                if (item.isInitial) {
                    // It is okay to call it multiple times, f.e. if rotate screen
                    item.screen.lifecycleDependency()?.showTransitionFinished()
                }
            }
            // Idle state renders as fully visible
            animationProgressState.floatValue = 1f
            animationRunningState.value = false
        }
    }
    return animationProgressState
}

@Composable
private fun RenderAnimationItems(
    modifier: Modifier,
    animationScreens: List<AnimationItem>,
    animator: StackAnimator,
    animationProgress: Float,
    content: @Composable ((Screen) -> Unit)
) {
    Box(modifier = modifier) {
        animationScreens.forEach { item ->
            key(item.screen.screenKey) {
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
private fun List<AnimationItem>.removeExitingAndMarkIdle(): List<AnimationItem> {
    return buildList(size) {
        this@removeExitingAndMarkIdle.forEach { item ->
            if (!item.animationPhase.isExit) {
                add(
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

/**
 * For cases when we cancel back navigation during predictive back.
 * Removes entering screens and marks remaining screens as idle (not animating).
 * Also clears oldStack to prevent memory leaks.
 */
private fun List<AnimationItem>.removeEnteringAndMarkIdle(): List<AnimationItem> {
    return buildList(size) {
        this@removeEnteringAndMarkIdle.forEach { item ->
            if (item.animationPhase.isExit) {
                add(
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
): List<AnimationItem> {
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
private fun rememberAnimationItemsSimple(
    stack: List<Screen>
): MutableState<List<AnimationItem>> {
    val animationItems = remember { mutableStateOf<List<AnimationItem>>(emptyList()) }
    var currentStack by remember { mutableStateOf<List<Screen>>(emptyList()) }

    // Detect stack changes and update animation items immediately
    if (stack != currentStack) {
        val oldStack = currentStack
        currentStack = stack

        animationItems.value = calculateStackAnimationItems(oldStack, stack)
    }

    return animationItems
}

@OptIn(ExperimentalModoApi::class)
private fun calculateStackAnimationItems(
    oldStack: List<Screen>,
    stack: List<Screen>
): List<AnimationItem> {
    val oldDialogsStack = oldStack.takeLastWhile { it is DialogScreen }
    val oldScreensStack = oldStack.subList(0, oldStack.size - oldDialogsStack.size)

    val dialogsStack = stack.takeLastWhile { it is DialogScreen }
    val screensStack = stack.subList(0, stack.size - dialogsStack.size)

    val screenAnimationItems = processStackTransition(
        oldStack = oldScreensStack,
        newStack = screensStack,
        isDialogs = false
    )

    val dialogsAnimationItems = processStackTransition(
        oldStack = oldDialogsStack,
        newStack = dialogsStack,
        isDialogs = true
    )
    return screenAnimationItems + dialogsAnimationItems
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
private fun rememberAnimationItemsQueued(
    stack: List<Screen>,
): MutableState<List<AnimationItem>> {
    val animationItems = remember { mutableStateOf<List<AnimationItem>>(emptyList()) }
    var currentStack by remember { mutableStateOf<List<Screen>>(emptyList()) }
    var visibleStack by remember { mutableStateOf<List<Screen>>(emptyList()) }
    var pendingStack by remember { mutableStateOf<List<Screen>?>(null) }

    val hasAnimatingScreens by remember {
        derivedStateOf {
            animationItems.value.any { it.isAnimating }
        }
    }

    // Detect stack changes
    if (stack != currentStack) {
        if (hasAnimatingScreens) {
            // Animation in progress - queue this change
            pendingStack = stack
            currentStack = stack
        } else {
            // No animation - process immediately
            val oldStack = visibleStack
            currentStack = stack
            visibleStack = stack

            animationItems.value = calculateStackAnimationItems(oldStack, stack)
        }
    }

    // Process queued stack change when animations finish
    LaunchedEffect(hasAnimatingScreens, pendingStack) {
        if (!hasAnimatingScreens && pendingStack != null) {
            val queuedStack = pendingStack!!
            pendingStack = null

            val oldStack = visibleStack
            visibleStack = queuedStack

            animationItems.value = calculateStackAnimationItems(
                oldStack = oldStack,
                stack = queuedStack,
            )
        }
    }

    return animationItems
}

/**
 * Remembers animation items with optional queueing.
 * Delegates to either simple or queued implementation based on parameter.
 *
 * @param waitForAnimationCompletion if true, queues navigation changes during animation;
 * if false, immediately processes changes (may interrupt current animation)
 */
@OptIn(ExperimentalModoApi::class)
@Composable
fun rememberAnimationItems(
    stack: List<Screen>,
    waitForAnimationCompletion: Boolean = true
): MutableState<List<AnimationItem>> {
    return if (waitForAnimationCompletion) {
        rememberAnimationItemsQueued(stack = stack)
    } else {
        rememberAnimationItemsSimple(stack = stack)
    }
}

@OptIn(ExperimentalModoApi::class)
@Composable
fun StackState.rememberDialogsAnimationItems(
    isPredictiveBack: Boolean = false
): MutableState<List<AnimationItem>> {
    val filteredNewStack = remember(this) {
        stack.takeLastWhile { it is DialogScreen }
    }
    val currentScreen = filteredNewStack.lastOrNull()

    // Track animation items (screens currently being rendered/animated)
    val animationItems = remember { mutableStateOf<List<AnimationItem>>(emptyList()) }
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
            newScreen = currentScreen,
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
): List<AnimationItem> {
    if (newScreen == null) {
        return if (oldScreen == null) {
            emptyList()
        } else {
            listOf(
                AnimationItem(
                    screen = oldScreen,
                    oldStack = emptyList(),
                    newStack = newStack,
                    animationPhase = ScreenAnimationPhase.EXIT,
                    isInitial = false,
                    isAnimating = true,
                )
            )
        }
    }

    return when (transitionType) {
        StackTransitionType.Idle -> {
            // No animation or initial render
            listOf(
                AnimationItem(
                    screen = newScreen,
                    oldStack = oldStack,
                    newStack = newStack,
                    animationPhase = ScreenAnimationPhase.IDLE,
                    isInitial = oldScreen == null,
                    isAnimating = false,
                )
            )
        }

        StackTransitionType.Pop -> {
            // Going back: previous screen enters from back, current exits to front
            buildList {
                add(
                    AnimationItem(
                        screen = newScreen,
                        oldStack = oldStack,
                        newStack = newStack,
                        animationPhase = ScreenAnimationPhase.ENTER,
                        isInitial = false,
                        isAnimating = true,
                    )
                )
                if (oldScreen != null) {
                    add(
                        AnimationItem(
                            screen = oldScreen,
                            oldStack = oldStack,
                            newStack = newStack,
                            animationPhase = ScreenAnimationPhase.EXIT,
                            isInitial = false,
                            isAnimating = true,
                        )
                    )
                }
            }
        }

        StackTransitionType.Push,
        StackTransitionType.Replace -> {
            // Going forward: new screen enters from front, old exits to back
            buildList {
                if (oldScreen != null && oldScreen.screenKey != newScreen.screenKey) {
                    add(
                        AnimationItem(
                            screen = oldScreen,
                            oldStack = oldStack,
                            newStack = newStack,
                            animationPhase = ScreenAnimationPhase.EXIT,
                            isInitial = false,
                            isAnimating = true,
                        )
                    )
                }
                add(
                    AnimationItem(
                        screen = newScreen,
                        oldStack = oldStack,
                        newStack = newStack,
                        animationPhase = ScreenAnimationPhase.ENTER,
                        isInitial = false,
                        isAnimating = true,
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
    val isAnimating: Boolean,
) {
    init {
        assert(!(animationPhase == ScreenAnimationPhase.IDLE && isAnimating))
        assert(!(isInitial && animationPhase != ScreenAnimationPhase.IDLE))
    }
}
