package com.github.terrakok.modo.sample.playground.animation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.Screen

/**
 * Context containing all information needed for stack animation.
 *
 * @param screen the screen being animated
 * @param oldStack the previous stack of screens (empty if this is the first stack)
 * @param newStack the new stack of screens (empty if stack is being destroyed)
 * @param direction the animation direction
 * @param isInitial true if this is the initial screen (animation may be skipped)
 */
data class StackAnimationContext(
    val screen: Screen,
    val oldStack: List<Screen>,
    val newStack: List<Screen>,
    val direction: ScreenAnimationPhase,
    val isInitial: Boolean,
)

/**
 * Animates a screen in the given [ScreenAnimationPhase].
 *
 * Similar to Decompose's StackAnimator, this interface allows custom animations
 * to be applied to screen transitions. The animator receives progress and applies
 * appropriate modifiers to the content.
 */
fun interface StackAnimator {

    /**
     * Animates screen [content] based on the provided [progress] and [context].
     *
     * @param progress Animation progress from 0f (start) to 1f (end). Can be controlled manually for predictive gestures.
     * @param context the [StackAnimationContext] containing all animation information (screen, states, direction, etc.)
     * @param content the composable content of the screen being animated. It receives a [Modifier] to apply animations.
     */
    @Composable
    operator fun invoke(
        progress: Float,
        context: StackAnimationContext,
        content: @Composable (Modifier) -> Unit,
    )
}

/**
 * Creates a [StackAnimator] from a frame rendering lambda.
 *
 * @param frame renders the `content` using the provided `progress` and [StackAnimationContext]. Called for every animation frame.
 * The `progress` argument changes from 0f to 1f during the animation.
 * The `context` argument contains all animation information about the current screen transition and can be used to customize the animation.
 */
fun stackAnimator(
    frame: @Composable (progress: Float, context: StackAnimationContext, content: @Composable (Modifier) -> Unit) -> Unit,
): StackAnimator = StackAnimator(frame)

/**
 * Combines (merges) the receiver [StackAnimator] with the [other] [StackAnimator].
 * Both animators will be applied with the same progress, and the modifiers will be chained.
 */
operator fun StackAnimator.plus(other: StackAnimator): StackAnimator =
    StackAnimator { progress, context, content ->
        this(progress, context) { firstModifier ->
            other(progress, context) { secondModifier ->
                content(firstModifier.then(secondModifier))
            }
        }
    }
