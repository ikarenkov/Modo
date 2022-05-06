package com.github.terrakok.modo.android.compose

import android.app.Activity
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import com.github.terrakok.modo.NavigationRender
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.Screen

interface ComposeRenderer : NavigationRender {

    val state: State<NavigationState>

    @Composable
    fun Content()

}

open class ComposeRenderImpl(
    private val exitAction: () -> Unit
) : ComposeRenderer {
    constructor(activity: Activity) : this({ activity.finish() })

    override val state: MutableState<NavigationState> = mutableStateOf(NavigationState())

    private val removedScreens = mutableSetOf<Screen>()

    override fun invoke(state: NavigationState) {
        if (state.chain.isEmpty()) {
            exitAction()
        }
        removedScreens.addAll(calculateRemovedScreens(this.state.value.chain, state.chain))
        this.state.value = state
    }

    @Composable
    override fun Content() {
        val stateHolder = rememberSaveableStateHolder()
        DisposableEffect(key1 = state) {
            onDispose {
                clearStateHolder(stateHolder)
            }
        }
        state.value.chain.lastOrNull()?.let { screen ->
            require(screen is ComposeScreen) {
                "ComposeRender works with ComposeScreen only! Received $screen"
            }
            stateHolder.SaveableStateProvider(key = screen.screenKey) {
                screen.Content()
            }
        }
    }

    private fun clearStateHolder(stateHolder: SaveableStateHolder) {
        removedScreens.forEach {
            stateHolder.removeState(it)
        }
        if (removedScreens.isNotEmpty()) {
            removedScreens.clear()
        }
    }

    private fun calculateRemovedScreens(currentChain: List<Screen>, newChain: List<Screen>): List<Screen> {
        val newChainSet = newChain.toSet()
        return currentChain.filter { it !in newChainSet }
    }

}
