package com.github.terrakok.modo.android.compose

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.terrakok.modo.NavigationRender
import com.github.terrakok.modo.NavigationState

class ComposeRender(
    private val exitAction: () -> Unit
) : NavigationRender {
    constructor(activity: Activity) : this({ activity.finish() })

    private var state: NavigationState by mutableStateOf(NavigationState())

    override fun invoke(state: NavigationState) {
        this.state = state
        if (state.chain.isEmpty()) {
            exitAction()
        }
    }

    @Composable
    fun Content() {
        state.chain.lastOrNull()?.let { screen ->
            when (screen) {
                is ComposeScreen -> screen.Content()
                else -> error("ComposeRender works with ComposeScreen only! Received $screen")
            }
        }
    }
}
