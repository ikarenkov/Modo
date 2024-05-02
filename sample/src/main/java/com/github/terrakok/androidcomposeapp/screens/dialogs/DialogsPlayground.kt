package com.github.terrakok.androidcomposeapp.screens.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.terrakok.androidcomposeapp.screens.ButtonsState
import com.github.terrakok.androidcomposeapp.screens.GroupedButtonsState
import com.github.terrakok.androidcomposeapp.screens.MainScreen
import com.github.terrakok.androidcomposeapp.screens.base.ButtonsScreenContent
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
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
        DialogsPlaygroundContent(screenIndex, screenKey)
    }
}

@Composable
internal fun DialogsPlaygroundContent(screenIndex: Int, screenKey: ScreenKey, modifier: Modifier = Modifier) {
    ButtonsScreenContent(
        screenIndex = screenIndex,
        screenName = "DialogsPlayground",
        screenKey = screenKey,
        state = rememberDialogsButtons(LocalStackNavigation.current, screenIndex),
        modifier = modifier
    )
}

@OptIn(ExperimentalModoApi::class)
@Composable
internal fun rememberDialogsButtons(
    navigation: StackNavContainer,
    i: Int
): GroupedButtonsState =
    remember {
        listOf(
            "Forward" to { navigation.forward(MainScreen(i + 1)) },
            "Forward Dialogs" to { navigation.forward(DialogsPlayground(i + 1)) },
            "System Dialog" to {
                navigation.forward(
                    SampleDialog(
                        i + 1,
                        dialogsPlayground = true,
                        systemDialog = true,
                        permanentDialog = false
                    )
                )
            },
            "Custom Dialog" to {
                navigation.forward(
                    SampleDialog(
                        i + 1,
                        dialogsPlayground = true,
                        systemDialog = false,
                        permanentDialog = false
                    )
                )
            },
            "System Dialog perm" to {
                navigation.forward(
                    SampleDialog(
                        i + 1,
                        dialogsPlayground = true,
                        systemDialog = true,
                        permanentDialog = true
                    )
                )
            },
            "Custom Dialog perm" to {
                navigation.forward(
                    SampleDialog(
                        i + 1,
                        dialogsPlayground = true,
                        systemDialog = false,
                        permanentDialog = true
                    )
                )
            },
            "System Dialog Stack" to { navigation.forward(SampleDialogWithStack(i + 1, systemDialog = true, permanentDialog = false)) },
            "Custom Dialog Stack" to { navigation.forward(SampleDialogWithStack(i + 1, systemDialog = false, permanentDialog = false)) },
            "System Dialog Stack perm" to { navigation.forward(SampleDialogWithStack(i + 1, systemDialog = true, permanentDialog = true)) },
            "Custom Dialog Stack perm" to { navigation.forward(SampleDialogWithStack(i + 1, systemDialog = false, permanentDialog = true)) },
            "System BS" to { navigation.forward(SampleBottomSheet(i + 1, systemDialog = true, permanentDialog = false)) },
            "Custom BS" to { navigation.forward(SampleBottomSheet(i + 1, systemDialog = false, permanentDialog = false)) },
            "System BS perm" to { navigation.forward(SampleBottomSheet(i + 1, systemDialog = true, permanentDialog = false)) },
            "Custom BS perm" to { navigation.forward(SampleBottomSheet(i + 1, systemDialog = false, permanentDialog = false)) },
            "System BS Stack" to { navigation.forward(SampleBottomSheetStack(i + 1, systemDialog = true, permanentDialog = false)) },
            "Custom BS Stack" to { navigation.forward(SampleBottomSheetStack(i + 1, systemDialog = false, permanentDialog = false)) },
            "System BS Stack perm" to { navigation.forward(SampleBottomSheetStack(i + 1, systemDialog = true, permanentDialog = false)) },
            "Custom BS Stack perm" to { navigation.forward(SampleBottomSheetStack(i + 1, systemDialog = false, permanentDialog = false)) },
            "System Dialog random dim" to { navigation.forward(SystemDialogWithCustomDimSample(i + 1)) },
        ).let {
            ButtonsState(it)
        }
    }