package com.github.terrakok.modo.sample.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.stack.LocalStackNavigation
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalModoApi::class)
@Parcelize
class SamplePermanentDialog(
    private val i: Int,
    override val screenKey: ScreenKey = generateScreenKey()
) : DialogScreen {

    override val permanentDialog: Boolean get() = true

    override fun provideDialogConfig(): DialogScreen.DialogConfig = DialogScreen.DialogConfig.System(
        useSystemDim = true,
        dialogProperties = DialogProperties(
            usePlatformDefaultWidth = true,
            decorFitsSystemWindows = true
        )
    )

    @Suppress("ModifierNotUsedAtRoot")
    @Composable
    override fun Content(modifier: Modifier) {
        Box(
            Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            MainScreenContent(i, screenKey, LocalStackNavigation.current, modifier)
        }
    }
}