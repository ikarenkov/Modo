package com.github.terrakok.androidcomposeapp.screens.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.terrakok.androidcomposeapp.screens.SampleContent
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.lifecycle.LifecycleScreenEffect
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

    @OptIn(ExperimentalModoApi::class)
    @Composable
    override fun Content() {
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