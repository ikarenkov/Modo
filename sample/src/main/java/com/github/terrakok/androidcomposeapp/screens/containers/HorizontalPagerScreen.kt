package com.github.terrakok.androidcomposeapp.screens.containers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.lifecycle.LifecycleEventObserver
import com.github.terrakok.androidcomposeapp.screens.SampleScreen
import com.github.terrakok.androidcomposeapp.screens.containers.custom.InnerContent
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.NavModel
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.lifecycle.LifecycleScreenEffect
import com.github.terrakok.modo.list.ListNavigationAction
import com.github.terrakok.modo.list.ListNavigationState
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import logcat.logcat

@Parcelize
class HorizontalPagerScreen(
    private val navModel: NavModel<ListNavigationState, ListNavigationAction> = NavModel(
        ListNavigationState(
            listOf(
                SampleStack(SampleScreen(0)),
                PageScreen(canBeRemoved = true),
                PageScreen(canBeRemoved = true),
            )
        )
    )
) : ContainerScreen<ListNavigationState, ListNavigationAction>(navModel) {

    @OptIn(ExperimentalFoundationApi::class)
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
                    onClick = { dispatch(AddStack) },
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                ) {
                    Icon(painter = rememberVectorPainter(image = Icons.Default.Add), contentDescription = "Add")
                }
            }
            HorizontalPager(
                state = pagerState
            ) { pos ->
                InternalContent(screen = navigationState.screens[pos], Modifier.fillMaxSize())
            }
        }
    }

    object AddStack : ListNavigationAction {
        override fun reduce(oldState: ListNavigationState): ListNavigationState = ListNavigationState(
            oldState.screens + SampleStack(SampleScreen(0))
        )
    }
}

@Parcelize
internal class PageScreen(
    val canBeRemoved: Boolean,
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @OptIn(ExperimentalModoApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        val parent = LocalContainerScreen.current as HorizontalPagerScreen
        LifecycleScreenEffect {
            LifecycleEventObserver { _, event ->
                logcat(tag = "PageScreen Lifecycle") { "$screenKey $event" }
            }
        }
        InnerContent(
            title = screenKey.value,
            onRemoveClick = takeIf { canBeRemoved }?.let {
                {
                    parent.dispatch(
                        ListNavigationAction {
                            ListNavigationState(it.screens.filter { it != this })
                        }
                    )
                }
            },
            modifier = modifier
        )
    }

}