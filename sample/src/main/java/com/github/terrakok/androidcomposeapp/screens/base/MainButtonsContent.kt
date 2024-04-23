package com.github.terrakok.androidcomposeapp.screens.base

import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.terrakok.androidcomposeapp.SampleAppConfig
import com.github.terrakok.androidcomposeapp.randomBackground
import com.github.terrakok.androidcomposeapp.screens.ButtonsList
import com.github.terrakok.androidcomposeapp.screens.ButtonsState
import com.github.terrakok.modo.ScreenKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import logcat.logcat

@Composable
internal fun MainButtonsContent(
    screenIndex: Int,
    screenKey: ScreenKey,
    buttonsState: ButtonsState,
    modifier: Modifier = Modifier,
) {
    var counter by rememberSaveable { mutableStateOf(0) }
    val context = LocalContext.current
    val view = LocalView.current
    LaunchedEffect(key1 = Unit) {
        logcat("SampleButtonsContent") { "heightDp " + context.resources.configuration.screenHeightDp.toString() }
        logcat("SampleButtonsContent") { "view " + view.minimumHeight }
        view.requestLayout()
        if (SampleAppConfig.counterEnabled) {
            while (isActive) {
                delay(100)
                counter++
            }
        }
    }
    MainButtonsContent(screenIndex, counter, screenKey, buttonsState, modifier)
}

@Composable
internal fun MainButtonsContent(
    screenIndex: Int,
    counter: Int,
    screenKey: ScreenKey,
    buttonsState: ButtonsState,
    modifier: Modifier = Modifier,
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
            text = "Screen $screenIndex",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Text(
            text = screenKey.value,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(16.dp))
        ButtonsList(
            buttonsState,
            Modifier.weight(1f, fill = false)
        )
    }
}

@Preview
@Composable
private fun ButtonsPreview() {
    MainButtonsContent(
        screenIndex = 0,
        counter = 666,
        screenKey = ScreenKey("ScreenKey"),
        buttonsState = ButtonsState(
            listOf(
                "Button 1",
                "Button 2",
                "Button 3",
                "Button with a very long text",
            ).map { it to {} }
        ),
        modifier = Modifier.fillMaxSize()
    )
}