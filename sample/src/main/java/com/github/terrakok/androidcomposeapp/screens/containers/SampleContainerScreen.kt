package com.github.terrakok.androidcomposeapp.screens.containers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.github.terrakok.androidcomposeapp.SlideTransition
import com.github.terrakok.androidcomposeapp.components.CancelButton
import com.github.terrakok.androidcomposeapp.screens.MainScreen
import com.github.terrakok.androidcomposeapp.screens.base.SampleScreenContent
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.NavModel
import com.github.terrakok.modo.lifecycle.LifecycleScreenEffect
import com.github.terrakok.modo.model.OnScreenRemoved
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.StackNavModel
import com.github.terrakok.modo.stack.StackState
import com.github.terrakok.modo.stack.back
import kotlinx.parcelize.Parcelize
import logcat.logcat

@Parcelize
class SampleContainerScreen(
    private val i: Int,
    private val navModel: StackNavModel
) : SampleStack(navModel) {

    constructor(
        i: Int,
        sampleNavigationState: StackState = StackState(MainScreen(1))
    ) : this(i, NavModel(sampleNavigationState))

    @Suppress("ModifierNotUsedAtRoot")
    @OptIn(ExperimentalModoApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        LifecycleScreenEffect {
            LifecycleEventObserver { source: LifecycleOwner, event: Lifecycle.Event ->
                logcat { "$screenKey: Lifecycle.Event $event" }
            }
        }
        OnScreenRemoved {
            logcat { "Screen $screenKey was removed" }
        }
        val navigation = LocalStackNavigation.current
        Box {
            SampleScreenContent(
                screenIndex = i,
                screenName = "SampleContainerScreen",
                screenKey = screenKey
            ) {
                TopScreenContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                ) { modifier ->
                    SlideTransition(modifier)
                }
            }
            CancelButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .windowInsetsPadding(WindowInsets.statusBars),
                onClick = { navigation.back() },
                contentDescription = "Close screen"
            )
        }
    }

}

@Preview
@Composable
private fun PreviewContainerScreen() {
    SampleContainerScreen(1).Content(Modifier)
}