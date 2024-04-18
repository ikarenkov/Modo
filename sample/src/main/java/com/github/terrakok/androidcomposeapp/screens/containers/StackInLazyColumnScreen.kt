package com.github.terrakok.androidcomposeapp.screens.containers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.github.terrakok.androidcomposeapp.screens.MainScreen
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.list.ListNavModel
import com.github.terrakok.modo.list.ListNavigationAction
import com.github.terrakok.modo.list.ListNavigationState
import kotlinx.parcelize.Parcelize

/**
 * Sample of embedding screens inside LazyColumn.
 */
@Parcelize
class StackInLazyColumnScreen(
    private val navModel: ListNavModel = ListNavModel(
        buildList {
            repeat(5) {
                add(SampleStack(MainScreen(0)))
            }
        }
    )
) : ContainerScreen<ListNavigationState, ListNavigationAction>(
    navModel
) {
    @Composable
    override fun Content(modifier: Modifier) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { dispatch(ListNavigationAction.Add(SampleStack(MainScreen(0)))) }) {
                    Icon(painter = rememberVectorPainter(image = Icons.Default.Add), contentDescription = "Add screen")
                }
            }
        ) {
            LazyColumn(contentPadding = it) {
                // Defining key is vital - you are gonna have crush on screens removal instead
                items(navigationState.screens, key = { it.screenKey }) { screen ->
                    Box {
                        InternalContent(
                            screen = screen,
                            modifier = Modifier
                                .padding(16.dp)
                                .height(IntrinsicSize.Min)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        IconButton(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp),
                            onClick = { dispatch(ListNavigationAction.Remove(screen)) }
                        ) {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Default.Close),
                                contentDescription = "Close screen"
                            )
                        }
                    }
                }
            }
        }
    }
}