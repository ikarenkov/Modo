package com.github.terrakok.modo.sample.screens.containers.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.NavModel
import com.github.terrakok.modo.NavigationAction
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.ReducerAction
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import kotlinx.parcelize.Parcelize

@Stable
@Parcelize
internal data class CustomContainerState(
    val screens: List<Screen>
) : NavigationState {
    override fun getChildScreens(): List<Screen> = screens
}

internal interface CustomContainerAction : NavigationAction<CustomContainerState>
internal fun interface CustomContainerReducerAction : CustomContainerAction, ReducerAction<CustomContainerState>

internal class RemoveScreen(val screenKey: ScreenKey) : CustomContainerReducerAction {
    override fun reduce(oldState: CustomContainerState): CustomContainerState = CustomContainerState(
        oldState.screens.filter { it.screenKey != screenKey }
    )
}

@Suppress("CompositionLocalAllowlist")
internal val LocalSampleCustomNavigation = compositionLocalOf<SampleCustomContainerScreen> {
    error("CompositionLocal LocalSampleCustomNavigation is not present")
}

@Parcelize
internal class SampleCustomContainerScreen(
    private val navModel: NavModel<CustomContainerState, CustomContainerAction> = NavModel(CustomContainerState(listOf(InnerScreen())))
) : ContainerScreen<CustomContainerState, CustomContainerAction>(navModel) {

    override fun provideCompositionLocals(): Array<ProvidedValue<*>> =
        arrayOf(LocalSampleCustomNavigation provides this)

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier.padding(16.dp)
        ) {
            val screenContent = movableScreen {
                InternalContent(screen = it, modifier = Modifier.size(150.dp))
            }
            var isFlowRow by rememberSaveable {
                mutableStateOf(false)
            }
            Column(
                Modifier.weight(1f)
            ) {
                if (isFlowRow) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        navigationState.screens.forEach { screen ->
                            screenContent(screen)
                        }
                    }
                } else {
                    LazyColumn {
                        items(navigationState.screens, key = { it.screenKey }) { screen ->
                            screenContent(screen)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            Column {
                Button(
                    onClick = {
                        navModel.dispatch(
                            CustomContainerReducerAction { state ->
                                CustomContainerState(listOf(InnerScreen()) + state.screens)
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Add screen")
                }
                Button(
                    onClick = {
                        navModel.dispatch(
                            CustomContainerReducerAction { state ->
                                CustomContainerState(state.screens.reversed())
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Reverse screens")
                }
                Button(
                    onClick = {
                        isFlowRow = !isFlowRow
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Switch")
                }
            }
        }
    }

    // actually it is remembered, but in composedItems
    @Suppress("RememberContentMissing")
    @Composable
    fun movableScreen(
        transform: @Composable (screen: Screen) -> Unit
    ): @Composable (item: Screen) -> Unit {
        val screens = navigationState.screens
        val composedItems = remember { mutableStateMapOf<Screen, @Composable () -> Unit>() }
        DisposableEffect(key1 = this) {
            val movableContentScreens = composedItems.keys
            val actualScreens = screens.toSet()
            val removedScreens = movableContentScreens - actualScreens
            removedScreens.forEach {
                composedItems -= it
            }
            onDispose {}
        }
        return { item: Screen ->
            composedItems.getOrPut(item) {
                movableContentOf { transform(item) }
            }.invoke()
        }
    }

}