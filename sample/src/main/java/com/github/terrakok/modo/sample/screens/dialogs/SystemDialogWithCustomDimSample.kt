package com.github.terrakok.modo.sample.screens.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.sample.randomBackground
import kotlinx.parcelize.Parcelize

@ExperimentalModoApi
@Parcelize
class SystemDialogWithCustomDimSample(
    private val screenIndex: Int,
    override val screenKey: ScreenKey = generateScreenKey()
) : DialogScreen {

    override fun provideDialogConfig(): DialogScreen.DialogConfig = DialogScreen.DialogConfig.System(
        useSystemDim = false,
        dialogProperties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    )

    @Composable
    override fun Content(modifier: Modifier) {
        SetupSystemBar()
        // We need to manually draw under the system bar because there is no other way to draw under it.
        Box(
            modifier = modifier
                .fillMaxSize()
                .randomBackground(alpha = 0.5f)
        ) {
            Box(
                Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 50.dp, vertical = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
            ) {
                DialogsPlaygroundContent(screenIndex, screenKey)
            }
        }
    }

}