package com.github.terrakok.modo.android.compose

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
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
        state.chain.forEach { screen ->
                Box(Modifier.fillMaxSize().background(Color.White)) {
                    Box(Modifier.fillMaxSize().alpha(0f).clickable {})
                    when (screen) {
                        is ComposeScreen -> screen.Content()
                        else -> error("ComposeRender works with ComposeScreen only! Received $screen")
                    }
                }
        }
    }
}
