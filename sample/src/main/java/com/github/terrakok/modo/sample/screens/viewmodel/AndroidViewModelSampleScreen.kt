package com.github.terrakok.modo.sample.screens.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.sample.screens.MainScreenContent
import com.github.terrakok.modo.sample.screens.base.COUNTER_DELAY_MS
import com.github.terrakok.modo.stack.LocalStackNavigation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import logcat.logcat

@Parcelize
internal class AndroidViewModelSampleScreen(
    private val screenPos: Int,
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        val viewModel: SampleViewModel = viewModel {
            SampleViewModel(screenPos, createSavedStateHandle())
        }
        MainScreenContent(screenPos, viewModel.stateFlow.collectAsState().value, LocalStackNavigation.current, modifier)
    }

}

internal class SampleViewModel(
    private val screenPos: Int,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val stateFlow: StateFlow<Int> = savedStateHandle.getStateFlow(STATE_KEY, 0)
    private var state: Int
        get() = stateFlow.value
        set(value) {
            savedStateHandle[STATE_KEY] = value
        }

    init {
        logcat { "SampleViewModel init $screenPos" }
        viewModelScope.launch {
            while (isActive) {
                delay(COUNTER_DELAY_MS)
                state += 1
            }
        }
    }

    override fun onCleared() {
        logcat { "SampleViewModel onCleared $screenPos" }
        super.onCleared()
    }

    companion object {
        private const val STATE_KEY = "STATE_KEY"
    }

}