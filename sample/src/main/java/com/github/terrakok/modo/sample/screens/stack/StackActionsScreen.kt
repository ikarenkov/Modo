package com.github.terrakok.modo.sample.screens.stack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.sample.screens.ButtonsState
import com.github.terrakok.modo.sample.screens.GroupedButtonsState
import com.github.terrakok.modo.sample.screens.MainScreen
import com.github.terrakok.modo.sample.screens.SampleCustomBottomSheet
import com.github.terrakok.modo.sample.screens.SamplePermanentDialog
import com.github.terrakok.modo.sample.screens.base.ButtonsScreenContent
import com.github.terrakok.modo.sample.screens.dialogs.SampleBottomSheetStack
import com.github.terrakok.modo.sample.screens.dialogs.SampleDialog
import com.github.terrakok.modo.sample.screens.dialogs.SampleDialogWithStack
import com.github.terrakok.modo.stack.Back
import com.github.terrakok.modo.stack.Forward
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.StackNavContainer
import com.github.terrakok.modo.stack.StackState
import com.github.terrakok.modo.stack.back
import com.github.terrakok.modo.stack.backTo
import com.github.terrakok.modo.stack.dispatch
import com.github.terrakok.modo.stack.forward
import com.github.terrakok.modo.stack.removeScreens
import com.github.terrakok.modo.stack.replace
import com.github.terrakok.modo.stack.setStack
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

/**
 * Sample playground screen to take a look to available navigation operations for stack.
 */
@Parcelize
internal class StackActionsScreen(
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
            state = rememberButtons(
                LocalStackNavigation.current,
                screenIndex
            )
        )
    }
}

@Suppress("LongMethod", "MagicNumber")
@Composable
internal fun rememberButtons(
    navigation: StackNavContainer,
    screenIndex: Int
): GroupedButtonsState {
    val coroutineScope = rememberCoroutineScope()
    return remember {
        listOf<Pair<String, () -> Unit>>(
            "Forward" to { navigation.forward(StackActionsScreen(screenIndex + 1)) },
            "Back" to { navigation.back() },
            "Replace" to { navigation.replace(StackActionsScreen(screenIndex + 1)) },
            "Set new stack" to {
                navigation.setStack(
                    StackActionsScreen(screenIndex + 1),
                    StackActionsScreen(screenIndex + 2),
                    StackActionsScreen(screenIndex + 3)
                )
            },
            "Multi forward" to {
                navigation.forward(
                    StackActionsScreen(screenIndex + 1),
                    StackActionsScreen(screenIndex + 2),
                    StackActionsScreen(screenIndex + 3)
                )
            },
            "New root" to { navigation.setStack(StackActionsScreen(screenIndex + 1)) },
            "Forward 3 sec delay" to {
                coroutineScope.launch {
                    delay(3000)
                    navigation.forward(StackActionsScreen(screenIndex + 1))
                }
            },
            "Remove previous" to {
                val prevScreenIndex = navigation.navigationState.stack.lastIndex - 1
                navigation.removeScreens { pos, screen -> pos == prevScreenIndex }
            },
            "Back to '3'" to {
                navigation.backTo { pos, _ -> pos == 2 }
            },
            "Back 2 screens + Forward" to {
                navigation.dispatch(
                    Back(2),
                    Forward(StackActionsScreen(screenIndex + 1))
                )
            },
            "Back to MainScreen" to {
                navigation.backTo<MainScreen>()
            },
            "Back to root" to {
                navigation.backTo { pos, _ -> pos == 0 }
            },
            "Custom action" to {
                navigation.dispatch { oldState ->
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
            "Dialog" to {
                navigation.forward(
                    SampleDialog(
                        screenIndex + 1,
                        dialogsPlayground = false,
                        systemDialog = true,
                        permanentDialog = false
                    )
                )
            },
            "Permanent Dialog" to { navigation.forward(SamplePermanentDialog(screenIndex + 1)) },
            "Dialog Container" to { navigation.forward(SampleDialogWithStack(screenIndex + 1)) },
            "Bottom Sheet" to { navigation.forward(SampleBottomSheetStack(screenIndex + 1)) },
            "Custom Bottom Sheet" to { navigation.forward(SampleCustomBottomSheet(screenIndex + 1)) },
        ).let {
            ButtonsState(it)
        }
    }
}