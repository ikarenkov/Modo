package com.github.terrakok.modo.sample.playground.animation

import androidx.activity.BackEventCompat
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.SaveableContent
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.sample.logs.logcat
import com.github.terrakok.modo.stack.StackScreenNew
import com.github.terrakok.modo.stack.StackState
import com.github.terrakok.modo.stack.dispatch
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import logcat.logcat

/**
 * State of the predictive back gesture containing touch and edge information.
 *
 * @param touchX X coordinate of the touch point
 * @param touchY Y coordinate of the touch point
 * @param swipeEdge The edge from which the swipe started (EDGE_LEFT or EDGE_RIGHT)
 */
data class PredictiveBackState(
    val touchX: Float,
    val touchY: Float,
    val swipeEdge: SwipeEdge,
) {
    enum class SwipeEdge {
        LEFT,
        RIGHT,
        UNKNOWN
    }

    companion object {
        val Default = PredictiveBackState(
            touchX = 0f,
            touchY = 0f,
            swipeEdge = SwipeEdge.UNKNOWN
        )

        fun fromBackEvent(backEvent: BackEventCompat): PredictiveBackState = PredictiveBackState(
            touchX = backEvent.touchX,
            touchY = backEvent.touchY,
            swipeEdge = when (backEvent.swipeEdge) {
                BackEventCompat.EDGE_LEFT -> SwipeEdge.LEFT
                BackEventCompat.EDGE_RIGHT -> SwipeEdge.RIGHT
                else -> SwipeEdge.UNKNOWN
            }
        )
    }
}

/**
 * Animator for predictive back gesture with 2-stage animation to match Material guidelines.
 *
 * Stage 1 (gesture): User drags back, [gestureProgress] moves from 0 to ~1
 * Stage 2 (finish): When back is confirmed/cancelled, [finishProgress] animates from 0 to 1
 */
fun interface PredictiveBackAnimator {

    /**
     * Animates screen content based on two progress values and back state.
     *
     * @param gestureProgress Progress of the back gesture (0-1). Controlled by user's finger.
     * @param finishProgress Progress of the finish animation (0-1). Animates after gesture ends.
     * @param backState State containing touch position and swipe edge information.
     * @param context Animation context containing screen info and direction.
     * @param content The composable content to animate. Receives a Modifier to apply animations.
     */
    @Composable
    operator fun invoke(
        gestureProgress: Float,
        finishProgress: Float,
        backState: PredictiveBackState,
        context: StackAnimationContext,
        content: @Composable (Modifier) -> Unit,
    )
}

/**
 * Renders a single screen with predictive back 2-stage animation.
 */
@Composable
private fun PredictiveBackAnimatedScreen(
    item: AnimationItem,
    animator: PredictiveBackAnimator,
    gestureProgress: Float,
    finishProgress: Float,
    backState: PredictiveBackState,
    content: @Composable () -> Unit
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

    animator(
        gestureProgress = gestureProgress,
        finishProgress = finishProgress,
        backState = backState,
        context = context
    ) { modifier ->
        Box(modifier = modifier) {
            content()
        }
    }
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
    predictiveBackAnimator: PredictiveBackAnimator = fullScreenSurfacePredictiveBackAnimator(),
    animationSpec: FiniteAnimationSpec<Float> = tween(durationMillis = 1500),
    predictiveGestureAnimationSpec: FiniteAnimationSpec<Float> = spring(),
    predictiveFinalAnimationSpec: FiniteAnimationSpec<Float> = predictiveGestureAnimationSpec,
    waitForAnimationCompletion: Boolean = true,
    predictiveBackDesiredStack: (List<Screen>) -> List<Screen> = {
        it.dropLast(1)
    },
    onBack: (() -> Unit)? = {
        dispatch { StackState(predictiveBackDesiredStack(it.stack)) }
    },
    content: @Composable (Screen) -> Unit = { it.SaveableContent(manualResumePause = true) }
) {
    val predictiveBackDesiredStack: MutableState<List<Screen>?> = remember { mutableStateOf(null) }

    // 2-stage animation: gestureProgress for user drag, finishProgress for completion animation
    val gestureProgressAnimatable = remember {
        Animatable(0f).apply {
            updateBounds(0f, 1f)
        }
    }
    val finishProgressAnimatable = remember {
        Animatable(0f).apply {
            updateBounds(0f, 1f)
        }
    }
    val predictiveBackItemsState = remember { mutableStateOf<List<AnimationItem>?>(null) }

    val backState = remember { mutableStateOf(PredictiveBackState.Default) }

    var predictiveAnimationJob: Job? by remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope()
    PredictiveBackCallbacks(
        enabled = navigationState.stack.size > 1,
        onBackStarted = { backEvent ->
            logcat("PredictiveBackCallbacks") { "onBackStarted, $backEvent" }
            predictiveAnimationJob?.cancel()
            predictiveBackDesiredStack.value = predictiveBackDesiredStack(navigationState.stack)
            predictiveBackItemsState.value = predictiveBackDesiredStack.value?.let { newStack ->
                calculateStackAnimationItems(
                    oldStack = navigationState.stack,
                    stack = newStack
                ).also {
                    logcat("Predictive back animation items") { it.joinToString { (it.screen to it.animationPhase).toString() } }
                }
            }
            // Update back state from event
            backState.value = PredictiveBackState.fromBackEvent(backEvent)
            coroutineScope.launch {
                // Reset both progresses at start
                gestureProgressAnimatable.snapTo(0f)
                finishProgressAnimatable.snapTo(0f)
                gestureProgressAnimatable.snapTo(backEvent.progress)
            }
        },
        onBackProgressed = { backEvent ->
            predictiveAnimationJob?.cancel()
            logcat("PredictiveBackCallbacks") { "onBackProgressed, $backEvent" }
            // Update back state from event
            backState.value = PredictiveBackState.fromBackEvent(backEvent)
            coroutineScope.launch {
                // Only gesture progress updates during drag
                gestureProgressAnimatable.snapTo(
                    backEvent.progress,
                )
            }
        },
        onBackPressed = {
            logcat("PredictiveBackCallbacks") { "onBackPressed" }
            predictiveAnimationJob = coroutineScope.launch {
                // Doing back before to let autoanimation recalculate items
                onBack?.invoke()
                // Animate both progresses to 1 in parallel
                val gestureAnimationJob = launch {
                    gestureProgressAnimatable.animateTo(
                        targetValue = 1f,
                        animationSpec = predictiveGestureAnimationSpec
                    )
                }
                val finishAnimationJob = launch {
                    finishProgressAnimatable.animateTo(
                        targetValue = 1f,
                        animationSpec = predictiveFinalAnimationSpec
                    )
                }
                joinAll(gestureAnimationJob, finishAnimationJob)
                predictiveBackDesiredStack.value = null
                predictiveBackItemsState.value = null
                // Reset for next gesture
                gestureProgressAnimatable.snapTo(0f)
                finishProgressAnimatable.snapTo(0f)
            }
        },
        onBackCancelled = {
            logcat("PredictiveBackCallbacks") { "onBackCancelled" }
            predictiveAnimationJob = coroutineScope.launch {
                // Animate gesture progress back to 0 (if not already there)
                if (gestureProgressAnimatable.value > 0f) {
                    gestureProgressAnimatable.animateTo(
                        targetValue = 0f,
                        animationSpec = predictiveGestureAnimationSpec
                    )
                }
                logcat("PredictiveBackCallbacks") { "Animation finished - clearing state" }
                predictiveBackDesiredStack.value = null
                predictiveBackItemsState.value = null
            }
        }
    )

    RenderAnimationScreens(
        predictiveBackItemsState = predictiveBackItemsState,
        gestureProgressAnimatable = gestureProgressAnimatable,
        finishProgressAnimatable = finishProgressAnimatable,
        backState = backState,
        state = composeState,
        waitForAnimationCompletion = waitForAnimationCompletion,
        animationSpec = animationSpec,
        animator = animator,
        predictiveBackAnimator = predictiveBackAnimator,
        modifier = modifier,
        content = content
    )
}

@Composable
private fun RenderAnimationScreens(
    predictiveBackItemsState: MutableState<List<AnimationItem>?>,
    gestureProgressAnimatable: Animatable<Float, *>,
    finishProgressAnimatable: Animatable<Float, *>,
    backState: State<PredictiveBackState>,
    state: State<StackState>,
    waitForAnimationCompletion: Boolean,
    animationSpec: FiniteAnimationSpec<Float>,
    animator: StackAnimator,
    predictiveBackAnimator: PredictiveBackAnimator,
    modifier: Modifier = Modifier,
    content: @Composable (Screen) -> Unit = { it.SaveableContent(manualResumePause = true) }
) {
    LaunchedEffect(predictiveBackItemsState.value) {
        logcat("StackAnimation") { "predictiveBackAnimationItems: ${predictiveBackItemsState.value}" }
    }

    // Select items and progress based on predictive back state
    val predictiveBackItems = predictiveBackItemsState.value
    val isPredictiveBack = predictiveBackItems != null

    val (screenItems, autoProgress) = if (isPredictiveBack) {
        predictiveBackItems to 0f
    } else {
        // Move auto detection into separate if branch to cause IDLE state and no animation when predictive back is finished or canceled.
        val autoAnimationScreensState = rememberAnimationItems(
            stackStateState = state,
            waitForAnimationCompletion = waitForAnimationCompletion,
        )
        val autoAnimationProgressState = autoLaunchScreensAnimation(
            animationScreenItems = autoAnimationScreensState,
            animationSpec = animationSpec,
        )
        autoAnimationScreensState.value to autoAnimationProgressState.value
    }

    Box(modifier = modifier) {
        screenItems.forEach { item ->
            key(item.screen.screenKey) {
                // Each screen gets its own movable content instance
                // This preserves rememberSaveable state when switching between animation modes
                val movableScreenContent = remember {
                    movableContentOf {
                        content(item.screen)
                    }
                }

                if (isPredictiveBack) {
                    // 2-stage predictive back animation
                    PredictiveBackAnimatedScreen(
                        item = item,
                        animator = predictiveBackAnimator,
                        gestureProgress = gestureProgressAnimatable.value,
                        finishProgress = finishProgressAnimatable.value,
                        backState = backState.value,
                    ) {
                        movableScreenContent()
                    }
                } else {
                    // Regular auto animation
                    AnimatedScreen(
                        item = item,
                        animator = animator,
                        progress = autoProgress,
                    ) {
                        movableScreenContent()
                    }
                }
            }
        }
    }
}