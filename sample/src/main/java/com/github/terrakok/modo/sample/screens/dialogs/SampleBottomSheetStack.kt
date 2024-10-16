package com.github.terrakok.modo.sample.screens.dialogs

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.sample.SlideTransition
import com.github.terrakok.modo.sample.screens.MainScreen
import com.github.terrakok.modo.stack.StackNavModel
import com.github.terrakok.modo.stack.StackScreen
import com.github.terrakok.modo.stack.back
import com.github.terrakok.modo.util.currentOrThrow
import kotlinx.parcelize.Parcelize

/**
 * The sample of BottomSheet that contains stack of screens inside.
 */
@OptIn(ExperimentalModoApi::class)
@Parcelize
class SampleBottomSheetStack(
    private val i: Int,
    private val systemDialog: Boolean = true,
    override val permanentDialog: Boolean = false,
    private val navModel: StackNavModel = StackNavModel(MainScreen(i + 1))
) : StackScreen(navModel), DialogScreen {

    override fun provideDialogConfig(): DialogScreen.DialogConfig = if (systemDialog) {
        DialogScreen.DialogConfig.System(
            useSystemDim = false,
            dialogProperties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        )
    } else {
        DialogScreen.DialogConfig.Custom
    }

    override fun provideDialogPlaceholderScreen(): DialogScreen? = null

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        SetupSystemBar()
        val navigation = LocalContainerScreen.currentOrThrow as StackScreen
        val state = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.HalfExpanded)
        LaunchedEffect(key1 = state.currentValue) {
            if (state.currentValue == ModalBottomSheetValue.Hidden) {
                navigation.back()
            }
        }
        ModalBottomSheetLayout(
            sheetContent = {
                TopScreenContent { modifier ->
                    SlideTransition(modifier)
                }
            },
            sheetState = state,
            modifier = modifier
        ) {
        }
    }
}