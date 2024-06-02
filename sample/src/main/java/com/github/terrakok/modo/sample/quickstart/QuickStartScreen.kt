package com.github.terrakok.modo.sample.quickstart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.forward
import kotlinx.parcelize.Parcelize

// You need to use Parcelize plugin to generate Parcelable implementation for process death survavial
@Parcelize
class QuickStartScreen(
    // You can pass argiment as a constructor parameter
    private val screenIndex: Int,
    // You need to generate a unique screen key using special function
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        // Taking a nearest stack navigation container
        val stackNavigation = LocalStackNavigation.current
        QuickStartScreenContent(
            modifier = modifier,
            screenIndex = screenIndex,
            openNextScreen = {
                stackNavigation.forward(QuickStartScreen(screenIndex + 1))
            },
        )
    }
}

@Composable
internal fun QuickStartScreenContent(
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
    QuickStartScreenContent(
        modifier = Modifier.fillMaxSize(),
        screenIndex = 1,
        openNextScreen = {},
    )
}