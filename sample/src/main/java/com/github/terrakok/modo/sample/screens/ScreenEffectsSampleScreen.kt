package com.github.terrakok.modo.sample.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.lifecycle.DisposableScreenEffect
import com.github.terrakok.modo.lifecycle.LaunchedScreenEffect
import com.github.terrakok.modo.lifecycle.LifecycleScreenEffect
import com.github.terrakok.modo.sample.screens.base.SampleScreenContent
import com.github.terrakok.modo.sample.screens.base.rememberCounterState
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.back
import com.github.terrakok.modo.stack.forward
import kotlinx.parcelize.Parcelize
import logcat.logcat

@Parcelize
class ScreenEffectsSampleScreen(
    private val screenIndex: Int,
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @OptIn(ExperimentalModoApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        var lifecycleEventsHistory by rememberSaveable {
            mutableStateOf(listOf<Lifecycle.Event>())
        }
        val scaffoldState = rememberScaffoldState()
        val counter by rememberCounterState()
        // This effect is going to be launched once despite on activity recreation.
        LaunchedScreenEffect {
            scaffoldState.snackbarHostState.showSnackbar("LaunchedScreenEffect! Counter: $counter.")
        }
        LifecycleScreenEffect {
            LifecycleEventObserver { _, event ->
                lifecycleEventsHistory += event
                logcat { "Lifecycle event $event. Counter: $counter." }
            }
        }
        DisposableScreenEffect {
            logcat { "Analytics: screen created. Counter: $counter." }
            onDispose {
                logcat { "Analytics: screen destroyed.  Counter: $counter." }
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