package io.github.ikarenkov.workshop.screens.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.sharp.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// TODO: Workshop 3.1.1 - create main tab screen inheriting from MultiScreen
// TODO: Workshop 3.1.2 - define initial state in constructor and pass it as argument tu super
// TODO: Workshop 3.1.3 - use @Parcelize annotation to make MainTabScreen class Parcelable
// TODO: Workshop 3.1.4 - navigate to this screen from SampleScreen
// TODO: Workshop 3.2.1 - implement Content function with MainTabContent composable
// TODO: Workshop 3.2.2 - display selected screen inside MainTabContent using SelectedScreen composable
// TODO: Workshop 3.3 - navigate between tabs using selectContainer function
// TODO: Workshop 3.4 - use navigation state to access selected tab position
// TODO: Workshop 3.5 - support animation using build-in SlideTransition

@Composable
fun MainTabContent(
    selectedTabPos: Int,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomAppBar {
                for ((pos, tab) in MainTabs.entries.withIndex()) {
                    IconButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onTabClick(pos)
                        },
                    ) {
                        val contentColor = LocalContentColor.current
                        val color by animateColorAsState(
                            contentColor.copy(
                                alpha = if (pos == selectedTabPos) contentColor.alpha else 0.5f
                            )
                        )
                        Icon(
                            rememberVectorPainter(tab.icon),
                            tint = color,
                            contentDescription = tab.title
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}

enum class MainTabs(
    val icon: ImageVector,
    val title: String
) {
    HOME(Icons.Sharp.Home, "Home"),
    PROFILE(Icons.Default.Face, "Profile")
}

@Preview
@Composable
private fun PreviewMainTabsContent() {
    MainTabContent(
        selectedTabPos = 0,
        onTabClick = {},
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Selected screen", Modifier.padding(16.dp))
        }
    }
}