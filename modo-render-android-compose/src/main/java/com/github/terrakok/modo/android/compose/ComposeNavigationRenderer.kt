package com.github.terrakok.modo.android.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.github.terrakok.modo.MultiNavigation
import com.github.terrakok.modo.NavigationRenderer
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.StackNavigation

class ComposeNavigationRenderer(
    private val onExit: () -> Unit
) : NavigationRenderer {
    internal val mutableState: MutableState<NavigationState?> = mutableStateOf(null)

    override fun render(state: NavigationState) {
        mutableState.value = state
    }

    @Composable
    fun Content() {
        val state = mutableState.value
        if (state != null) {
            val screen = getComposeContent(state)
            if (screen == null) onExit()
            else screen.Content()
        }
    }

    private fun getComposeContent(state: NavigationState): ComposeContent? = when (state) {
        is StackNavigation -> state.stack.lastOrNull()?.let { it as ComposeContent }
        is MultiNavigation -> state.containers[state.activeContainerIndex] as ComposeContent
        else -> throw IllegalStateException("Unknown navigation state: $state")
    }

}