package com.github.terrakok.androidcomposeapp.screens.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.terrakok.androidcomposeapp.screens.SampleScreenContent
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.lifecycle.LifecycleScreenEffect
import com.github.terrakok.modo.stack.StackScreen
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

    @OptIn(ExperimentalModoApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
//        val lifecycleOwner = LocalLifecycleOwner.current

        // You will lose onResume, onStop, if you use regular DisposableEffect or LaunchedEffect, because it finishes as far as composition ends.
//        DisposableEffect(lifecycleOwner) {
//            val observer = LifecycleEventObserver { _, event ->
//                logcat { "AndroidViewModelSampleScreen DisposableEffect $screenKey: event $event" }
//            }
//            lifecycleOwner.lifecycle.addObserver(observer)
//            onDispose {
//                lifecycleOwner.lifecycle.removeObserver(observer)
//            }
//        }

        // Coroutines way
//        LaunchedScreenEffect {
//            lifecycleOwner.lifecycle.eventFlow.collect { lifecycleState ->
//                logcat { "AndroidViewModelSampleScreen $screenKey: LifecycleState $lifecycleState" }
//            }
//        }

        // Disposable observer way
//        DisposableScreenEffect {
//            logcat { "AndroidViewModelSampleScreen $screenPos DisposableScreenEffect created" }
//            val observer = object : LifecycleEventObserver {
//                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
//                    logcat { "AndroidViewModelSampleScreen $screenKey observer: Lifecycle.Event $event" }
//                }
//            }
//            lifecycleOwner.lifecycle.addObserver(observer)
//            onDispose {
//                logcat { "AndroidViewModelSampleScreen $screenKey DisposableScreenEffect disposed" }
//                lifecycleOwner.lifecycle.removeObserver(observer)
//            }
//        }

        LifecycleScreenEffect {
            object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    logcat { "AndroidViewModelSampleScreen $screenKey: Lifecycle.Event $event" }
                }
            }
        }

        val viewModel: SampleViewModel = viewModel {
            SampleViewModel(screenPos, createSavedStateHandle())
        }
        val parent = LocalContainerScreen.current
        SampleScreenContent(screenPos, viewModel.stateFlow.collectAsState().value, parent as StackScreen)
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
                delay(10)
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