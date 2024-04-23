package com.github.terrakok.androidcomposeapp.screens.containers.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.NavModel
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.ReducerAction
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class RemovableItemContainerState(
    val screen1: Screen,
    val screen2: Screen,
    val screen3: Screen?,
    val screen4: Screen,
) : NavigationState {
    override fun getChildScreens(): List<Screen> = listOfNotNull(screen1, screen2, screen3, screen4)
}

fun interface RemovableItemContainerAction : ReducerAction<RemovableItemContainerState>

internal class Remove : RemovableItemContainerAction {
    override fun reduce(oldState: RemovableItemContainerState): RemovableItemContainerState =
        oldState.copy(screen3 = null)
}

internal class CreateScreen : RemovableItemContainerAction {
    override fun reduce(oldState: RemovableItemContainerState): RemovableItemContainerState =
        oldState.copy(screen3 = NestedScreen(canBeRemoved = true))
}

@Parcelize
class RemovableItemContainerScreen(
    private val navModel: NavModel<RemovableItemContainerState, RemovableItemContainerAction> = NavModel(
        RemovableItemContainerState(
            NestedScreen(canBeRemoved = false),
            NestedScreen(canBeRemoved = false),
            NestedScreen(canBeRemoved = true),
            NestedScreen(canBeRemoved = false),
        )
    )
) : ContainerScreen<RemovableItemContainerState, RemovableItemContainerAction>(navModel) {

    @Composable
    override fun Content(modifier: Modifier) {
        Column {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item(navigationState.screen1) {
                    InternalContent(navigationState.screen1)
                }
                item(navigationState.screen2) {
                    InternalContent(navigationState.screen2)
                }
                navigationState.screen3?.let {
                    item("screen3") {
                        InternalContent(it)
                    }
                }
                item(navigationState.screen4) {
                    InternalContent(navigationState.screen4)
                }
            }
            Column {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { dispatch(CreateScreen()) }
                ) {
                    Text(text = "Create screen")
                }
            }
        }
    }
}

@Parcelize
internal class NestedScreen(
    val canBeRemoved: Boolean,
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        val parent = LocalContainerScreen.current as RemovableItemContainerScreen
        InnerContent(
            title = screenKey.value,
            onRemoveClick = takeIf { canBeRemoved }?.let { { parent.dispatch(Remove()) } },
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )
    }

}