package com.github.terrakok.modo.sample.screens.containers.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleEventObserver
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.lifecycle.LifecycleScreenEffect
import com.github.terrakok.modo.sample.randomBackground
import com.github.terrakok.modo.sample.screens.base.rememberCounterState
import kotlinx.parcelize.Parcelize
import logcat.logcat

@Parcelize
internal class ListInnerScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @OptIn(ExperimentalModoApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        LifecycleScreenEffect {
            LifecycleEventObserver { _, event ->
                logcat(tag = "InnerScreen Lifecycle") { "$screenKey $event" }
            }
        }
        ListInnerContent(
            title = "Screen $screenKey",
            modifier = modifier
        )
    }
}

@Composable
internal fun ListInnerContent(
    title: String,
    modifier: Modifier = Modifier
) {
    val parent = LocalContainerScreen.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .randomBackground()
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
        ) {
            Text(text = title)
            val counter by rememberCounterState()
            Text(text = "$counter")
        }
    }
}

@Preview
@Composable
private fun ListInnerContentPreview() {
    ListInnerContent(
        title = "Screen 1",
        modifier = Modifier.size(100.dp)
    )
}