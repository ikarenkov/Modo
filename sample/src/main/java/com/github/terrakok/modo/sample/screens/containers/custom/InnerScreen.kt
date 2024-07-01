package com.github.terrakok.modo.sample.screens.containers.custom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
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
import com.github.terrakok.modo.sample.components.CancelButton
import com.github.terrakok.modo.sample.randomBackground
import com.github.terrakok.modo.sample.screens.base.LogLifecycle
import com.github.terrakok.modo.sample.screens.base.rememberCounterState
import kotlinx.parcelize.Parcelize
import logcat.logcat

@Parcelize
internal class InnerScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        val parent = LocalSampleCustomNavigation.current
        val closeScreen by rememberUpdatedState {
            parent.dispatch(RemoveScreen(screenKey))
        }
        LogLifecycle()
        InnerContent(
            title = "Screen $screenKey",
            onRemoveClick = closeScreen,
            modifier = modifier
        )
    }
}

@Composable
fun InnerContent(
    title: String,
    onRemoveClick: (() -> Unit)?,
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
        if (onRemoveClick != null) {
            CancelButton(onClick = onRemoveClick, contentDescription = "Close screen")
        }
    }
}

@Preview
@Composable
private fun InnerContentPreview() {
    InnerContent(
        title = "Screen 1",
        onRemoveClick = null,
        modifier = Modifier.size(100.dp)
    )
}

@Preview
@Composable
private fun InnerContentRemovablePreview() {
    InnerContent(
        title = "Screen 1",
        onRemoveClick = { },
        modifier = Modifier.size(100.dp)
    )
}