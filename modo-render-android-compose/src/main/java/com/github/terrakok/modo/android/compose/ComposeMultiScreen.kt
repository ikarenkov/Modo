package com.github.terrakok.modo.android.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.terrakok.modo.MultiScreenState

abstract class ComposeMultiScreen(multiScreenState: MultiScreenState, id: String) : ComposeScreen(id) {

    /**
     * @param multiScreen - current multiscreen model, for access to state
     */
    @Composable
    abstract fun Content(innerContent: @Composable () -> Unit)

    var multiScreenState by mutableStateOf(multiScreenState)

    @Composable
    final override fun Content() {
        Content {
            val selectedScreen = multiScreenState.stacks[multiScreenState.selectedStack].chain.last()
            require(selectedScreen is ComposeScreen)
            selectedScreen.Content()
        }
    }

}