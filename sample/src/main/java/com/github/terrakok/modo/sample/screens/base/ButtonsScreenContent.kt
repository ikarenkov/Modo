package com.github.terrakok.modo.sample.screens.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.IntState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.sample.SampleAppConfig
import com.github.terrakok.modo.sample.components.BackButton
import com.github.terrakok.modo.sample.logs.logcat
import com.github.terrakok.modo.sample.randomBackground
import com.github.terrakok.modo.sample.screens.ButtonsState
import com.github.terrakok.modo.sample.screens.GroupedButtonsList
import com.github.terrakok.modo.sample.screens.GroupedButtonsState
import com.github.terrakok.modo.sample.screens.ModoButtonSpec
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.back
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

internal const val COUNTER_DELAY_MS = 100L

@Composable
internal fun Screen.ButtonsScreenContent(
    screenIndex: Int,
    screenName: String,
    state: GroupedButtonsState,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets.systemBars,
    topRightButtonSlot: @Composable () -> Unit = {},
    logLifecycle: Boolean = true,
    enableCounter: Boolean = true,
) {
    if (logLifecycle) {
        LogLifecycle()
    }
    val counter by rememberCounterState()
    ButtonsScreenContent(
        screenIndex = screenIndex,
        screenName = screenName,
        counter = if (enableCounter) counter else 0,
        screenKey = screenKey,
        state = state,
        topRightButtonSlot = topRightButtonSlot,
        windowInsets = windowInsets,
        modifier = modifier
    )
}

@Composable
fun rememberCounterState(): IntState {
    val counter = rememberSaveable { mutableIntStateOf(0) }
    LaunchedEffect(key1 = Unit) {
        if (SampleAppConfig.counterEnabled) {
            while (isActive) {
                delay(COUNTER_DELAY_MS)
                counter.intValue++
            }
        }
    }
    return counter
}

@Composable
internal fun ButtonsScreenContent(
    screenIndex: Int,
    screenName: String,
    counter: Int,
    screenKey: ScreenKey,
    state: GroupedButtonsState,
    modifier: Modifier = Modifier,
    topRightButtonSlot: @Composable () -> Unit = {},
    windowInsets: WindowInsets = WindowInsets.systemBars,
) {
    SampleScreenContent(
        screenIndex = screenIndex,
        screenName = screenName,
        counter = counter,
        screenKey = screenKey,
        topRightButtonSlot = topRightButtonSlot,
        windowInsets = windowInsets,
        modifier = modifier,
    ) {
        GroupedButtonsList(
            state,
            Modifier.weight(1f, fill = false)
        )
    }
}

@Composable
internal fun Screen.SampleScreenContent(
    screenIndex: Int,
    screenName: String,
    screenKey: ScreenKey,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets.systemBars,
    content: @Composable ColumnScope.() -> Unit
) {
    LogLifecycle()
    val counter by rememberCounterState()
    SampleScreenContent(
        screenIndex = screenIndex,
        screenName = screenName,
        counter = counter,
        screenKey = screenKey,
        modifier = modifier,
        windowInsets = windowInsets,
        content = content
    )
}

@OptIn(ExperimentalModoApi::class)
@Composable
fun Screen.LogLifecycle(prefix: String = "") {
    val lifecycleOwner = LocalLifecycleOwner.current

    // You will not be able to observe updates of lifecycleOwner when this content is not in the composition
    DisposableEffect(lifecycleOwner) {
        logcat("LogLifecycle") { "$prefix DisposableEffect $lifecycleOwner".trim() }
        val observer = LifecycleEventObserver { _, event ->
            logcat("LogLifecycle") { "$prefix DisposableEffect $event $lifecycleOwner".trim() }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            logcat("LogLifecycle") { "$prefix DisposableEffect.onDispose $lifecycleOwner".trim() }
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
//    LifecycleScreenEffect {
//        LifecycleEventObserver { source, event ->
//            logcat(tag = "LifecycleDebug") { "$screenKey LifecycleScreenEffect $event" }
//        }
//    }
}

@Composable
internal fun SampleScreenContent(
    screenIndex: Int,
    screenName: String,
    counter: Int,
    screenKey: ScreenKey,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets.systemBars,
    topRightButtonSlot: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .randomBackground()
            .windowInsetsPadding(windowInsets),
    ) {
        Column(Modifier.padding(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val stackNavigation = if (LocalInspectionMode.current) null else LocalStackNavigation.current
                BackButton(
                    onClick = { stackNavigation?.back() },
                )
                Text(
                    text = counter.toString(),
                    modifier = Modifier.weight(1f)
                )
                topRightButtonSlot()
            }
            Text(
                text = "$screenName $screenIndex",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "ScreenKey: ${screenKey.value}",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(16.dp))
            content()
        }
        LifecycleEventsHistory()
    }
}

@Preview
@Composable
private fun ButtonsPreview() {
    ButtonsScreenContent(
        screenIndex = 0,
        counter = 666,
        screenName = "ButtonsPreview",
        screenKey = ScreenKey("ScreenKey"),
        state = ButtonsState(
            listOf(
                ModoButtonSpec("Button 1") {},
                ModoButtonSpec("Button 2") {},
                ModoButtonSpec("Button 3") {},
                ModoButtonSpec("Button with a very long text") {},
            )
        ),
        topRightButtonSlot = {
            IconButton(onClick = {}) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Filled.ArrowDropDown),
                    contentDescription = null
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}