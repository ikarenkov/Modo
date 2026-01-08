package com.github.terrakok.modo.sample.playground.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Represents the direction of stack navigation/animation.
 * Defines the direction of THE STACK movement (push vs pop).
 *
 * This is a helper concept used by animation helpers to determine
 * directional behavior, separate from the core [ScreenAnimationPhase]
 * which describes the screen's lifecycle state.
 */
enum class AnimationDirection {
    /** Navigating forward - pushing new screen */
    FORWARD,

    /** Navigating backward - popping or returning to previous screen */
    BACKWARD,

    /** No navigation */
    IDLE
}

/**
 * Remembers the animation direction for this transition.
 * Computed once and cached for the animation duration.
 *
 * This is a performance-optimized helper that calculates the navigation direction
 * by checking if the screen is in old/new stacks (O(n) operation) only once,
 * then caches the result for the entire animation.
 *
 * @return [AnimationDirection] indicating stack navigation direction:
 *   - [AnimationDirection.FORWARD] when pushing a new screen
 *   - [AnimationDirection.BACKWARD] when popping or returning to previous screen
 *   - [AnimationDirection.IDLE] when no navigation is happening
 */
@Composable
fun rememberAnimationDirection(context: StackAnimationContext): AnimationDirection {
    return remember(context.screen, context.oldStack, context.newStack, context.direction) {
        when (context.direction) {
            ScreenAnimationPhase.ENTER -> {
                if (context.screen in context.oldStack) {
                    AnimationDirection.BACKWARD  // Returning to previous screen
                } else {
                    AnimationDirection.FORWARD   // New screen appearing
                }
            }
            ScreenAnimationPhase.EXIT -> {
                if (context.screen !in context.newStack) {
                    AnimationDirection.BACKWARD  // Being removed/popped
                } else {
                    AnimationDirection.FORWARD   // Being pushed down
                }
            }
            ScreenAnimationPhase.IDLE -> AnimationDirection.IDLE
        }
    }
}

/**
 * Remembers direction-aware animation progress.
 *
 * Converts standard progress (0→1) into a directional progress value that indicates
 * both animation completion and navigation direction. This is useful for positional
 * animations like slides, parallax, or any animation that needs different behavior
 * for push vs pop.
 *
 * @param context The animation context
 * @param progress Standard animation progress (0f = start, 1f = end)
 * @return Direction-aware progress where:
 *   - **0f** = neutral/complete position (screen fully visible)
 *   - **1f** = forward direction endpoint (push/new screen moves from right)
 *   - **-1f** = backward direction endpoint (pop/returning moves from left)
 *
 * **Progress mapping examples:**
 * - Push new screen: progress 0→1 becomes directionalProgress 1→0 (slides in from right)
 * - Pop to previous: progress 0→1 becomes directionalProgress -1→0 (slides in from left)
 * - Exit during push: progress 0→1 becomes directionalProgress 0→-1 (slides out to left)
 * - Exit during pop: progress 0→1 becomes directionalProgress 0→1 (slides out to right)
 *
 * **Usage examples:**
 * ```kotlin
 * // Slide animation
 * val offset = rememberDirectionalProgress(context, progress)
 * Modifier.offsetXFactor(offset)
 *
 * // Rotation based on direction
 * val rotation = rememberDirectionalProgress(context, progress) * 45f
 * Modifier.graphicsLayer { rotationY = rotation }
 * ```
 */
@Composable
fun rememberDirectionalProgress(
    context: StackAnimationContext,
    progress: Float
): Float {
    val direction = rememberAnimationDirection(context)

    return remember(progress, context.direction, direction) {
        when {
            // -1 → 0
            context.direction == ScreenAnimationPhase.ENTER && direction == AnimationDirection.BACKWARD -> progress - 1f
            // 1 → 0
            context.direction == ScreenAnimationPhase.ENTER -> 1f - progress
            // 0 → 1
            context.direction == ScreenAnimationPhase.EXIT && direction == AnimationDirection.BACKWARD -> progress
            // 0 → -1
            context.direction == ScreenAnimationPhase.EXIT -> -progress
            else -> 0f
        }
    }
}
