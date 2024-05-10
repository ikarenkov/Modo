package com.github.terrakok.modo.sample.screens.containers.custom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
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
class InnerScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @OptIn(ExperimentalModoApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        val parent = LocalSampleCustomNavigation.current
        val closeScreen by rememberUpdatedState {
            parent.dispatch(RemoveScreen(screenKey))
        }
        LifecycleScreenEffect {
            LifecycleEventObserver { _, event ->
                logcat(tag = "InnerScreen Lifecycle") { "$screenKey $event" }
            }
        }
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
            IconButton(onClick = onRemoveClick) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Outlined.Close),
                    contentDescription = "Close screen"
                )
            }
        }
    }
}