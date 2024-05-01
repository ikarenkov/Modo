package com.github.terrakok.androidcomposeapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.forward
import kotlinx.parcelize.Parcelize

@Parcelize
class ListScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Suppress("MagicNumber")
    @Composable
    override fun Content(modifier: Modifier) {
        val navigation = LocalStackNavigation.current
        Box(Modifier.fillMaxSize()) {
            val lazyColumnState = rememberSaveable(saver = LazyListState.Saver) {
                LazyListState(
                    0,
                    0
                )
            }
            LazyColumn(
                Modifier.fillMaxSize(),
                lazyColumnState
            ) {
                items((1..100).toList()) {
                    Text(text = "Item $it",
                         Modifier
                             .fillMaxWidth()
                             .clickable { navigation.forward(DetailsScreen(it.toString())) }
                             .padding(16.dp))
                }
            }
        }
    }
}

@Parcelize
class DetailsScreen(
    private val userId: String,
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.align(Alignment.Center)) {
                Text(text = "Profile details $userId")
            }
        }
    }
}