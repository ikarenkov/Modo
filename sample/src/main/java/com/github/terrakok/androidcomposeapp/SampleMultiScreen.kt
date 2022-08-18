package com.github.terrakok.androidcomposeapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.android.compose.ComposeMultiScreen
import com.github.terrakok.modo.selectContainer

class SampleMultiScreen(i: Int) : ComposeMultiScreen(
    "m_$i",
    1,
    SampleScreen(1),
    SampleScreen(1),
    SampleScreen(1),
    reducer = CustomReducer()
) {

    @Composable
    private fun Tab(modifier: Modifier, stackIndex: Int, i: Int) = Text(
        modifier = modifier
            .clickable { selectContainer(i) }
            .background(if (stackIndex == i) Color.LightGray else Color.White)
            .padding(16.dp),
        textAlign = TextAlign.Center,
        fontStyle = if (stackIndex == i) FontStyle.Italic else FontStyle.Normal,
        color = if (stackIndex == i) Color.Red else Color.Black,
        text = "Tab $i"
    )

    @Composable
    override fun Content(stackCount: Int, selectedStack: Int, screenContent: @Composable () -> Unit) {
        Column {
            Box(modifier = Modifier.weight(1f)) {
                screenContent()
            }
            Row {
                repeat(stackCount) {
                    Tab(modifier = Modifier.weight(1f), selectedStack, it)
                }
                Text(
                    modifier = Modifier
                        .clickable { dispatch(AddTab(stackCount.toString(), SampleScreen(1))) }
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    text = "[+]"
                )
            }
        }
    }
}