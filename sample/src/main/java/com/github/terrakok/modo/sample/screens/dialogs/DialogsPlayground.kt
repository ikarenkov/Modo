package com.github.terrakok.modo.sample.screens.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.sample.screens.ButtonsState
import com.github.terrakok.modo.sample.screens.GroupedButtonsState
import com.github.terrakok.modo.sample.screens.MainScreen
import com.github.terrakok.modo.sample.screens.ModoButtonSpec
import com.github.terrakok.modo.sample.screens.base.ButtonsScreenContent
import com.github.terrakok.modo.sample.screens.base.LogLifecycle
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.StackNavContainer
import com.github.terrakok.modo.stack.forward
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogsPlayground(
    private val screenIndex: Int,
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {
    @Composable
    override fun Content(modifier: Modifier) {
        DialogsPlaygroundContent(screenIndex)
    }
}

@Composable
internal fun Screen.DialogsPlaygroundContent(screenIndex: Int, modifier: Modifier = Modifier) {
    LogLifecycle()
    ButtonsScreenContent(
        screenIndex = screenIndex,
        screenName = "DialogsPlayground",
        state = rememberDialogsButtons(LocalStackNavigation.current, screenIndex),
        modifier = modifier
    )
}

@Suppress("LongMethod")
@OptIn(ExperimentalModoApi::class)
@Composable
internal fun rememberDialogsButtons(
    navigation: StackNavContainer,
    i: Int
): GroupedButtonsState =
    remember {
        listOf(
            ModoButtonSpec("Forward") { navigation.forward(MainScreen(i + 1)) },
            ModoButtonSpec("Forward Dialogs") { navigation.forward(DialogsPlayground(i + 1)) },
            ModoButtonSpec("System Dialog") {
                navigation.forward(
                    SampleDialog(
                        i + 1,
                        dialogsPlayground = true,
                        systemDialog = true,
                        permanentDialog = false
                    )
                )
            },
            ModoButtonSpec("Custom Dialog") {
                navigation.forward(
                    SampleDialog(
                        i + 1,
                        dialogsPlayground = true,
                        systemDialog = false,
                        permanentDialog = false
                    )
                )
            },
            ModoButtonSpec("System Dialog perm") {
                navigation.forward(
                    SampleDialog(
                        i + 1,
                        dialogsPlayground = true,
                        systemDialog = true,
                        permanentDialog = true
                    )
                )
            },
            ModoButtonSpec("Custom Dialog perm") {
                navigation.forward(
                    SampleDialog(
                        i + 1,
                        dialogsPlayground = true,
                        systemDialog = false,
                        permanentDialog = true
                    )
                )
            },
            ModoButtonSpec("System Dialog Stack") {
                navigation.forward(SampleDialogWithStack(i + 1, systemDialog = true, permanentDialog = false))
            },
            ModoButtonSpec("Custom Dialog Stack") {
                navigation.forward(SampleDialogWithStack(i + 1, systemDialog = false, permanentDialog = false))
            },
            ModoButtonSpec("System Dialog Stack perm") {
                navigation.forward(
                    SampleDialogWithStack(
                        i + 1,
                        systemDialog = true,
                        permanentDialog = true
                    )
                )
            },
            ModoButtonSpec("Custom Dialog Stack perm") {
                navigation.forward(
                    SampleDialogWithStack(
                        i + 1,
                        systemDialog = false,
                        permanentDialog = true
                    )
                )
            },
            ModoButtonSpec("System BS") {
                navigation.forward(SampleBottomSheet(i + 1, systemDialog = true, permanentDialog = false))
            },
            ModoButtonSpec("Custom BS") {
                navigation.forward(SampleBottomSheet(i + 1, systemDialog = false, permanentDialog = false))
            },
            ModoButtonSpec("System BS perm") {
                navigation.forward(SampleBottomSheet(i + 1, systemDialog = true, permanentDialog = false))
            },
            ModoButtonSpec("Custom BS perm") {
                navigation.forward(SampleBottomSheet(i + 1, systemDialog = false, permanentDialog = false))
            },
            ModoButtonSpec("System BS Stack") {
                navigation.forward(SampleBottomSheetStack(i + 1, systemDialog = true, permanentDialog = false))
            },
            ModoButtonSpec("Custom BS Stack") {
                navigation.forward(SampleBottomSheetStack(i + 1, systemDialog = false, permanentDialog = false))
            },
            ModoButtonSpec("System BS Stack perm") {
                navigation.forward(
                    SampleBottomSheetStack(
                        i + 1,
                        systemDialog = true,
                        permanentDialog = false
                    )
                )
            },
            ModoButtonSpec("Custom BS Stack perm") {
                navigation.forward(
                    SampleBottomSheetStack(
                        i + 1,
                        systemDialog = false,
                        permanentDialog = false
                    )
                )
            },
            ModoButtonSpec("System Dialog random dim") {
                navigation.forward(SystemDialogWithCustomDimSample(i + 1))
            },
            ModoButtonSpec("M3 BottomSheet") {
                navigation.forward(M3BottomSheet(i + 1))
            },
        ).let {
            ButtonsState(it)
        }
    }