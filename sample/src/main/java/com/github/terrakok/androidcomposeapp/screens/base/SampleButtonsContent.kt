package com.github.terrakok.androidcomposeapp.screens.base

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.terrakok.androidcomposeapp.SampleAppConfig
import com.github.terrakok.androidcomposeapp.randomBackground
import com.github.terrakok.androidcomposeapp.screens.ButtonsList
import com.github.terrakok.androidcomposeapp.screens.ButtonsState
import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.stack.StackState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
internal fun SampleButtonsContent(
    screenIndex: Int,
    buttonsState: ButtonsState,
    modifier: Modifier = Modifier,
    isDialog: Boolean = false,
) {
    var counter by rememberSaveable { mutableStateOf(0) }
    LaunchedEffect(key1 = Unit) {
        if (SampleAppConfig.counterEnabled) {
            while (isActive) {
                delay(100)
                counter++
            }
        }
    }
    SampleButtonsContent(screenIndex, counter, buttonsState, modifier, isDialog)
}

@Composable
internal fun SampleButtonsContent(
    screenIndex: Int,
    counter: Int,
    buttonsState: ButtonsState,
    modifier: Modifier = Modifier,
    isDialog: Boolean = false,
) {
    Column(
        modifier = modifier
            .randomBackground()
            .padding(8.dp)
            .then(if (!isDialog) Modifier.fillMaxSize() else Modifier),
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
        Spacer(modifier = Modifier.size(16.dp))
        ButtonsList(
            buttonsState,
            Modifier.weight(1f, fill = false)
        )
    }
}