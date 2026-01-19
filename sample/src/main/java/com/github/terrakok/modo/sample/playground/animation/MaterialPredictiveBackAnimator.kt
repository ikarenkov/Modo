package com.github.terrakok.modo.sample.playground.animation

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex

/**
 * STANDARD_DECELERATE easing as recommended by Google for predictive back animations.
 * Equivalent to PathInterpolator(0f, 0f, 0f, 1f).
 *
 * This easing is applied internally by [materialPredictiveBackAnimator].
 *
 * @see <a href="https://developer.android.com/design/ui/mobile/guides/patterns/predictive-back#shared-element-transition">Google Predictive Back Design Guidelines</a>
 */
val StandardDecelerateEasing: Easing = CubicBezierEasing(0f, 0f, 0f, 1f)

/**
 * Creates a Material Design predictive back animator that matches the
 * [predictive back design for Android](https://developer.android.com/design/ui/mobile/guides/patterns/predictive-back).
 *
 * Implements the "Shared element transition" specification from Google:
 * - Scale: 90% at full gesture
 * - X Shift: ((screenWidth / 20) - 8) dp, leaving 8dp margin from edge
 * - Y Shift: ((screenHeight / 20) - 8) dp based on touch position
 *
 */
fun materialPredictiveBackAnimator(): PredictiveBackAnimator =
    PredictiveBackAnimator { gestureProgress, finishProgress, backState, context, content ->
        // Apply STANDARD_DECELERATE easing to gesture progress per Google spec
        val easedProgress = StandardDecelerateEasing.transform(gestureProgress)

        when (context.direction) {
            ScreenAnimationPhase.EXIT -> {
                var size by remember { mutableStateOf(Size.Zero) }
                val density = LocalDensity.current

                // Scale: 1.0 -> 0.9 during gesture (90% at full gesture per Google spec)
                val scaleFactor = 1f - easedProgress / 10f

                // Alpha: visible during gesture, fades out during finish
                val alpha = 1f - finishProgress

                // X Shift per Google spec: ((screenWidth / 20) - 8) dp
                // This leaves an 8dp margin from the screen edge
                val translationX = with(density) {
                    if (size.width == 0f) {
                        0f
                    } else {
                        val screenWidthDp = size.width / density.density
                        // Google formula: ((screenWidth / 20) - 8) dp max shift
                        val maxShiftDp = (screenWidthDp / 20f) - 8f
                        val shiftPx = maxShiftDp.dp.toPx() * easedProgress

                        // Account for scale offset (centering the scaled surface)
                        val scaledWidth = size.width * scaleFactor
                        val scaleOffsetX = (size.width - scaledWidth) / 2f

                        when (backState.swipeEdge) {
                            // Swiping from left: screen moves right (toward left edge from center)
                            PredictiveBackState.SwipeEdge.LEFT -> scaleOffsetX - shiftPx
                            // Swiping from right: screen moves left (toward right edge from center)
                            PredictiveBackState.SwipeEdge.RIGHT -> -scaleOffsetX + shiftPx
                            PredictiveBackState.SwipeEdge.UNKNOWN -> 0f
                        }
                    }
                }

                // Y Shift per Google spec: ((screenHeight / 20) - 8) dp
                // Based on touch Y position relative to screen center
                val translationY = with(density) {
                    if (size.height == 0f) {
                        0f
                    } else {
                        val screenHeightDp = size.height / density.density
                        // Google formula: ((screenHeight / 20) - 8) dp max shift
                        val maxShiftDp = (screenHeightDp / 20f) - 8f
                        val maxShiftPx = maxShiftDp.dp.toPx()

                        // Calculate Y offset based on touch position relative to center
                        // touchY is in screen coordinates, normalize to -1..1 range
                        val centerY = size.height / 2f
                        val touchOffsetNormalized = if (size.height > 0f) {
                            ((backState.touchY - centerY) / centerY).coerceIn(-1f, 1f)
                        } else {
                            0f
                        }

                        // Apply shift based on touch position
                        maxShiftPx * touchOffsetNormalized * easedProgress
                    }
                }

                // Corner radius increases with gesture progress
                val cornerRadius = with(density) { 16.dp.toPx() * easedProgress }

                content(
                    Modifier
                        .zIndex(1f)
                        .onPlaced { size = it.size.toSize() }
                        .graphicsLayer {
                            scaleX = scaleFactor
                            scaleY = scaleFactor
                            this.alpha = alpha
                            this.translationX = translationX
                            this.translationY = translationY
                            shape = RoundedCornerShape(cornerRadius)
                            clip = true
                        }
                )
            }
            ScreenAnimationPhase.ENTER -> {
                // Dark scrim: visible during gesture, fades out during finish
                val scrimAlpha = (1f - finishProgress) * 0.25f

                content(
                    Modifier
                        .zIndex(0f)
                        .drawWithContent {
                            drawContent()
                            drawRect(color = Color.Black.copy(alpha = scrimAlpha))
                        }
                )
            }
            ScreenAnimationPhase.IDLE -> {
                content(Modifier)
            }
        }
    }
