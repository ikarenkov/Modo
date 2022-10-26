package com.github.terrakok.androidcomposeapp

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
import com.github.terrakok.modo.android.compose.ComposeScreen
import com.github.terrakok.modo.android.compose.LocalContainerScreen
import com.github.terrakok.modo.android.compose.generateScreenKey
import com.github.terrakok.modo.forward
import kotlinx.parcelize.Parcelize

@Parcelize
class ListScreen(
    override val screenKey: String = generateScreenKey()
) : ComposeScreen {

    @Composable
    override fun Content() {
        val conScreen = LocalContainerScreen.current
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
                             .clickable { conScreen.forward(DetailsScreen(it.toString())) }
                             .padding(16.dp))
                }
            }
        }
    }
}

@Parcelize
class DetailsScreen(
    private val userId: String,
    override val screenKey: String = generateScreenKey()
) : ComposeScreen {

    @Composable
    override fun Content() {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.align(Alignment.Center)) {
                Text(text = "Profile details $userId")
            }
        }
    }
}