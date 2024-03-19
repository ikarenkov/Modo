package com.github.terrakok.androidcomposeapp.screens.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.eventFlow
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.terrakok.androidcomposeapp.screens.SampleContent
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.lifecycle.DisposableScreenEffect
import com.github.terrakok.modo.lifecycle.OnScreenCreated
import com.github.terrakok.modo.stack.StackScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import logcat.logcat

@Parcelize
class AndroidViewModelSampleScreen(
    private val screenPos: Int,
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content() {
        val lifecycleOwner = LocalLifecycleOwner.current
        // You will lose onResume, onStop, if you use regular DisposableEffect or LaunchedEffect, because it finishes as far as composition ends.
//        DisposableEffect(lifecycleOwner) {
//            val observer = LifecycleEventObserver { _, event ->
//                logcat { "AndroidViewModelSampleScreen$screenKey: event $event" }
//            }
//            lifecycleOwner.lifecycle.addObserver(observer)
//            onDispose {
//                lifecycleOwner.lifecycle.removeObserver(observer)
//            }
//        }

        // Coroutines way
        OnScreenCreated {
            lifecycleOwner.lifecycle.eventFlow.collect { lifecycleState ->
                logcat { "AndroidViewModelSampleScreen $screenPos: LifecycleState $lifecycleState" }
            }
        }

        // Observer way
        DisposableScreenEffect {
            val observer = object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    logcat { "AndroidViewModelSampleScreen observer $screenPos: Lifecycle.Event $event" }
                }

            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        val viewModel: SampleViewModel = viewModel {
            SampleViewModel(screenPos)
        }
        val parent = LocalContainerScreen.current
        SampleContent(screenPos, viewModel.state.collectAsState().value, parent as StackScreen)
    }

}

class SampleViewModel(private val screenPos: Int) : ViewModel() {

    val state: MutableStateFlow<Int> = MutableStateFlow(0)

    init {
        logcat { "SampleViewModel init $screenPos" }
        viewModelScope.launch {
            while (isActive) {
                delay(10)
                state.value += 1
            }
        }
    }

    override fun onCleared() {
        logcat { "SampleViewModel onCleared $screenPos" }
        super.onCleared()
    }

}