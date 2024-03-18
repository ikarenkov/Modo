package com.github.terrakok.modo.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.saveable.rememberSaveable
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.model.DependencyKey
import com.github.terrakok.modo.model.ScreenModelStore
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Function that invokes block on the screen creation, and disposed when screen is gone.
 * @param block - suspend lambda that will be invoked once at the firs composition of screen.
 * This will be canceled when screen is removed from hierarchy.
 */
@Composable
@NonRestartableComposable
fun Screen.OnScreenCreated(
    tag: String = rememberSaveable { UUID.randomUUID().toString() },
    block: suspend CoroutineScope.() -> Unit
): Unit {
    LaunchedEffect(this) {
        ScreenModelStore.getOrPutDependency<CoroutineScope>(
            screen = this@OnScreenCreated,
            name = "OnScreenCreated",
            tag = tag,
            onDispose = { scope -> scope.cancel() },
            factory = { key ->
                screenCoroutineMainScope(key).also { scope ->
                    scope.launch {
                        block()
                    }
                }
            }
        )
    }
}

private fun screenCoroutineMainScope(key: DependencyKey) =
    CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate + CoroutineName(key))