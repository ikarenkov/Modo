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
    private val mutableState: MutableState<NavigationState?> = mutableStateOf(null)
    val currentState get() = mutableState.value

    override fun render(state: NavigationState) {
        if (state is StackNavigation && state.stack.isEmpty()) {
            onExit()
        } else {
            mutableState.value = state
        }
    }

    @Composable
    fun Content() {
        currentState?.getScreen()?.Content()
    }

    private fun NavigationState.getScreen(): ComposeContent = when (this) {
        is StackNavigation -> stack.last() as ComposeContent
        is MultiNavigation -> containers[selected] as ComposeContent
        else -> throw IllegalStateException("Unknown navigation state: $this")
    }
}