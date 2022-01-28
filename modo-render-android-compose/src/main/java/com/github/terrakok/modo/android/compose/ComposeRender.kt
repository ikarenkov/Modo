package com.github.terrakok.modo.android.compose

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import com.github.terrakok.modo.NavigationRender
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.Screen

class ComposeRender(
    private val exitAction: () -> Unit
) : NavigationRender {
    constructor(activity: Activity) : this({ activity.finish() })

    private var state: NavigationState by mutableStateOf(NavigationState())

    private val removedScreens = mutableSetOf<Screen>()

    override fun invoke(state: NavigationState) {
        if (state.chain.isEmpty()) {
            exitAction()
        }
        removedScreens.addAll(calculateRemovedScreens(this.state.chain, state.chain))
        this.state = state
    }

    @Composable
    fun Content() {
        val stateHolder = rememberSaveableStateHolder()
        SideEffect {
            // Have to try to clear state holder, because changes inside chain might happens.
            clearStateHolder(stateHolder)
        }
        state.chain.lastOrNull()?.let { screen ->
            require(screen is ComposeScreen) {
                "ComposeRender works with ComposeScreen only! Received $screen"
            }
            // It works, until our navigation is stack, but it stop works, when we remove screens inside.
            DisposableEffect(key1 = screen) {
                onDispose {
                    clearStateHolder(stateHolder)
                }
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
