package com.github.terrakok.androidcomposeapp.screens.stack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.terrakok.androidcomposeapp.screens.ButtonsState
import com.github.terrakok.androidcomposeapp.screens.SampleCustomBottomSheet
import com.github.terrakok.androidcomposeapp.screens.SamplePermanentDialog
import com.github.terrakok.androidcomposeapp.screens.base.MainButtonsContent
import com.github.terrakok.androidcomposeapp.screens.dialogs.SampleBottomSheetStack
import com.github.terrakok.androidcomposeapp.screens.dialogs.SampleDialog
import com.github.terrakok.androidcomposeapp.screens.dialogs.SampleDialogWithStack
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.stack.StackReducerAction
import com.github.terrakok.modo.stack.StackScreen
import com.github.terrakok.modo.stack.StackState
import com.github.terrakok.modo.stack.back
import com.github.terrakok.modo.stack.backTo
import com.github.terrakok.modo.stack.forward
import com.github.terrakok.modo.stack.newStack
import com.github.terrakok.modo.stack.removeScreens
import com.github.terrakok.modo.stack.replace
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
class StackActionsScreen(
    private val screenIndex: Int,
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        MainButtonsContent(
            modifier = modifier,
            screenKey = screenKey,
            screenIndex = screenIndex,
            buttonsState = rememberButtons(
                LocalContainerScreen.current as StackScreen,
                screenIndex
            )
        )
    }
}

@Composable
private fun rememberButtons(
    navigator: StackScreen,
    screenIndex: Int
): ButtonsState = remember {
    listOf<Pair<String, () -> Unit>>(
        "Forward" to { navigator.forward(StackActionsScreen(screenIndex + 1)) },
        "Back" to { navigator.back() },
        "Replace" to { navigator.replace(StackActionsScreen(screenIndex + 1)) },
        "New stack" to {
            navigator.newStack(
                StackActionsScreen(screenIndex + 1),
                StackActionsScreen(screenIndex + 2),
                StackActionsScreen(screenIndex + 3)
            )
        },
        "Multi forward" to {
            navigator.forward(
                StackActionsScreen(screenIndex + 1),
                StackActionsScreen(screenIndex + 2),
                StackActionsScreen(screenIndex + 3)
            )
        },
        "New root" to { navigator.newStack(StackActionsScreen(screenIndex + 1)) },
        "Forward 3 sec delay" to {
            GlobalScope.launch {
                delay(3000)
                navigator.forward(StackActionsScreen(screenIndex + 1))
            }
        },
        "Remove previous" to {
            val prevScreenIndex = navigator.navigationState.stack.lastIndex - 1
            navigator.removeScreens { pos, screen -> pos == prevScreenIndex }
        },
        "Back to '3'" to {
            navigator.backTo { pos, _ -> pos == 2 }
        },
        "Custom action" to {
            navigator.dispatch(StackReducerAction { oldState ->
                StackState(
                    oldState.stack.filterIndexed { index, screen ->
                        index % 2 == 0 && screen != oldState.stack.last()
                    }
                )
            })
        },
        // Just experiments
//        "2 items screen" to { navigator.forward(TwoTopItemsStackScreen(screenIndex + 1)) },
//                "Demo" to { navigator.forward(SaveableStateHolderDemoScreen()) },
        "Dialog" to { navigator.forward(SampleDialog(screenIndex + 1, dialogsPlayground = false, systemDialog = true, permanentDialog = false)) },
        "Permanent Dialog" to { navigator.forward(SamplePermanentDialog(screenIndex + 1)) },
        "Dialog Container" to { navigator.forward(SampleDialogWithStack(screenIndex + 1)) },
        "Bottom Sheet" to { navigator.forward(SampleBottomSheetStack(screenIndex + 1)) },
        "Custom Bottom Sheet" to { navigator.forward(SampleCustomBottomSheet(screenIndex + 1)) },
    ).let {
        ButtonsState(it)
    }
}