package com.github.terrakok.modo.sample.screens.containers.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.lazylist.screenItems
import com.github.terrakok.modo.list.ListNavModel
import com.github.terrakok.modo.list.ListNavigationContainerScreen
import com.github.terrakok.modo.list.addScreens
import com.github.terrakok.modo.list.removeScreens
import com.github.terrakok.modo.sample.components.CancelButton
import kotlinx.parcelize.Parcelize

/**
 * Sample of usage build-in ListNavigation
 */
@Parcelize
internal class SampleListNavigation(
    @Suppress("MagicNumber")
    private val navModel: ListNavModel = ListNavModel(List(5) { ListInnerScreen() })
) : ListNavigationContainerScreen(navModel) {

    @Composable
    override fun Content(modifier: Modifier) {
        Column(modifier) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                screenItems(navigationState.screens) { screen ->
                    Box(
                        Modifier
                            .animateItem()
                    ) {
                        InternalContent(
                            screen = screen,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )
                        CancelButton(
                            modifier = Modifier.align(Alignment.TopEnd),
                            onClick = {
                                removeScreens(screen)
                            },
                            contentDescription = "Remove screen"
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        if (navigationState.screens.size > 1) {
                            addScreens(pos = 1, ListInnerScreen(), ListInnerScreen(), ListInnerScreen())
                        } else {
                            addScreens(ListInnerScreen(), ListInnerScreen(), ListInnerScreen())
                        }
                    }
                ) {
                    Text(text = "Add screens")
                }
            }
        }
    }
}