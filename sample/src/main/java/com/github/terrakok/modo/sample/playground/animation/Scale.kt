package com.github.terrakok.modo.sample.playground.animation

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.abs

/**
 * A simple scaling animation. Screens scale from [minScale] to 1.0 when entering,
 * and from 1.0 to [minScale] when exiting.
 *
 * @param minScale the minimum scale value during the animation. Default is 0.9 (90% size).
 * @param transformOrigin the origin point for the scale transformation. Default is center.
 */
fun scale(
    minScale: Float = 0.9f,
    transformOrigin: TransformOrigin = TransformOrigin.Center,
): StackAnimator =
    stackAnimator { progress, context, content ->
        val scale = getScale(factor = if (context.direction.isEnter) 1f - progress else progress, minScale = minScale)
        content(
            Modifier.graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                transformOrigin = transformOrigin
            )
        )
    }

/**
 * Calculate scale value based on animation factor.
 * Scale transitions from 1.0 to minScale as factor moves away from 0.
 */
internal fun getScale(factor: Float, minScale: Float): Float =
    (1f - abs(factor) * (1f - minScale)).coerceIn(minimumValue = minScale, maximumValue = 1f)
