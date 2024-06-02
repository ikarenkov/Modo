package com.github.terrakok.modo.sample.screens.base

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.IntState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.sample.SampleAppConfig
import com.github.terrakok.modo.sample.randomBackground
import com.github.terrakok.modo.sample.screens.ButtonsState
import com.github.terrakok.modo.sample.screens.GroupedButtonsList
import com.github.terrakok.modo.sample.screens.GroupedButtonsState
import com.github.terrakok.modo.sample.screens.ModoButtonSpec
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

internal const val COUNTER_DELAY_MS = 100L

@Composable
internal fun ButtonsScreenContent(
    screenIndex: Int,
    screenName: String,
    screenKey: ScreenKey,
    state: GroupedButtonsState,
    modifier: Modifier = Modifier,
) {
    val counter by rememberCounterState()
    ButtonsScreenContent(screenIndex, screenName, counter, screenKey, state, modifier)
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
) {
    SampleScreenContent(
        screenIndex = screenIndex,
        screenName = screenName,
        counter = counter,
        screenKey = screenKey,
        modifier = modifier,
    ) {
        GroupedButtonsList(
            state,
            Modifier.weight(1f, fill = false)
        )
    }
}

@Composable
internal fun SampleScreenContent(
    screenIndex: Int,
    screenName: String,
    screenKey: ScreenKey,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val counter by rememberCounterState()
    SampleScreenContent(screenIndex, screenName, counter, screenKey, modifier, content)
}

@Composable
internal fun SampleScreenContent(
    screenIndex: Int,
    screenName: String,
    counter: Int,
    screenKey: ScreenKey,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .randomBackground()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(8.dp),
    ) {
        Text(
            text = counter.toString()
        )
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
        modifier = Modifier.fillMaxSize()
    )
}