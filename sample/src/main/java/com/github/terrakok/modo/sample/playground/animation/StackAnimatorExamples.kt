package com.github.terrakok.modo.sample.playground.animation

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Example animators demonstrating how to create custom animations.
 * Users can use these as-is or as inspiration for their own custom animators.
 */

// ============= Pre-built Combinations =============

/**
 * Classic iOS-style animation: slide + fade with slight scale.
 */
fun iosLikeAnimation(): StackAnimator =
    fade(minAlpha = 0.9f) + slide() + scale(minScale = 0.95f)

/**
 * Material Design-style animation: scale + fade.
 */
fun materialAnimation(): StackAnimator =
    fade(minAlpha = 0f) + scale(minScale = 0.92f)

/**
 * Vertical slide animation.
 */
fun verticalSlide(): StackAnimator =
    slide(orientation = Orientation.Vertical) + fade(minAlpha = 0.8f)

/**
 * Bouncy animation with scale.
 * Note: To use spring animation spec, configure it at the StackAnimation level.
 */
fun bouncyAnimation(): StackAnimator =
    fade() + slide() + scale(minScale = 0.8f)

// ============= Custom Animators =============

/**
 * Custom blur animation that blurs the exiting screen.
 */
fun blurAnimation(): StackAnimator =
    stackAnimator { progress, context, content ->
        val blurRadius = when {
            context.direction.isExit -> progress * 10f
            else -> 0f
        }
        content(Modifier.blur(blurRadius.dp))
    }

/**
 * Custom rotation animation based on navigation direction.
 */
fun rotationAnimation(): StackAnimator =
    stackAnimator { progress, context, content ->
        val direction = rememberAnimationDirection(context)
        val rotation = when (direction) {
            AnimationDirection.FORWARD -> progress * 15f
            AnimationDirection.BACKWARD -> -progress * 15f
            AnimationDirection.IDLE -> 0f
        }
        content(
            Modifier.graphicsLayer(
                rotationY = rotation,
                transformOrigin = TransformOrigin.Center
            )
        )
    }

/**
 * Custom parallax-like animation where background moves slower.
 * Uses directional progress for smooth left/right transitions.
 */
fun parallaxAnimation(): StackAnimator =
    stackAnimator { progress, context, content ->
        val dirProgress = rememberDirectionalProgress(context, progress)
        val slowOffset = dirProgress * 0.3f  // Background moves 30% of full speed
        content(Modifier.offsetXFactor(slowOffset))
    }

/**
 * Custom elevator-style animation (vertical with scale).
 */
fun elevatorAnimation(): StackAnimator =
    slide(orientation = Orientation.Vertical) +
    scale(minScale = 0.8f, transformOrigin = TransformOrigin(0.5f, 0f)) +
    fade(minAlpha = 0.5f)

/**
 * Example of a completely custom animator using directional progress.
 * Combines slide, fade, and scale with direction-aware positioning.
 */
fun customFromScratch(): StackAnimator =
    stackAnimator { progress, context, content ->
        // Use helper for direction-aware offset
        val dirProgress = rememberDirectionalProgress(context, progress)

        // Calculate custom transformations
        val alpha = (1f - abs(dirProgress) * 0.5f).coerceIn(0.5f, 1f)
        val scale = (1f - abs(dirProgress) * 0.2f).coerceIn(0.8f, 1f)

        content(
            Modifier
                .graphicsLayer(
                    alpha = alpha,
                    scaleX = scale,
                    scaleY = scale,
                    transformOrigin = TransformOrigin.Center
                )
                .offsetXFactor(dirProgress)
        )
    }
