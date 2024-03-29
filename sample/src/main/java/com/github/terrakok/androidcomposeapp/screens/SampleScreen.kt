package com.github.terrakok.androidcomposeapp.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.terrakok.androidcomposeapp.ListScreen
import com.github.terrakok.androidcomposeapp.ModelSampleScreen
import com.github.terrakok.androidcomposeapp.screens.base.SampleButtonsContent
import com.github.terrakok.androidcomposeapp.screens.containers.SampleContainerScreen
import com.github.terrakok.androidcomposeapp.screens.containers.SampleMultiScreen
import com.github.terrakok.androidcomposeapp.screens.dialogs.DialogsPlayground
import com.github.terrakok.androidcomposeapp.screens.dialogs.SampleBottomSheetStack
import com.github.terrakok.androidcomposeapp.screens.dialogs.SampleDialog
import com.github.terrakok.androidcomposeapp.screens.dialogs.SampleDialogWithStack
import com.github.terrakok.androidcomposeapp.screens.viewmodel.AndroidViewModelSampleScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.model.OnScreenRemoved
import com.github.terrakok.modo.stack.StackScreen
import com.github.terrakok.modo.stack.StackState
import com.github.terrakok.modo.stack.back
import com.github.terrakok.modo.stack.backTo
import com.github.terrakok.modo.stack.forward
import com.github.terrakok.modo.stack.newStack
import com.github.terrakok.modo.stack.replace
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        SampleScreenContent(i, parent as StackScreen, isDialog = false)
    }
}

@Composable
internal fun SampleScreenContent(
    screenIndex: Int,
    parent: StackScreen,
    modifier: Modifier = Modifier,
    isDialog: Boolean = false
) {
    SampleButtonsContent(
        screenIndex = screenIndex,
        buttonsState = rememberButtons(navigator = parent, i = screenIndex),
        modifier = modifier,
        isDialog = isDialog
    )
}

@Composable
internal fun SampleScreenContent(
    screenIndex: Int,
    counter: Int,
    parent: StackScreen,
    isDialog: Boolean = false
) {
    SampleButtonsContent(
        screenIndex = screenIndex,
        buttonsState = rememberButtons(navigator = parent, i = screenIndex),
        isDialog = isDialog,
        counter = counter
    )
}

@Composable
private fun rememberButtons(
    navigator: NavigationContainer<StackState>,
    i: Int
): ButtonsState = remember {
    listOf<Pair<String, () -> Unit>>(
        "Forward" to { navigator.forward(SampleScreen(i + 1)) },
        "Dialogs Playground" to { navigator.forward(DialogsPlayground(i + 1)) },
        "Back" to { navigator.back() },
        "Replace" to { navigator.replace(SampleScreen(i + 1)) },
        "New stack" to {
            navigator.newStack(
                SampleScreen(i + 1),
                SampleScreen(i + 2),
                SampleScreen(i + 3)
            )
        },
        "Multi forward" to {
            navigator.forward(
                SampleScreen(i + 1),
                SampleScreen(i + 2),
                SampleScreen(i + 3)
            )
        },
        "New root" to { navigator.newStack(SampleScreen(i + 1)) },
        "Forward 3 sec delay" to {
            GlobalScope.launch {
                delay(3000)
                navigator.forward(SampleScreen(i + 1))
            }
        },
        "Back to '3'" to {
            navigator.navigationState.stack.getOrNull(2)?.let {
                navigator.backTo(it)
            }
        },
        "Container" to { navigator.forward(SampleContainerScreen(i + 1)) },
        "Multiscreen" to { navigator.forward(SampleMultiScreen()) },
        "List/Details" to { navigator.forward(ListScreen()) },
        // Just experiments
//        "2 items screen" to { navigator.forward(TwoTopItemsStackScreen(i + 1)) },
//                "Demo" to { navigator.forward(SaveableStateHolderDemoScreen()) },
        "Dialog" to { navigator.forward(SampleDialog(i + 1, dialogsPlayground = false, systemDialog = true, permanentDialog = false)) },
        "Permanent Dialog" to { navigator.forward(SamplePermanentDialog(i + 1)) },
        "Dialog Container" to { navigator.forward(SampleDialogWithStack(i + 1)) },
        "Model" to { navigator.forward(ModelSampleScreen()) },
        "Bottom Sheet" to { navigator.forward(SampleBottomSheetStack(i + 1)) },
        "Android ViewModel" to { navigator.forward(AndroidViewModelSampleScreen(i + 1)) },
        "Custom Bottom Sheet" to { navigator.forward(SampleCustomBottomSheet(i + 1)) },
        "Animation Playground" to { navigator.forward(AnimationPlaygroundScreen()) },
    ).let {
        ButtonsState(it)
    }
}