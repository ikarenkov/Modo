package com.github.terrakok.modo.stack

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.ContainerContent
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.stack.back

/**
 * Basic screen container that represents stack of [Screen]'s.
 */
@Stable
abstract class StackScreenNew(
    navigationModel: StackNavModel
) : ContainerScreen<StackState, StackAction>(navigationModel), StackNavContainer {

    override fun provideNavigationContainer(): ProvidedValue<StackNavContainer> =
        LocalStackNavigation provides this

    @OptIn(ExperimentalModoApi::class)
    @Composable
    protected fun Content(
        modifier: Modifier = Modifier,
        content: ContainerContent<StackState>
    ) {
        super.InternalContentNew(modifier, content)
    }

}

@Composable
fun StackNavContainer.StackBackHandler() {
    val isBackHandlerEnabled by remember {
        derivedStateOf {
            navigationState.getChildScreens().size > 1
        }
    }
    BackHandler(enabled = isBackHandlerEnabled) {
        back()
    }
}