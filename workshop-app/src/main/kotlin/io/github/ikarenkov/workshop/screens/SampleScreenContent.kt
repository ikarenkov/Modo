package io.github.ikarenkov.workshop.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun SampleScreenContent(
    screenIndex: Int,
    openNextScreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(text = "Hello, Modo! Screen №$screenIndex")
        Button(
            onClick = openNextScreen
        ) {
            Text(text = "Next screen")
        }
    }
}

@Preview
@Composable
private fun SampleScreenPreview() {
    SampleScreenContent(
        modifier = Modifier.fillMaxSize(),
        screenIndex = 1,
        openNextScreen = {},
    )
}