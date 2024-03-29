package com.github.terrakok.androidcomposeapp.screens.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.github.terrakok.androidcomposeapp.SlideTransition
import com.github.terrakok.androidcomposeapp.screens.SampleScreen
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.stack.StackNavModel
import com.github.terrakok.modo.stack.StackScreen
import kotlinx.parcelize.Parcelize

/**
 * The sample of Dialog with nested navigation.
 */
@OptIn(ExperimentalModoApi::class)
@Parcelize
class SampleDialogWithStack(
    private val i: Int,
    private val systemDialog: Boolean = true,
    override val permanentDialog: Boolean = false,
    private val navModel: StackNavModel = StackNavModel(SampleScreen(i + 1))
) : StackScreen(navModel), DialogScreen {

    override fun provideDialogConfig(): DialogScreen.DialogConfig = if (systemDialog) {
        DialogScreen.DialogConfig.System(
            useSystemDim = true,
            dialogProperties = DialogProperties(
                usePlatformDefaultWidth = true,
                decorFitsSystemWindows = true
            )
        )
    } else {
        DialogScreen.DialogConfig.Custom
    }

    @Composable
    override fun Content(modifier: Modifier) {
        Box(
            Modifier
                .fillMaxHeight(0.7f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            TopScreenContent {
                SlideTransition()
            }
        }
    }
}