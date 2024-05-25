package com.github.terrakok.modo.sample.screens.stack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.sample.screens.ButtonsState
import com.github.terrakok.modo.sample.screens.GroupedButtonsState
import com.github.terrakok.modo.sample.screens.MainScreen
import com.github.terrakok.modo.sample.screens.ModoButtonSpec
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
                screenKey,
                screenIndex
            )
        )
    }
}

@Suppress("LongMethod", "MagicNumber")
@Composable
private fun rememberButtons(
    navigation: StackNavContainer,
    screenKey: ScreenKey,
    screenIndex: Int
): GroupedButtonsState {
    val coroutineScope = rememberCoroutineScope()
    val isFirstScreen by remember {
        derivedStateOf {
            navigation.navigationState.stack.first().screenKey == screenKey
        }
    }
    return remember(navigation, isFirstScreen) {
        listOf<ModoButtonSpec>(
            ModoButtonSpec("Forward") { navigation.forward(StackActionsScreen(screenIndex + 1)) },
            ModoButtonSpec("Back", isEnabled = !isFirstScreen) { navigation.back() },
            ModoButtonSpec("Replace") { navigation.replace(StackActionsScreen(screenIndex + 1)) },
            ModoButtonSpec("Set new stack") {
                navigation.setStack(
                    StackActionsScreen(screenIndex + 1),
                    StackActionsScreen(screenIndex + 2),
                    StackActionsScreen(screenIndex + 3)
                )
            },
            ModoButtonSpec("Multi forward") {
                navigation.forward(
                    StackActionsScreen(screenIndex + 1),
                    StackActionsScreen(screenIndex + 2),
                    StackActionsScreen(screenIndex + 3)
                )
            },
            ModoButtonSpec("New root") { navigation.setStack(StackActionsScreen(screenIndex + 1)) },
            ModoButtonSpec("Forward 3 sec delay") {
                coroutineScope.launch {
                    delay(3000)
                    navigation.forward(StackActionsScreen(screenIndex + 1))
                }
            },
            ModoButtonSpec("Remove previous") {
                val prevScreenIndex = navigation.navigationState.stack.lastIndex - 1
                navigation.removeScreens { pos, screen -> pos == prevScreenIndex }
            },
            ModoButtonSpec("Back to '3'") {
                navigation.backTo { pos, _ -> pos == 2 }
            },
            ModoButtonSpec("Back 2 screens + Forward") {
                navigation.dispatch(
                    Back(2),
                    Forward(StackActionsScreen(screenIndex + 1))
                )
            },
            ModoButtonSpec("Back to MainScreen") {
                navigation.backTo<MainScreen>()
            },
            ModoButtonSpec("Back to root") {
                navigation.backTo { pos, _ -> pos == 0 }
            },
            ModoButtonSpec("Custom action") {
                navigation.dispatch { oldState ->
                    StackState(
                        oldState.stack.filterIndexed { index, screen ->
                            index % 2 == 0 && screen != oldState.stack.last()
                        }
                    )
                }
            },
            // Just experiments
//        ModoButtonSpec("2 items screen") { navigator.forward(TwoTopItemsStackScreen(screenIndex + 1)) },
//                ModoButtonSpec("Demo") { navigator.forward(SaveableStateHolderDemoScreen()) },
            ModoButtonSpec("Dialog") {
                navigation.forward(
                    SampleDialog(
                        screenIndex + 1,
                        dialogsPlayground = false,
                        systemDialog = true,
                        permanentDialog = false
                    )
                )
            },
            ModoButtonSpec("Permanent Dialog") { navigation.forward(SamplePermanentDialog(screenIndex + 1)) },
            ModoButtonSpec("Dialog Container") { navigation.forward(SampleDialogWithStack(screenIndex + 1)) },
            ModoButtonSpec("Bottom Sheet") { navigation.forward(SampleBottomSheetStack(screenIndex + 1)) },
            ModoButtonSpec("Custom Bottom Sheet") { navigation.forward(SampleCustomBottomSheet(screenIndex + 1)) },
        ).let {
            ButtonsState(it)
        }
    }
}