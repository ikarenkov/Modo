package com.github.terrakok.androidcomposeapp.screens

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.back
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalModoApi::class)
@Parcelize
class SampleCustomBottomSheet(
    private val i: Int,
    override val screenKey: ScreenKey = generateScreenKey()
) : DialogScreen {

    override fun provideDialogConfig() = DialogScreen.DialogConfig.Custom

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
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
                    canOpenFragment = false,
                    navigation = navigation,
                    modifier = modifier
                )
            },
            sheetState = state
        ) {
        }
    }
}