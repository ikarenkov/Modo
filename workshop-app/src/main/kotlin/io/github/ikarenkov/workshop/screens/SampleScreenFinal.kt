package io.github.ikarenkov.workshop.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.forward
import kotlinx.parcelize.Parcelize

// Workshop 1.2.1 - create SampleScreen class implementing Screen interface
// Workshop 1.2.3 - use @Parcelize annotation to make QuickStartScreen class Parcelable
@Parcelize
class SampleScreenFinal(
    // You can pass argiment as a constructor parameter
    private val screenIndex: Int,
    // Workshop 1.2.2 - implement screenKey in the constructor using generateScreenKey() function
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        // Taking a nearest stack navigation container
        val stackNavigation = LocalStackNavigation.current
        // Workshop 1.2.4 - implement Content function with QuickStartScreenContent composable
        SampleScreenContent(
            modifier = modifier,
            screenIndex = screenIndex,
            openNextScreen = {
                stackNavigation.forward(SampleScreenFinal(screenIndex + 1))
            },
        )
    }
}