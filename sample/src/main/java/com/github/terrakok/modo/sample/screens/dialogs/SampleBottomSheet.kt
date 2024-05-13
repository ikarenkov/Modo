package com.github.terrakok.modo.sample.screens.dialogs

import androidx.compose.foundation.layout.fillMaxSize
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
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.sample.screens.MainScreenContent
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.back
import kotlinx.parcelize.Parcelize

/**
 * The sample of BottomSheet that contains stack of screens inside.
 */
@OptIn(ExperimentalModoApi::class)
@Parcelize
class SampleBottomSheet(
    private val i: Int,
    private val systemDialog: Boolean = true,
    override val permanentDialog: Boolean = false,
    override val screenKey: ScreenKey = generateScreenKey()
) : DialogScreen {

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

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        SetupSystemBar()
        val navigation = LocalStackNavigation.current
        val state = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.HalfExpanded)
        LaunchedEffect(key1 = state.currentValue) {
            if (state.currentValue == ModalBottomSheetValue.Hidden) {
                navigation.back()
            }
        }
        ModalBottomSheetLayout(
            sheetContent = {
                MainScreenContent(
                    screenIndex = i,
                    screenKey = screenKey,
                    navigation = navigation,
                    modifier = Modifier.fillMaxSize()
                )
            },
            sheetState = state,
            modifier = modifier.fillMaxSize()
        ) {
        }
    }
}