package com.github.terrakok.modo.sample.playground.animation

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex

/**
 * STANDARD_DECELERATE easing for full screen surfaces predictive back animation.
 * Equivalent to PathInterpolator(0.1f, 0.1f, 0f, 1f).
 *
 * This is slightly different from [StandardDecelerateEasing] in MaterialPredictiveBackAnimator
 * which uses (0, 0, 0, 1).
 */
private val FullScreenDecelerateEasing: Easing = CubicBezierEasing(0.1f, 0.1f, 0f, 1f)

/**
 * Creates a predictive back animator for full screen surfaces that matches the
 * [predictive back design for Android](https://developer.android.com/design/ui/mobile/guides/patterns/predictive-back).
 *
 * Implements the "Full screen surfaces" specification from Google:
 * - Exit Scale: 100% → 90%
 * - Enter Scale: 110% → 100%
 * - Exit Fade: 100% → 0% (completes by 35% progress - fade through)
 * - Enter Fade: 0% → 100% (starts at 35% progress - fade through)
 * - Interpolator: STANDARD_DECELERATE (0.1, 0.1, 0, 1)
 *
 * The 35% progress threshold creates a "fade through" crossfade effect where
 * neither screen is fully visible at the midpoint.
 */
fun fullScreenSurfacePredictiveBackAnimator(): PredictiveBackAnimator =
    PredictiveBackAnimator { gestureProgress, finishProgress, backState, context, content ->
        // Apply STANDARD_DECELERATE easing to gesture progress per Google spec
        val easedProgress = FullScreenDecelerateEasing.transform(gestureProgress)

        // Fade through threshold at 35% progress
        val fadeThreshold = 0.35f

        when (context.direction) {
            ScreenAnimationPhase.EXIT -> {
                // Scale: 100% → 90% during gesture
                val scale = 1f - (0.1f * easedProgress)

                // Fade: 100% → 0% by 35% progress (fade through)
                // During gesture: fade out completely by threshold
                // During finish: stay faded out
                val alpha = if (finishProgress > 0f) {
                    0f // Already faded during finish
                } else {
                    // Map 0..0.35 progress to 1..0 alpha
                    (1f - (easedProgress / fadeThreshold)).coerceIn(0f, 1f)
                }

                content(
                    Modifier
                        .zIndex(1f)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        }
                )
            }
            ScreenAnimationPhase.ENTER -> {
                // Scale: 110% → 100% during gesture
                val scale = 1.1f - (0.1f * easedProgress)

                // Fade: 0% → 100% starting at 35% progress (fade through)
                // During gesture: fade in after threshold
                // During finish: fully visible
                val alpha = if (finishProgress > 0f) {
                    1f // Fully visible during finish
                } else {
                    // Map 0.35..1 progress to 0..1 alpha
                    ((easedProgress - fadeThreshold) / (1f - fadeThreshold)).coerceIn(0f, 1f)
                }

                content(
                    Modifier
                        .zIndex(0f)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        }
                )
            }
            ScreenAnimationPhase.IDLE -> {
                content(Modifier)
            }
        }
    }