package com.github.terrakok.modo.sample.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.multiscreen.MultiScreenState
import com.github.terrakok.modo.stack.StackState
import com.github.terrakok.modo.subtreeStateFlow

/**
 * Demo: observes the navigation tree via [NavigationContainer.subtreeStateFlow] and renders a
 * compact textual snapshot. Re-walks the tree on every emission — emissions are notifications,
 * not values, since the deep flow re-emits the root state on any descendant change.
 */
@Composable
fun NavigationTreeStrip(
    container: NavigationContainer<*>,
    modifier: Modifier = Modifier,
) {
    val state by produceState<NavigationState?>(initialValue = null, container) {
        container.subtreeStateFlow().collect { value = it }
    }
    Text(
        text = state?.compactRender().orEmpty(),
        color = Color.White,
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

private const val STACK_VISIBLE = 2

private fun NavigationState.compactRender(): String =
    buildString { appendNode(prefix = "", state = this@compactRender) }.trimEnd()

private fun StringBuilder.appendNode(prefix: String, state: NavigationState) {
    when (state) {
        is StackState -> {
            val stack = state.stack
            val hidden = (stack.size - STACK_VISIBLE).coerceAtLeast(0)
            if (hidden > 0) append(prefix).append("…").append(hidden).append(" more\n")
            val visible = stack.takeLast(STACK_VISIBLE)
            visible.forEachIndexed { idx, screen ->
                val isTop = idx == visible.lastIndex
                appendScreen(prefix, screen, isTop)
            }
        }
        is MultiScreenState -> {
            val selected = state.screens.getOrNull(state.selected) ?: return
            append(prefix).append("multi #").append(state.selected).append('\n')
            appendScreen("$prefix  ", selected, isTop = true)
        }
        else -> state.getChildScreens().forEach { appendScreen(prefix, it, isTop = false) }
    }
}

private fun StringBuilder.appendScreen(prefix: String, screen: Screen, isTop: Boolean) {
    append(prefix)
    append(screen.label())
    if (isTop && screen !is ContainerScreen<*>) append(" ◀")
    append('\n')
    if (screen is ContainerScreen<*>) {
        appendNode("$prefix  ", screen.navigationState)
    }
}

private fun Screen.label(): String = this::class.simpleName ?: "Screen"
