package com.github.terrakok.modo.sample.screens.containers

import android.os.Parcelable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.NavModel
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.list.ListNavigationState
import com.github.terrakok.modo.list.removeScreens
import com.github.terrakok.modo.sample.components.CancelButton
import com.github.terrakok.modo.sample.screens.MainScreen
import kotlinx.coroutines.launch
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class HorizontalPagerScreen(
    private val navModel: NavModel<ListNavigationState> = NavModel(
        ListNavigationState(
            listOf(
                SampleStack(MainScreen(0)),
                SampleStack(MainScreen(0)),
                SampleStack(MainScreen(0)),
            )
        )
    )
) : ContainerScreen<ListNavigationState>(navModel), Parcelable {

    @Composable
    override fun Content(modifier: Modifier) {
        val pagerState = rememberPagerState {
            navigationState.screens.size
        }
        val coroutineScope = rememberCoroutineScope()
        Column(modifier) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
                    )
                }
            ) {
                navigationState.screens.forEachIndexed { index, screen ->
                    Tab(
                        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                        selected = index == pagerState.currentPage,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(text = "Tab $index") }
                    )
                }
                IconButton(
                    onClick = {
                        dispatch { oldState ->
                            ListNavigationState(
                                oldState.screens + SampleStack(MainScreen(0))
                            )
                        }
                    },
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                ) {
                    Icon(painter = rememberVectorPainter(image = Icons.Default.Add), contentDescription = "Add")
                }
            }
            HorizontalPager(
                state = pagerState,
                key = { navigationState.screens[it].screenKey }
            ) { pos ->
                Box {
                    val screen = navigationState.screens[pos]
                    InternalContent(screen = screen, Modifier.fillMaxSize())
                    CancelButton(
                        onClick = { removeScreens(screen) },
                        contentDescription = "Close screen",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    )
                }
            }
        }
    }

}