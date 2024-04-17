package com.github.terrakok.androidcomposeapp.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.terrakok.androidcomposeapp.ListScreen
import com.github.terrakok.androidcomposeapp.ModelSampleScreen
import com.github.terrakok.androidcomposeapp.screens.base.SampleButtonsContent
import com.github.terrakok.androidcomposeapp.screens.containers.SampleContainerScreen
import com.github.terrakok.androidcomposeapp.screens.containers.SampleMultiScreen
import com.github.terrakok.androidcomposeapp.screens.containers.custom.MovableContentPlaygroundScreen
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
class SampleScreen(
    private val i: Int,
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @OptIn(ExperimentalModoApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        OnScreenRemoved {
            logcat { "Screen $screenKey was removed" }
        }
        val parent = LocalContainerScreen.current
        SampleScreenContent(i, parent as StackScreen, modifier)
    }
}

@Composable
internal fun SampleScreenContent(
    screenIndex: Int,
    parent: StackScreen,
    modifier: Modifier = Modifier,
) {
    SampleButtonsContent(
        screenIndex = screenIndex,
        buttonsState = rememberButtons(navigator = parent, i = screenIndex),
        modifier = modifier,
    )
}

@Composable
internal fun SampleScreenContent(
    screenIndex: Int,
    counter: Int,
    parent: StackScreen,
    modifier: Modifier
) {
    SampleButtonsContent(
        screenIndex = screenIndex,
        buttonsState = rememberButtons(navigator = parent, i = screenIndex),
        counter = counter,
        modifier = modifier
    )
}

@Composable
private fun rememberButtons(
    navigator: StackScreen,
    i: Int
): ButtonsState = remember {
    listOf<Pair<String, () -> Unit>>(
        "Back" to { navigator.back() },
        "Stack Playground" to { navigator.forward(StackActionsScreen(i + 1)) },
        "Multiscreen" to { navigator.forward(SampleMultiScreen()) },
        "Dialogs & BottomSheets Playground" to { navigator.forward(DialogsPlayground(i + 1)) },
        "Container" to { navigator.forward(SampleContainerScreen(i + 1)) },
        "Custom Container Actions" to { navigator.forward(SampleCustomContainerScreen()) },
        "List/Details" to { navigator.forward(ListScreen()) },
        // Just experiments
//        "2 items screen" to { navigator.forward(TwoTopItemsStackScreen(i + 1)) },
//                "Demo" to { navigator.forward(SaveableStateHolderDemoScreen()) },
        "Model" to { navigator.forward(ModelSampleScreen()) },
        "Android ViewModel" to { navigator.forward(AndroidViewModelSampleScreen(i + 1)) },
        "Movable Content" to { navigator.forward(MovableContentPlaygroundScreen()) },
        "Animation Playground" to { navigator.forward(AnimationPlaygroundScreen()) },
    ).let {
        ButtonsState(it)
    }
}