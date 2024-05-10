package com.github.terrakok.modo.sample.screens.containers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.list.ListNavModel
import com.github.terrakok.modo.list.ListNavigationAction
import com.github.terrakok.modo.list.ListNavigationState
import com.github.terrakok.modo.sample.components.CancelButton
import com.github.terrakok.modo.sample.screens.MainScreen
import kotlinx.parcelize.Parcelize

/**
 * Sample of embedding screens inside LazyColumn.
 */
@Parcelize
class StackInLazyColumnScreen(
    @Suppress("MagicNumber")
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
    @Suppress("LongMethod")
    @Composable
    override fun Content(modifier: Modifier) {
        val lazyColumnState = rememberLazyListState()
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { dispatch(ListNavigationAction.Add(SampleStack(MainScreen(0)))) }) {
                    Icon(painter = rememberVectorPainter(image = Icons.Default.Add), contentDescription = "Add screen")
                }
            },
            topBar = {
                TopAppBar(
                    windowInsets = WindowInsets.statusBars,
                    title = {
                        Text(
                            text = "Stacks in LazyColumn",
                        )
                    }
                )
            },
            drawerElevation = if (remember { derivedStateOf { lazyColumnState.firstVisibleItemIndex } }.value > 0) 16.dp else 0.dp
        ) {
            LazyColumn(
                state = lazyColumnState,
                contentPadding = it,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Button(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        onClick = {
                            dispatch(ListNavigationAction.Add(pos = 0, SampleStack(MainScreen(0))))
                        }
                    ) {
                        Text(text = "Add item", modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }
                // Defining key is vital - you are gonna have crush on screens removal instead
                items(navigationState.screens, key = { it.screenKey }) { screen ->
                    Box {
                        InternalContent(
                            screen = screen,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .height(IntrinsicSize.Min)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        CancelButton(
                            onClick = { dispatch(ListNavigationAction.Remove(screen)) },
                            contentDescription = "Close screen",
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp),
                        )
                    }
                }

                item {
                    Button(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.navigationBars),
                        onClick = {
                            dispatch(ListNavigationAction.Add(SampleStack(MainScreen(0))))
                        }
                    ) {
                        Text(text = "Add item", modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }
            }
        }
    }
}