package com.github.terrakok.androidcomposeapp.screens.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.terrakok.androidcomposeapp.screens.ButtonsState
import com.github.terrakok.androidcomposeapp.screens.MainScreen
import com.github.terrakok.androidcomposeapp.screens.base.MainButtonsContent
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.stack.StackScreen
import com.github.terrakok.modo.stack.forward
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogsPlayground(
    private val screenIndex: Int,
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {
    @Composable
    override fun Content(modifier: Modifier) {
        DialogsPlaygroundContent(screenIndex, screenKey)
    }
}

@Composable
internal fun DialogsPlaygroundContent(screenIndex: Int, screenKey: ScreenKey, modifier: Modifier = Modifier) {
    MainButtonsContent(
        screenIndex = screenIndex,
        screenKey = screenKey,
        buttonsState = rememberDialogsButtons(LocalContainerScreen.current as StackScreen, screenIndex),
        modifier = modifier
    )
}

@OptIn(ExperimentalModoApi::class)
@Composable
internal fun rememberDialogsButtons(
    navigator: StackScreen,
    i: Int
): ButtonsState =
    remember {
        listOf(
            "Forward" to { navigator.forward(MainScreen(i + 1)) },
            "Forward Dialogs" to { navigator.forward(DialogsPlayground(i + 1)) },
            "System Dialog" to {
                navigator.forward(
                    SampleDialog(
                        i + 1,
                        dialogsPlayground = true,
                        systemDialog = true,
                        permanentDialog = false
                    )
                )
            },
            "Custom Dialog" to {
                navigator.forward(
                    SampleDialog(
                        i + 1,
                        dialogsPlayground = true,
                        systemDialog = false,
                        permanentDialog = false
                    )
                )
            },
            "System Dialog perm" to {
                navigator.forward(
                    SampleDialog(
                        i + 1,
                        dialogsPlayground = true,
                        systemDialog = true,
                        permanentDialog = true
                    )
                )
            },
            "Custom Dialog perm" to {
                navigator.forward(
                    SampleDialog(
                        i + 1,
                        dialogsPlayground = true,
                        systemDialog = false,
                        permanentDialog = true
                    )
                )
            },
            "System Dialog Stack" to { navigator.forward(SampleDialogWithStack(i + 1, systemDialog = true, permanentDialog = false)) },
            "Custom Dialog Stack" to { navigator.forward(SampleDialogWithStack(i + 1, systemDialog = false, permanentDialog = false)) },
            "System Dialog Stack perm" to { navigator.forward(SampleDialogWithStack(i + 1, systemDialog = true, permanentDialog = true)) },
            "Custom Dialog Stack perm" to { navigator.forward(SampleDialogWithStack(i + 1, systemDialog = false, permanentDialog = true)) },
            "System BS" to { navigator.forward(SampleBottomSheet(i + 1, systemDialog = true, permanentDialog = false)) },
            "Custom BS" to { navigator.forward(SampleBottomSheet(i + 1, systemDialog = false, permanentDialog = false)) },
            "System BS perm" to { navigator.forward(SampleBottomSheet(i + 1, systemDialog = true, permanentDialog = false)) },
            "Custom BS perm" to { navigator.forward(SampleBottomSheet(i + 1, systemDialog = false, permanentDialog = false)) },
            "System BS Stack" to { navigator.forward(SampleBottomSheetStack(i + 1, systemDialog = true, permanentDialog = false)) },
            "Custom BS Stack" to { navigator.forward(SampleBottomSheetStack(i + 1, systemDialog = false, permanentDialog = false)) },
            "System BS Stack perm" to { navigator.forward(SampleBottomSheetStack(i + 1, systemDialog = true, permanentDialog = false)) },
            "Custom BS Stack perm" to { navigator.forward(SampleBottomSheetStack(i + 1, systemDialog = false, permanentDialog = false)) },
            "System Dialog random dim" to { navigator.forward(SystemDialogWithCustomDimSample(i + 1)) },
        ).let {
            ButtonsState(it)
        }
    }