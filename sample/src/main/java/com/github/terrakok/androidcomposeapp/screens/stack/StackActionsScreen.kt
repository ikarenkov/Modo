package com.github.terrakok.androidcomposeapp.screens.stack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.github.terrakok.androidcomposeapp.screens.ButtonsState
import com.github.terrakok.androidcomposeapp.screens.MainScreen
import com.github.terrakok.androidcomposeapp.screens.SampleCustomBottomSheet
import com.github.terrakok.androidcomposeapp.screens.SamplePermanentDialog
import com.github.terrakok.androidcomposeapp.screens.base.ButtonsScreenContent
import com.github.terrakok.androidcomposeapp.screens.dialogs.SampleBottomSheetStack
import com.github.terrakok.androidcomposeapp.screens.dialogs.SampleDialog
import com.github.terrakok.androidcomposeapp.screens.dialogs.SampleDialogWithStack
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.stack.Back
import com.github.terrakok.modo.stack.Forward
import com.github.terrakok.modo.stack.StackNavigationContainer
import com.github.terrakok.modo.stack.StackState
import com.github.terrakok.modo.stack.back
import com.github.terrakok.modo.stack.backTo
import com.github.terrakok.modo.stack.dispatch
import com.github.terrakok.modo.stack.forward
import com.github.terrakok.modo.stack.newStack
import com.github.terrakok.modo.stack.removeScreens
import com.github.terrakok.modo.stack.replace
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
        ButtonsScreenContent(
            modifier = modifier,
            screenName = "StackActionsScreen",
            screenKey = screenKey,
            screenIndex = screenIndex,
            buttonsState = rememberButtons(
                LocalContainerScreen.current as StackNavigationContainer,
                screenIndex
            )
        )
    }
}

@Composable
private fun rememberButtons(
    navigator: StackNavigationContainer,
    screenIndex: Int
): ButtonsState {
    val coroutineScope = rememberCoroutineScope()
    return remember {
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
                coroutineScope.launch {
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
            "Back 2 screens + Forward" to {
                navigator.dispatch(
                    Back(2),
                    Forward(StackActionsScreen(screenIndex + 1))
                )
            },
            "Back to MainScreen" to {
                navigator.backTo<MainScreen>()
            },
            "Back to root" to {
                navigator.backTo { pos, _ -> pos == 0 }
            },
            "Custom action" to {
                navigator.dispatch { oldState ->
                    StackState(
                        oldState.stack.filterIndexed { index, screen ->
                            index % 2 == 0 && screen != oldState.stack.last()
                        }
                    )
                }
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
}