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

// TODO: Workshop 1.2.1 - create SampleScreen class implementing Screen interface
// TODO: Workshop 1.2.2 - implement screenKey in the constructor using generateScreenKey() function
// TODO: Workshop 1.2.3 - use @Parcelize annotation to make SampleScreen class Parcelable
// TODO: Workshop 1.2.4 - implement Content function with SampleScreenContent composable
// TODO: Workshop 2.1 - get stack navigation using LocalStackNavigation.current
// TODO: Workshop 2.2 - navigate to next screen using forward function
// TODO: Workshop 2.3.1 - add argument to constructor
// TODO: Workshop 2.3.2 - use it in Content function
// TODO: Workshop 2.3.3 - pass argument to next screen
// TODO: Workshop 3.1.4 - navigate to MainTabScreen from SampleScreen

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