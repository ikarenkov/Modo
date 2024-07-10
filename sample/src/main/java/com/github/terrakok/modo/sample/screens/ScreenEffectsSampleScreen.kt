package com.github.terrakok.modo.sample.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.eventFlow
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.lifecycle.LaunchedScreenEffect
import com.github.terrakok.modo.lifecycle.LifecycleScreenEffect
import com.github.terrakok.modo.sample.screens.base.SampleScreenContent
import com.github.terrakok.modo.sample.screens.base.rememberCounterState
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.back
import com.github.terrakok.modo.stack.forward
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import logcat.logcat

@Parcelize
class ScreenEffectsSampleScreen(
    private val screenIndex: Int,
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    companion object {
        private const val TAG = "ScreenEffectsSampleScreen"
    }

    @OptIn(ExperimentalModoApi::class, ExperimentalStdlibApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        var lifecycleEventsHistory by rememberSaveable {
            mutableStateOf(listOf<Lifecycle.Event>())
        }
        val scaffoldState = rememberScaffoldState()
        val counter by rememberCounterState()

        // This effect is going to be launched once per screen an triggered even if screen left composition!
        // So be careful with passing any data to lambda which lifecycle is shorter than screen lifecycle.
        // F.e. capturing context cause leak of the context.
        LaunchedScreenEffect {
            // Doing so will cause a leak of the scaffoldState.
            scaffoldState.snackbarHostState.showSnackbar("LaunchedScreenEffect! Counter: $counter.")
        }
        val lifecycleOwner = LocalLifecycleOwner.current
        LaunchedEffect(Unit) {
            // Use Dispatchers.Main.immediate, otherwise you will lose ON_PAUSE, ON_STOP, ON_DESTROY events,
            // because of peculiarities of coroutines - it removes lifecycle observer before handling effects
            // You can try out and remove Dispatchers.Main.immediate to see the difference.
            withContext(Dispatchers.Main.immediate) {
                val dispatcher = coroutineContext[CoroutineDispatcher.Key]
                logcat(TAG) { "LaunchedEffect $dispatcher" }
                lifecycleOwner.lifecycle.eventFlow.collect { event ->
                    logcat(TAG) { "LaunchedEffect: event $event. Counter: $counter." }
                }
            }
        }
        DisposableEffect(this) {
            val observer = LifecycleEventObserver { _, event ->
                logcat(TAG) { "DisposableEffect: event $event. Counter: $counter." }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                logcat(TAG) { "DisposableEffect: on dispose" }
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
        LifecycleScreenEffect {
            LifecycleEventObserver { _, event ->
                lifecycleEventsHistory += event
                logcat(TAG) { "LifecycleScreenEffect: event $event. Counter: $counter." }
            }
        }
        val navigation = LocalStackNavigation.current
        SampleScreenContent(
            screenIndex = screenIndex,
            screenName = "ScreenEffectsSampleScreen",
            screenKey = screenKey,
            counter = counter,
            modifier = modifier,
        ) {
            GroupedButtonsList(
                state = remember {
                    listOf(
                        ModoButtonSpec("Forward") { navigation.forward(MainScreen(screenIndex + 1)) },
                        ModoButtonSpec("Back") { navigation.back() }
                    ).let {
                        ButtonsState(it)
                    }
                }
            )
            LazyColumn {
                items(lifecycleEventsHistory) {
                    Text(text = it.name)
                }
            }
        }
    }
}