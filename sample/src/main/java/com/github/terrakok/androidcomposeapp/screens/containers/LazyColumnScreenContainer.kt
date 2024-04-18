package com.github.terrakok.androidcomposeapp.screens.containers

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.terrakok.androidcomposeapp.screens.SampleScreen
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.list.ListNavModel
import com.github.terrakok.modo.list.ListNavigationAction
import com.github.terrakok.modo.list.ListNavigationState
import kotlinx.parcelize.Parcelize

/**
 * Sample of embedding screens inside LazyColumn.
 */
@Parcelize
class LazyColumnScreenContainer(
    private val navModel: ListNavModel = ListNavModel(
        buildList {
            repeat(5) {
                add(SampleStack(SampleScreen(0)))
            }
        }
    )
) : ContainerScreen<ListNavigationState, ListNavigationAction>(
    navModel
) {
    @Composable
    override fun Content(modifier: Modifier) {
        LazyColumn {
            items(navigationState.screens) { screen ->
                InternalContent(
                    screen = screen,
                    modifier = Modifier
                        .padding(16.dp)
                        .height(500.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}