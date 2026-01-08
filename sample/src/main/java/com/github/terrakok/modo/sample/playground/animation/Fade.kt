package com.github.terrakok.modo.sample.playground.animation

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import kotlin.math.abs

/**
 * A simple fading animation. Appearing screens' `alpha` is animated from [minAlpha] to 1.0.
 * Disappearing screens' `alpha` is animated from 1.0 to [minAlpha].
 *
 * @param minAlpha the minimum alpha value during the animation. Default is 0.0 (fully transparent).
 */
fun fade(
    minAlpha: Float = 0f,
): StackAnimator =
    stackAnimator { progress, context, content ->
        val fadeProgress = if (context.direction.isExit) progress else 1f - progress
        content(Modifier.alpha(getFadeAlpha(factor = fadeProgress, minAlpha = minAlpha)))
    }

/**
 * Calculate alpha value based on animation factor.
 * Alpha transitions from 1.0 to minAlpha as factor moves away from 0.
 */
internal fun getFadeAlpha(factor: Float, minAlpha: Float): Float =
    (1f - abs(factor) * (1f - minAlpha)).coerceIn(minimumValue = minAlpha, maximumValue = 1f)
