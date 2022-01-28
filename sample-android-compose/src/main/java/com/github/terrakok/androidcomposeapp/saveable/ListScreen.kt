package com.github.terrakok.androidcomposeapp.saveable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.terrakok.androidcomposeapp.App
import com.github.terrakok.modo.Forward
import com.github.terrakok.modo.android.compose.ComposeScreen
import com.github.terrakok.modo.android.compose.uniqueScreenKey
import kotlinx.parcelize.Parcelize

@Parcelize
class ListScreen(
    override val screenKey: String = uniqueScreenKey
) : ComposeScreen("ListScreen") {

    @Composable
    override fun Content() {
        ProfilesScreen { App.INSTANCE.modo.dispatch(Forward(DetailsScreen(it))) }
    }

}

@Composable
fun ProfilesScreen(openDetails: (String) -> Unit) {
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
                        .clickable { openDetails(it.toString()) }
                        .padding(16.dp))
            }
        }
    }
}