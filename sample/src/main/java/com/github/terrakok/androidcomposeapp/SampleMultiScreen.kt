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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.MultiNavigation
import com.github.terrakok.modo.android.compose.ComposeContainerScreen
import com.github.terrakok.modo.android.compose.Stack
import com.github.terrakok.modo.navigator
import com.github.terrakok.modo.selectContainer

class SampleMultiScreen(i: Int) : ComposeContainerScreen<MultiNavigation>(
    "m_$i",
    MultiNavigation(
        listOf(
            Stack("s_1", SampleScreen(1)),
            Stack("s_2", SampleScreen(1)),
            Stack("s_3", SampleScreen(1)),
        ), 1
    ),
    CustomReducer()
) {
    @Composable
    override fun Content(state: MultiNavigation, screenContent: @Composable () -> Unit) {
        val stackCount = state.containers.size
        Column {
            Box(modifier = Modifier.weight(1f)) {
                screenContent()
            }
            Row {
                repeat(stackCount) {
                    Tab(modifier = Modifier.weight(1f), state.selected, it)
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

    @Composable
    private fun Tab(modifier: Modifier, selected: Int, i: Int) = Text(
        modifier = modifier
            .clickable { navigator.selectContainer(i) }
            .background(if (selected == i) Color.LightGray else Color.White)
            .padding(16.dp),
        textAlign = TextAlign.Center,
        fontStyle = if (selected == i) FontStyle.Italic else FontStyle.Normal,
        color = if (selected == i) Color.Red else Color.Black,
        text = "Tab $i"
    )
}

@Preview
@Composable
fun PreviewSampleMultiScreen() {
    SampleMultiScreen(1).Content()
}