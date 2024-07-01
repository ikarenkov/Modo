package com.github.terrakok.modo.sample.screens.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.sample.screens.base.ButtonsScreenContent
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.StackScreen
import com.github.terrakok.modo.stack.back
import kotlinx.parcelize.Parcelize

@Parcelize
@OptIn(ExperimentalModoApi::class)
class M3BottomSheet(
    private val screenIndex: Int,
    override val screenKey: ScreenKey = generateScreenKey()
) : DialogScreen {

    override fun provideDialogConfig(): DialogScreen.DialogConfig = DialogScreen.DialogConfig.Custom

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(modifier: Modifier) {
        val stackScreen = LocalStackNavigation.current
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = {
                stackScreen.back()
            },
            sheetState = sheetState,
            dragHandle = null
        ) {
            ButtonsScreenContent(
                screenIndex = screenIndex,
                screenName = "SampleDialog",
                state = rememberDialogsButtons(LocalContainerScreen.current as StackScreen, screenIndex),
                modifier = modifier
                    .fillMaxSize()
                    .align(Alignment.CenterHorizontally)
                    // TODO: deal with A11Y and remove it from A11Y tree
                    .clickable(
                        enabled = false,
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        indication = null
                    ) {}
            )
        }
    }
}