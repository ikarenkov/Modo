package com.github.terrakok.modo.sample.screens.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.model.coroutineScope
import com.github.terrakok.modo.sample.randomBackground
import com.github.terrakok.modo.sample.screens.ModoButton
import com.github.terrakok.modo.sample.screens.ModoButtonSpec
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.forward
import com.github.terrakok.modo.stack.replace
import kotlinx.coroutines.launch
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
internal class AnimationScreen(
    private val animationType: AnimationType = AnimationType.SLIDE_UP,
    private var exitOnPopAnimation: AnimationType = animationType.reversed(),
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen, StackAnimation {

    @IgnoredOnParcel
    private val exitOnPopAnimationState = mutableStateOf(exitOnPopAnimation)

    init {
        coroutineScope.launch {
            snapshotFlow { exitOnPopAnimationState.value }
                .collect { exitOnPopAnimation = it }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .randomBackground(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val stackNavigation = LocalStackNavigation.current
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = modifier.width(IntrinsicSize.Max)
            ) {
                ModoButton(
                    spec = ModoButtonSpec(
                        text = "Slide Up",
                        action = { stackNavigation.forward(AnimationScreen(AnimationType.SLIDE_UP)) }
                    ),
                    modifier = modifier.fillMaxWidth()
                )
                ModoButton(
                    spec = ModoButtonSpec(
                        text = "Slide Down",
                        action = { stackNavigation.forward(AnimationScreen(AnimationType.SLIDE_DOWN)) }
                    ),
                    modifier = modifier.fillMaxWidth()
                )
                ModoButton(
                    spec = ModoButtonSpec(
                        text = "Slide Left",
                        action = { stackNavigation.forward(AnimationScreen(AnimationType.SLIDE_LEFT)) }
                    ),
                    modifier = modifier.fillMaxWidth()
                )
                ModoButton(
                    spec = ModoButtonSpec(
                        text = "Slide Right",
                        action = { stackNavigation.forward(AnimationScreen(AnimationType.SLIDE_RIGHT)) }
                    ),
                    modifier = modifier.fillMaxWidth()
                )
                ModoButton(
                    spec = ModoButtonSpec(
                        text = "Replace",
                        action = { stackNavigation.replace(AnimationScreen(AnimationType.SLIDE_RIGHT)) }
                    ),
                    modifier = modifier.fillMaxWidth()
                )
            }
            var dropDownVisible by rememberSaveable { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = dropDownVisible,
                onExpandedChange = { dropDownVisible = it },
            ) {
                TextField(
                    value = "Exit on pop animation: ${exitOnPopAnimationState.value.name}",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropDownVisible) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = dropDownVisible,
                    onDismissRequest = { dropDownVisible = false },
                ) {
                    AnimationType.entries.forEach {
                        DropdownMenuItem(
                            text = {
                                Text(text = it.name)
                            },
                            onClick = {
                                exitOnPopAnimationState.value = it
                                dropDownVisible = false
                            }
                        )
                    }
                }
            }
        }
    }

    override fun getPushEnterTransition(animationScope: AnimatedContentTransitionScope<Screen>): EnterTransition =
        animationType.getEnterTransition(animationScope)

    override fun getPushExitTransition(animationScope: AnimatedContentTransitionScope<Screen>): ExitTransition =
        fadeOut(tween(ANIMATION_DURATION_MS))

    override fun getPopExitTransition(animationScope: AnimatedContentTransitionScope<Screen>): ExitTransition =
        exitOnPopAnimation.getExitTransition(animationScope)

    override fun getPopEnterTransition(animationScope: AnimatedContentTransitionScope<Screen>): EnterTransition =
        fadeIn(tween(ANIMATION_DURATION_MS))

    enum class AnimationType {
        SLIDE_UP,
        SLIDE_DOWN,
        SLIDE_LEFT,
        SLIDE_RIGHT;

        fun reversed(): AnimationType = when (this) {
            SLIDE_UP -> SLIDE_DOWN
            SLIDE_DOWN -> SLIDE_UP
            SLIDE_LEFT -> SLIDE_RIGHT
            SLIDE_RIGHT -> SLIDE_LEFT
        }

        fun getEnterTransition(scope: AnimatedContentTransitionScope<Screen>): EnterTransition = with(scope) {
            when (this@AnimationType) {
                SLIDE_UP -> slideIntoContainer(SlideDirection.Up, tween(ANIMATION_DURATION_MS))
                SLIDE_DOWN -> slideIntoContainer(SlideDirection.Down, tween(ANIMATION_DURATION_MS))
                SLIDE_LEFT -> slideIntoContainer(SlideDirection.Left, tween(ANIMATION_DURATION_MS))
                SLIDE_RIGHT -> slideIntoContainer(SlideDirection.Right, tween(ANIMATION_DURATION_MS))
            }
        }

        fun getExitTransition(scope: AnimatedContentTransitionScope<Screen>): ExitTransition = with(scope) {
            when (this@AnimationType) {
                SLIDE_UP -> slideOutOfContainer(SlideDirection.Up, tween(ANIMATION_DURATION_MS))
                SLIDE_DOWN -> slideOutOfContainer(SlideDirection.Down, tween(ANIMATION_DURATION_MS))
                SLIDE_LEFT -> slideOutOfContainer(SlideDirection.Left, tween(ANIMATION_DURATION_MS))
                SLIDE_RIGHT -> slideOutOfContainer(SlideDirection.Right, tween(ANIMATION_DURATION_MS))
            }
        }
    }

    companion object {
        const val ANIMATION_DURATION_MS = 500
    }
}