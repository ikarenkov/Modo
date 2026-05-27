package com.github.terrakok.modo.sample.screens.containers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.multiscreen.MultiScreen
import com.github.terrakok.modo.multiscreen.MultiScreenNavModel
import com.github.terrakok.modo.multiscreen.selectScreen
import com.github.terrakok.modo.sample.components.CancelButton
import com.github.terrakok.modo.sample.screens.MainScreen
import kotlinx.parcelize.Parcelize

@Suppress("MagicNumber")
@Parcelize
internal class SampleMultiScreen(
    private val navModel: MultiScreenNavModel = MultiScreenNavModel(
        screens = listOf(
            SampleStack(MainScreen(1)),
            SampleStack(MainScreen(2)),
            SampleStack(MainScreen(3)),
        ),
        selected = 1
    )
) : MultiScreen(navModel) {

    @Composable
    override fun Content(modifier: Modifier) {
        var showAllStacks by rememberSaveable {
            mutableStateOf(false)
        }
        Column(modifier) {
            Box(Modifier.weight(1f)) {
                TopContent(showAllStacks)
                if (navigationState.screens.size > 1) {
                    CancelButton(
                        onClick = { dispatch(RemoveTabReducer(navigationState.selected)) },
                        contentDescription = "Cansel screen",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .windowInsetsPadding(WindowInsets.statusBars)
                    )
                }
            }
            Row(Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
                Text(
                    modifier = Modifier
                        .clickable { showAllStacks = !showAllStacks }
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    text = "🪄"
                )
                repeat(navigationState.screens.size) { tabPos ->
                    Tab(
                        modifier = Modifier.weight(1f),
                        isSelected = navigationState.selected == tabPos,
                        tabPos = tabPos,
                        onTabClick = { selectScreen(tabPos) }
                    )
                }
                Text(
                    modifier = Modifier
                        .clickable { addTab(MainScreen(1)) }
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    text = "[+]"
                )
            }
        }
    }

    @Composable
    fun TopContent(showAllStacks: Boolean, modifier: Modifier = Modifier) {
        Box(modifier = modifier) {
            Row {
                for ((pos, container) in navigationState.screens.withIndex()) {
                    if (showAllStacks || pos == navigationState.selected) {
                        Box(modifier = Modifier.weight(1f)) {
                            // внутри вызывается используется SaveableStateProvider с одинаковым ключом для экрана
                            Content(container, Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun Tab(
        isSelected: Boolean,
        tabPos: Int,
        modifier: Modifier = Modifier,
        onTabClick: () -> Unit,
    ) = Text(
        modifier = modifier
            .clickable(onClick = onTabClick)
            .background(if (isSelected) Color.LightGray else Color.White)
            .padding(16.dp),
        textAlign = TextAlign.Center,
        fontStyle = if (isSelected) FontStyle.Italic else FontStyle.Normal,
        color = if (isSelected) Color.Red else Color.Black,
        text = "Tab $tabPos"
    )

}

@Preview
@Composable
private fun PreviewSampleMultiScreen() {
    SampleMultiScreen().Content(Modifier)
}