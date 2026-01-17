package com.github.terrakok.modo.sample.playground.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.launch

//// FIXME: when navigate forward while predictive back is running there is no animation, just jump to current state
///**
// * Implementation that relies on rememberAnimationItems and changes input stack.
// * There are no problem when back executed, but there are bugs when back is cancelled several times (blank screen, ignoring input and so on).
// */
//@OptIn(ExperimentalModoApi::class)
//@Composable
//fun StackScreenNew.PredictiveBackStackAnimationPOC(
//    modifier: Modifier = Modifier,
//    animator: StackAnimator = fade() + slide(),
//    animationSpec: FiniteAnimationSpec<Float> = tween(durationMillis = 1500),
//    waitForAnimationCompletion: Boolean = true,
//    predictiveBackDesiredStack: (List<Screen>) -> List<Screen> = {
//        it.dropLast(1)
//    },
//    onBack: (() -> Unit)? = {
//        dispatch { StackState(predictiveBackDesiredStack(it.stack)) }
//    },
//    content: @Composable (Screen) -> Unit = { it.SaveableContent(manualResumePause = true) }
//) {
//    val navigationStateState = rememberUpdatedState(navigationState)
//    val stack by remember { derivedStateOf { navigationStateState.value.stack } }
//    val predictiveBackDesiredStack: MutableState<List<Screen>?> = remember { mutableStateOf(null) }
//    val predictiveProgressState = remember { mutableFloatStateOf(0f) }
//    val predictiveBackAnimationItems = remember { mutableStateOf<List<AnimationItem>?>(null) }
//
//    val autoAnimationScreensState = rememberAnimationItems(
//        stackStateState = navigationStateState,
//        waitForAnimationCompletion = waitForAnimationCompletion,
//    )
//    val autoAnimationState = autoLaunchScreensAnimation(
//        animationScreenItems = autoAnimationScreensState,
//        animationSpec = animationSpec,
//    )
//
//    var predictiveAnimationJob: Job? by remember { mutableStateOf(null) }
//    val coroutineScope = rememberCoroutineScope()
//    PredictiveBackCallbacks(
//        enabled = stack.size > 1,
//        onBackStarted = {
//            logcat("PredictiveBackCallbacks") { "onBackStarted, $it" }
//            predictiveAnimationJob?.cancel()
//            predictiveBackDesiredStack.value = predictiveBackDesiredStack(navigationStateState.value.stack)
//            predictiveBackAnimationItems.value = predictiveBackDesiredStack.value?.let { newStack ->
//                calculateStackAnimationItems(
//                    oldStack = stack,
//                    stack = newStack
//                )
//            }
//            predictiveProgressState.floatValue = it.progress
//        },
//        onBackProgressed = {
//            predictiveAnimationJob?.cancel()
//            logcat("PredictiveBackCallbacks") { "onBackProgressed, $it" }
//            predictiveProgressState.floatValue = it.progress
//        },
//        onBackPressed = {
//            logcat("PredictiveBackCallbacks") { "onBackPressed" }
//            predictiveAnimationJob = coroutineScope.launch {
//                try {
//                    // Doing back before to let autoanimation recalculate items
//                    onBack?.invoke()
//                    animate(
//                        initialValue = predictiveProgressState.floatValue,
//                        targetValue = 1f,
//                        animationSpec = animationSpec
//                    ) { value, _ ->
//                        predictiveProgressState.floatValue = value
//                    }
//                } finally {
//                    predictiveBackDesiredStack.value = null
//                    predictiveBackAnimationItems.value = null
//                    // TODO: cancel autoanimation. Test it by running autoanimation for 3s, and this animation for 1 seccond
//                    autoAnimationScreensState.value = calculateStackAnimationItems(navigationStateState.value.stack, navigationStateState.value.stack)
//                }
//            }
//            // TODO: can simply use predictiveBackDesiredStack to calculete desired idle stack
////            predictiveBackAnimationItems.value = predictiveBackAnimationItems.value!!.removeExitingAndMarkIdle()
////            predictiveBackDesiredStack.value = null
//        },
//        onBackCancelled = {
//            logcat("PredictiveBackCallbacks") { "onBackCancelled" }
//            predictiveAnimationJob = coroutineScope.launch {
//                try {
//                    animate(
//                        initialValue = predictiveProgressState.floatValue,
//                        targetValue = 0f,
//                        animationSpec = animationSpec
//                    ) { value, _ ->
//                        logcat("PredictiveBackCallbacks") { "close animation progressed $value" }
//                        predictiveProgressState.floatValue = value
//                    }
//                } catch (e: CancellationException) {
//                    logcat("PredictiveBackCallbacks") { "Animation cancelled" }
//                    throw e
//                } finally {
//                    logcat("PredictiveBackCallbacks") { "Animation finally block - clearing state" }
//                    predictiveProgressState.floatValue = 0f
//                }
//            }
//        }
//    )
//    // TODO: make a sample of stack where multiple items, stack is Idle and dialog is entering
//
//    LaunchedEffect(predictiveBackAnimationItems.value) {
//        logcat("StackAnimation") { "predictiveBackAnimationItems: ${predictiveBackAnimationItems.value}" }
//    }
//
//    val actualItems = remember {
//        derivedStateOf {
//            predictiveBackAnimationItems.value ?: autoAnimationScreensState.value
//        }
//    }
//
//    val actualAnimationProgress = remember {
//        derivedStateOf {
//            if (predictiveBackAnimationItems.value != null) {
//                logcat("StackAnimation") { "actualAnimationProgress predictiveBackAnimationProgress: ${predictiveProgressState.floatValue}" }
//                predictiveProgressState.floatValue
//            } else {
//                logcat("StackAnimation") { "actualAnimationProgress autoAnimationProgress: ${autoAnimationState.value}" }
//                autoAnimationState.value
//            }
//        }
//    }
//
//    RenderAnimationItems(modifier, actualItems.value, animator, actualAnimationProgress.value, content)
//}

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
    val predictiveBackDesiredStack: MutableState<List<Screen>?> = remember { mutableStateOf(null) }
    val predictiveProgressState = remember {
        Animatable(0f).apply {
            updateBounds(0f, 1f)
        }
    }
    val predictiveBackItemsState = remember { mutableStateOf<List<AnimationItem>?>(null) }

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
                )
            }
            coroutineScope.launch {
                predictiveProgressState.snapTo(backEvent.progress)
            }
        },
        onBackProgressed = { backEvent ->
            predictiveAnimationJob?.cancel()
            logcat("PredictiveBackCallbacks") { "onBackProgressed, $backEvent" }
            coroutineScope.launch {
                predictiveProgressState.snapTo(backEvent.progress)
            }
        },
        onBackPressed = {
            logcat("PredictiveBackCallbacks") { "onBackPressed" }
            predictiveAnimationJob = coroutineScope.launch {
                // Doing back before to let autoanimation recalculate items
                onBack?.invoke()
                predictiveProgressState.animateTo(
                    targetValue = 1f,
                    animationSpec = animationSpec
                )
                predictiveBackDesiredStack.value = null
                predictiveBackItemsState.value = null
                // TODO: cancel autoanimation. Test it by running autoanimation for 3s, and this animation for 1 seccond
            }
            // TODO: can simply use predictiveBackDesiredStack to calculete desired idle stack
//            predictiveBackAnimationItems.value = predictiveBackAnimationItems.value!!.removeExitingAndMarkIdle()
//            predictiveBackDesiredStack.value = null
        },
        onBackCancelled = {
            logcat("PredictiveBackCallbacks") { "onBackCancelled" }
            predictiveAnimationJob = coroutineScope.launch {
                predictiveProgressState.animateTo(
                    targetValue = 0f,
                    animationSpec = animationSpec
                )
                logcat("PredictiveBackCallbacks") { "Animation finally block - clearing state" }
                predictiveBackDesiredStack.value = null
                predictiveBackItemsState.value = null
            }
        }
    )
    // TODO: make a sample of stack where multiple items, stack is Idle and dialog is entering

    LaunchedEffect(predictiveBackItemsState.value) {
        logcat("StackAnimation") { "predictiveBackAnimationItems: ${predictiveBackItemsState.value}" }
    }

    val predictiveBackItems = predictiveBackItemsState.value
    val (screenItems, progress) = if (predictiveBackItems != null) {
        predictiveBackItems to predictiveProgressState.value
    } else {
        // We keep default behavior for case when we are not in predictive back.
        // Putting it into different if branch also helps to display current state without any animation,
        // because it resets since it leaves composition.
        val autoAnimationScreensState = rememberAnimationItems(
            stackStateState = composeState,
            waitForAnimationCompletion = waitForAnimationCompletion,
        )
        val animationProgressState = autoLaunchScreensAnimation(
            animationScreenItems = autoAnimationScreensState,
            animationSpec = animationSpec,
        )
        autoAnimationScreensState.value to animationProgressState.value
    }
    RenderAnimationItems(modifier, screenItems, animator, progress, content)
}