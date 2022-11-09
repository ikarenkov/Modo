package com.github.terrakok.androidcomposeapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.stack.StackScreen
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalModoApi::class)
@Parcelize
class SampleDialog(
    private val i: Int,
    override val screenKey: ScreenKey = generateScreenKey()
) : DialogScreen {

    @Composable
    override fun Content() {
        Box(
            Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Cyan)
        ) {
            SampleContent(i, LocalContainerScreen.current as StackScreen)
        }
    }
}