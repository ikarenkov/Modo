package com.github.terrakok.modo.sample.playground.animation

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout

/**
 * A simple sliding animation. Screens enter from one side and exit to another side.
 *
 * @param orientation the orientation of the slide (horizontal or vertical).
 */
fun slide(
    orientation: Orientation = Orientation.Horizontal,
): StackAnimator =
    stackAnimator { progress, context, content ->
        // Use helper for direction-aware progress with performance optimization
        val dirProgress = rememberDirectionalProgress(context, progress)

        content(
            when (orientation) {
                Orientation.Horizontal -> Modifier.offsetXFactor(dirProgress)
                Orientation.Vertical -> Modifier.offsetYFactor(dirProgress)
            }
        )
    }

/**
 * Applies horizontal offset based on factor.
 * Factor of 1.0 means full screen width to the right, -1.0 means full width to the left.
 */
internal fun Modifier.offsetXFactor(factor: Float): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        layout(placeable.width, placeable.height) {
            placeable.placeRelative(
                x = (placeable.width.toFloat() * factor).toInt(),
                y = 0
            )
        }
    }

/**
 * Applies vertical offset based on factor.
 * Factor of 1.0 means full screen height downward, -1.0 means full height upward.
 */
internal fun Modifier.offsetYFactor(factor: Float): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        layout(placeable.width, placeable.height) {
            placeable.placeRelative(
                x = 0,
                y = (placeable.height.toFloat() * factor).toInt()
            )
        }
    }
