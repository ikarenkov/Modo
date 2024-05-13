package com.github.terrakok.modo.sample.screens.containers.custom

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
internal class MovableContentPlaygroundScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @IgnoredOnParcel
    private var counter = 3

    @IgnoredOnParcel
    private var list = mutableStateListOf<Int>().apply {
        for (i in 0..<counter) add(i)
    }

    @Composable
    override fun Content(modifier: Modifier) {
        val listComposable = list.movable { item ->
            InnerContent(
                title = "Item $item",
                onRemoveClick = {
                    list.remove(item)
                },
                modifier = Modifier
                    .height(height = 50.dp)
                    .fillMaxWidth()
            )
        }
        Column(modifier.windowInsetsPadding(WindowInsets.systemBars)) {
            Button(onClick = { list.removeFirst() }) {
                Text(text = "Remove first")
            }
            Button(
                onClick = {
                    list.add(0, counter++)
                }
            ) {
                Text(text = "Add first")
            }
            LazyColumn {
                items(list, key = { it }) { item ->
                    listComposable(item)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    // Actually it is remembered in the composedItems
    @Suppress("RememberContentMissing")
    @Composable
    fun <T> List<T>.movable(
        transform: @Composable (item: T) -> Unit
    ): @Composable (item: T) -> Unit {
        val composedItems = remember {
            mutableStateMapOf<T, @Composable () -> Unit>()
        }
        DisposableEffect(key1 = this) {
            val movableContentScreens = composedItems.keys
            val actualScreens = this@movable.toSet()
            val removedScreens = movableContentScreens - actualScreens
            removedScreens.forEach {
                composedItems -= it
            }
            onDispose { }
        }
        return { item: T ->
            composedItems.getOrPut(item) {
                movableContentOf { transform(item) }
            }.invoke()
        }
    }
}