package com.github.terrakok.modo.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffectResult
import androidx.compose.runtime.DisposableEffectScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import com.github.terrakok.modo.ExperimentalModoApi
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
 * A side effect of screen creation that must be reversed or cleaned up if the Screen leaves the hierarchy.
 * You can make any suspend invocation inside [block],
 * but the [CoroutineScope] in witch this block is running will be canceled as soon as screen leaves hierarchy.
 */
@ExperimentalModoApi
@Composable
fun Screen.LaunchedScreenEffect(
    tag: String = rememberSaveable { UUID.randomUUID().toString() },
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedEffect(this) {
        ScreenModelStore.getOrPutDependency<CoroutineScope>(
            screen = this@LaunchedScreenEffect,
            name = "OnScreenCreated",
            tag = tag,
            onDispose = { scope ->
                scope.cancel()
            },
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

/**
 * A side effect of screen creation that must be reversed or cleaned up if the Screen leaves the hierarchy.
 * Similar to the [DisposableEffect], but
 * 1. [effect] lambda called once per screen
 * 2. `onDispose` is called when the screen lives hierarchy
 */
@ExperimentalModoApi
@Composable
fun Screen.DisposableScreenEffect(
    tag: String = rememberSaveable { UUID.randomUUID().toString() },
    effect: DisposableEffectScope.() -> DisposableEffectResult
) {
    LaunchedEffect(this) {
        ScreenModelStore.getOrPutDependency<DisposableScreenEffectImpl>(
            screen = this@DisposableScreenEffect,
            name = "OnScreenCreated",
            tag = tag,
            onDispose = { disposableScreenEffect -> disposableScreenEffect.onDisposed() },
            factory = { _ ->
                DisposableScreenEffectImpl(effect)
            }
        )
    }
}

private val InternalDisposableScreenEffectScope = DisposableEffectScope()

private class DisposableScreenEffectImpl(
    effect: DisposableEffectScope.() -> DisposableEffectResult
) {
    private var onDispose: DisposableEffectResult? = null

    init {
        onDispose = InternalDisposableScreenEffectScope.effect()
    }

    fun onDisposed() {
        onDispose?.dispose()
        onDispose = null
    }
}

private fun screenCoroutineMainScope(key: DependencyKey) =
    CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate + CoroutineName(key))