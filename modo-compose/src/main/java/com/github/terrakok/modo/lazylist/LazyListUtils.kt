package com.github.terrakok.modo.lazylist

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.Screen

/**
 * A shortcut for [LazyListScope.items] with defined [key] as [Screen.screenKey].
 * Use [ContainerScreen.InternalContent] inside [itemContent] to render [Screen] as item.
 *
 * @see items
 */
inline fun <T : Screen> LazyListScope.screenItems(
    screens: List<T>,
    noinline contentType: (item: T) -> Any? = { null },
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit
): Unit = items(
    items = screens,
    contentType = contentType,
    key = { it.screenKey },
    itemContent = itemContent
)

/**
 * A shortcut for [LazyListScope.item] with defined [key] as [Screen.screenKey].
 * Use [ContainerScreen.InternalContent] inside [itemContent] to render [Screen] as item.
 *
 * @see item
 */
fun LazyListScope.screenItem(
    screen: Screen,
    contentType: Any? = null,
    content: @Composable LazyItemScope.() -> Unit
) {
    item(
        key = screen.screenKey,
        contentType = contentType,
        content = content
    )
}