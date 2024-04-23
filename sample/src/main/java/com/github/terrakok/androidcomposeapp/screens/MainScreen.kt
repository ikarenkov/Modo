package com.github.terrakok.androidcomposeapp.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.terrakok.androidcomposeapp.ListScreen
import com.github.terrakok.androidcomposeapp.ModelSampleScreen
import com.github.terrakok.androidcomposeapp.screens.base.MainButtonsContent
import com.github.terrakok.androidcomposeapp.screens.containers.HorizontalPagerScreen
import com.github.terrakok.androidcomposeapp.screens.containers.SampleContainerScreen
import com.github.terrakok.androidcomposeapp.screens.containers.SampleMultiScreen
import com.github.terrakok.androidcomposeapp.screens.containers.StackInLazyColumnScreen
import com.github.terrakok.androidcomposeapp.screens.containers.custom.MovableContentPlaygroundScreen
import com.github.terrakok.androidcomposeapp.screens.containers.custom.RemovableItemContainerScreen
import com.github.terrakok.androidcomposeapp.screens.containers.custom.SampleCustomContainerScreen
import com.github.terrakok.androidcomposeapp.screens.dialogs.DialogsPlayground
import com.github.terrakok.androidcomposeapp.screens.stack.StackActionsScreen
import com.github.terrakok.androidcomposeapp.screens.viewmodel.AndroidViewModelSampleScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.model.OnScreenRemoved
import com.github.terrakok.modo.stack.StackScreen
import com.github.terrakok.modo.stack.back
import com.github.terrakok.modo.stack.forward
import kotlinx.parcelize.Parcelize
import logcat.logcat

@Parcelize
class MainScreen(
    private val screenIndex: Int,
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @OptIn(ExperimentalModoApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        OnScreenRemoved {
            logcat { "Screen $screenKey was removed" }
        }
        val parent = LocalContainerScreen.current
        MainScreenContent(screenIndex, screenKey, parent as? StackScreen, modifier)
    }
}

@Composable
internal fun MainScreenContent(
    screenIndex: Int,
    screenKey: ScreenKey,
    parent: StackScreen?,
    modifier: Modifier = Modifier,
) {
    MainButtonsContent(
        screenIndex = screenIndex,
        screenKey = screenKey,
        buttonsState = rememberButtons(navigator = parent, i = screenIndex),
        modifier = modifier,
    )
}

@Composable
internal fun MainScreenContent(
    screenIndex: Int,
    screenKey: ScreenKey,
    counter: Int,
    parent: StackScreen,
    modifier: Modifier
) {
    MainButtonsContent(
        screenIndex = screenIndex,
        screenKey = screenKey,
        buttonsState = rememberButtons(navigator = parent, i = screenIndex),
        counter = counter,
        modifier = modifier
    )
}

@Composable
private fun rememberButtons(
    navigator: StackScreen?,
    i: Int
): ButtonsState = remember {
    listOf<Pair<String, () -> Unit>>(
        "Forward" to { navigator?.forward(MainScreen(i + 1)) },
        "Back" to { navigator?.back() },
        "Stack Playground" to { navigator?.forward(StackActionsScreen(i + 1)) },
        "Multiscreen" to { navigator?.forward(SampleMultiScreen()) },
        "Dialogs & BottomSheets" to { navigator?.forward(DialogsPlayground(i + 1)) },
        "Container" to { navigator?.forward(SampleContainerScreen(i + 1)) },
        "Custom Container Actions" to { navigator?.forward(SampleCustomContainerScreen()) },
        "Custom Container" to { navigator?.forward(RemovableItemContainerScreen()) },
        "Horizontal Pager" to { navigator?.forward(HorizontalPagerScreen()) },
        "Stacks in LazyColumn" to { navigator?.forward(StackInLazyColumnScreen()) },
        "List/Details" to { navigator?.forward(ListScreen()) },
        // Just experiments
//        "2 items screen" to { navigator.forward(TwoTopItemsStackScreen(i + 1)) },
//                "Demo" to { navigator.forward(SaveableStateHolderDemoScreen()) },
        "Sample Screen Model" to { navigator?.forward(ModelSampleScreen()) },
        "Android ViewModel" to { navigator?.forward(AndroidViewModelSampleScreen(i + 1)) },
        "Movable Content" to { navigator?.forward(MovableContentPlaygroundScreen()) },
        "Animation Playground" to { navigator?.forward(AnimationPlaygroundScreen()) },
    ).let {
        ButtonsState(it)
    }
}