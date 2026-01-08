package com.github.terrakok.modo.sample.playground

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

@Parcelize
class AnimationsPlaygroundScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        AnimationsPlaygroundContent(modifier)
    }
}

@Composable
fun AnimationsPlaygroundContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Animation Combination Solutions",
            style = MaterialTheme.typography.h5
        )

        Divider()

        Text("Solution 1: Progress Transformation (Easing)", style = MaterialTheme.typography.h6)
        Solution1ProgressTransformation()

        Divider()

        Text("Solution 2: Longest Duration Wins", style = MaterialTheme.typography.h6)
        Solution2LongestDuration()

        Divider()

        Text("Solution 3: Keyframes (Coordinated)", style = MaterialTheme.typography.h6)
        Solution3Keyframes()

        Divider()

        Text("Solution 4: Shared Spec (Pragmatic)", style = MaterialTheme.typography.h6)
        Solution4SharedSpec()
    }
}

// ============ Solution 1: Progress Transformation ============

@Composable
fun Solution1ProgressTransformation() {
    var manualProgress by remember { mutableFloatStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Each animation has its own easing curve applied to shared progress",
            style = MaterialTheme.typography.caption
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { isAnimating = true }) {
                Text("Animate")
            }
            Button(onClick = {
                isAnimating = false
                manualProgress = 0f
            }) {
                Text("Reset")
            }
        }

        Text("Manual Control: ${(manualProgress * 100).roundToInt()}%")
        Slider(
            value = manualProgress,
            onValueChange = { manualProgress = it },
            enabled = !isAnimating
        )

        val animatable = remember { Animatable(0f) }

        LaunchedEffect(isAnimating) {
            if (isAnimating) {
                animatable.snapTo(0f)
                animatable.animateTo(1f, tween(2000, easing = LinearEasing))
                isAnimating = false
            }
        }

        val progress = if (isAnimating) animatable.value else manualProgress

        // Same progress, different easing transformations
        AnimationBox(
            label = "Linear (no transform)",
            color = Color.Red,
            progress = progress  // No transformation
        )

        AnimationBox(
            label = "FastOutSlowIn",
            color = Color.Green,
            progress = FastOutSlowInEasing.transform(progress)  // Easing transform
        )

        AnimationBox(
            label = "Custom Bounce",
            color = Color.Blue,
            progress = bounceEasing(progress)  // Custom easing
        )

    }
}

private fun bounceEasing(progress: Float): Float {
    return if (progress < 0.5f) {
        // Bounce up
        (kotlin.math.sin(progress * kotlin.math.PI * 2) * 0.3f + progress).toFloat()
    } else {
        progress
    }
}

@Composable
private fun AnimationBox(
    label: String,
    color: Color,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color.LightGray.copy(alpha = 0.2f)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier
                .weight(0.3f)
                .padding(start = 8.dp),
            style = MaterialTheme.typography.caption
        )

        Box(
            modifier = Modifier
                .weight(0.7f)
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .offset { IntOffset((progress * 250).roundToInt(), 0) }
                    .background(color, MaterialTheme.shapes.small)
            )
        }
    }
}

// ============ Solution 2: Longest Duration Wins ============

@Composable
fun Solution2LongestDuration() {
    var isAnimating by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Animations have different durations. Each finishes at its own time.",
            style = MaterialTheme.typography.caption
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { isAnimating = true }) {
                Text("Animate")
            }
            Button(onClick = { isAnimating = false }) {
                Text("Reset")
            }
        }

        val globalAnimatable = remember { Animatable(0f) }

        LaunchedEffect(isAnimating) {
            if (isAnimating) {
                globalAnimatable.snapTo(0f)
                // Animate for longest duration (3000ms)
                globalAnimatable.animateTo(1f, tween(3000, easing = LinearEasing))
                isAnimating = false
            }
        }

        val globalProgress = globalAnimatable.value

        // Fast animation (1000ms) - finishes at globalProgress = 0.33
        val fastProgress = (globalProgress * 3000f / 1000f).coerceAtMost(1f)
        AnimationBoxWithDuration(
            label = "Fast (1000ms)",
            color = Color.Red,
            progress = fastProgress,
            isComplete = fastProgress >= 1f
        )

        // Medium animation (2000ms) - finishes at globalProgress = 0.66
        val mediumProgress = (globalProgress * 3000f / 2000f).coerceAtMost(1f)
        AnimationBoxWithDuration(
            label = "Medium (2000ms)",
            color = Color.Green,
            progress = mediumProgress,
            isComplete = mediumProgress >= 1f
        )

        // Slow animation (3000ms) - finishes at globalProgress = 1.0
        AnimationBoxWithDuration(
            label = "Slow (3000ms)",
            color = Color.Blue,
            progress = globalProgress,
            isComplete = globalProgress >= 1f
        )
    }
}

@Composable
private fun AnimationBoxWithDuration(
    label: String,
    color: Color,
    progress: Float,
    isComplete: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                if (isComplete) Color.Green.copy(alpha = 0.1f)
                else Color.LightGray.copy(alpha = 0.2f)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(0.3f)
                .padding(start = 8.dp)
        ) {
            Text(text = label, style = MaterialTheme.typography.caption)
            if (isComplete) {
                Text(text = "✓ Done", style = MaterialTheme.typography.caption, color = Color.Green)
            }
        }

        Box(
            modifier = Modifier
                .weight(0.7f)
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .offset { IntOffset((progress * 250).roundToInt(), 0) }
                    .background(color, MaterialTheme.shapes.small)
            )
        }
    }
}

// ============ Solution 3: Keyframes ============

@Composable
fun Solution3Keyframes() {
    var isAnimating by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Single keyframe animation coordinates multiple properties at specific times",
            style = MaterialTheme.typography.caption
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { isAnimating = true }) {
                Text("Animate")
            }
            Button(onClick = { isAnimating = false }) {
                Text("Reset")
            }
        }

        // Keyframe spec coordinating fade, slide, scale
        val keyframeSpec = keyframes<Float> {
            durationMillis = 2000

            // All start at 0
            0f at 0

            // Fade completes first (0-600ms)
            0.3f at 600

            // Slide next (0-1200ms)
            0.6f at 1200

            // Scale last (0-2000ms)
            1f at 2000
        }

        val animatable = remember { Animatable(0f) }

        LaunchedEffect(isAnimating) {
            if (isAnimating) {
                animatable.snapTo(0f)
                animatable.animateTo(1f, keyframeSpec)
                isAnimating = false
            }
        }

        val progress = animatable.value

        // Calculate individual animation progress from keyframe progress
        val fadeProgress = (progress / 0.3f).coerceAtMost(1f)  // 0-30% of total
        val slideProgress = ((progress - 0.3f) / 0.3f).coerceIn(0f, 1f)  // 30-60% of total
        val scaleProgress = ((progress - 0.6f) / 0.4f).coerceIn(0f, 1f)  // 60-100% of total

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Coordinated Animation Progress: ${(progress * 100).roundToInt()}%")
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .offset { IntOffset((slideProgress * 200).roundToInt(), 0) }
                        .alpha(fadeProgress)
                        .graphicsLayer {
                            val scale = 0.5f + (scaleProgress * 0.5f)
                            scaleX = scale
                            scaleY = scale
                        }
                        .background(Color.Magenta, MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Fade: ${(fadeProgress * 100).roundToInt()}%", color = Color.White)
                        Text("Slide: ${(slideProgress * 100).roundToInt()}%", color = Color.White)
                        Text("Scale: ${(scaleProgress * 100).roundToInt()}%", color = Color.White)
                    }
                }
            }
        }
    }
}

// ============ Solution 4: Shared Spec (Pragmatic) ============

@Composable
fun Solution4SharedSpec() {
    var isAnimating by remember { mutableStateOf(false) }
    var useSpring by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "All animations use the same spec and progress. Simple and synchronized.",
            style = MaterialTheme.typography.caption
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { isAnimating = true }) {
                Text("Animate")
            }
            Button(onClick = {
                isAnimating = false
            }) {
                Text("Reset")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { useSpring = !useSpring }
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Spec: ${if (useSpring) "Spring" else "Tween(1000ms)"}")
        }

        val spec: FiniteAnimationSpec<Float> = if (useSpring) {
            spring()
        } else {
            tween(1000)
        }

        val animatable = remember { Animatable(0f) }

        LaunchedEffect(isAnimating, spec) {
            if (isAnimating) {
                animatable.snapTo(0f)
                animatable.animateTo(1f, spec)
                isAnimating = false
            }
        }

        val progress = animatable.value

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Shared Progress: ${(progress * 100).roundToInt()}%")
                Spacer(modifier = Modifier.height(16.dp))

                // All three animations use exact same progress
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .offset { IntOffset((progress * 200).roundToInt(), 0) }  // Slide
                        .alpha(progress)  // Fade
                        .graphicsLayer {  // Scale
                            val scale = 0.5f + (progress * 0.5f)
                            scaleX = scale
                            scaleY = scale
                        }
                        .background(Color.Cyan, MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Text("All: ${(progress * 100).roundToInt()}%", color = Color.Black)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "✓ Simple: All animations perfectly synchronized",
                    style = MaterialTheme.typography.caption,
                    color = Color.Green
                )
                Text(
                    text = "✓ Predictive Back Ready: Single progress value",
                    style = MaterialTheme.typography.caption,
                    color = Color.Green
                )
            }
        }
    }
}

// ============ Previews ============

// Simple test animation to verify Animation Preview works
@Preview(name = "Test Animation", showBackground = true)
@Composable
fun PreviewTestAnimation() {
    var isVisible by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { isVisible = !isVisible }) {
            Text("Toggle")
        }

        val offset by animateIntAsState(
            targetValue = if (isVisible) 0 else 200,
            label = "offset"
        )

        Box(
            modifier = Modifier
                .offset(x = offset.dp)
                .size(100.dp)
                .background(Color.Blue)
        )
    }
}

@Preview(name = "Solution 1: Progress Transformation", showBackground = true)
@Composable
fun PreviewSolution1() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            Solution1ProgressTransformation()
        }
    }
}

@Preview(name = "Solution 2: Longest Duration", showBackground = true)
@Composable
fun PreviewSolution2() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            Solution2LongestDuration()
        }
    }
}

@Preview(name = "Solution 3: Keyframes", showBackground = true)
@Composable
fun PreviewSolution3() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            Solution3Keyframes()
        }
    }
}

@Preview(name = "Solution 4: Shared Spec (Recommended)", showBackground = true)
@Composable
fun PreviewSolution4() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            Solution4SharedSpec()
        }
    }
}

@Preview(name = "Full Playground", showBackground = true, heightDp = 2000)
@Composable
fun PreviewPlayground() {
    MaterialTheme {
        AnimationsPlaygroundContent()
    }
}

@Composable
fun ColorChangeAnimationScreen() {
    var isToggled by remember { mutableStateOf(false) }
    // Animate the color change whenever 'isToggled' changes
    val animatedColor by animateColorAsState(
        targetValue = if (isToggled) Color.Red else Color.Blue,
//        label = "color_animation" // Adding a label helps in the Animation Preview tool
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .background(animatedColor)
            .clickable { isToggled = !isToggled }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewColorChangeAnimation() {
    // The preview automatically detects and allows inspection of this animation
    ColorChangeAnimationScreen()
}