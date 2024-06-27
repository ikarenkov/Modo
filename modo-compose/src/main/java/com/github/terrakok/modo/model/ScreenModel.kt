package com.github.terrakok.modo.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.Screen
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.plus
import java.util.UUID

val ScreenModel.coroutineScope: CoroutineScope
    get() = ScreenModelStore.getOrPutDependency(
        screenModel = this,
        name = "ScreenModelCoroutineScope",
        factory = { key -> MainScope() + CoroutineName(key) },
        onDispose = { scope -> scope.cancel() }
    )

val Screen.coroutineScope: CoroutineScope
    get() = ScreenModelStore.getOrPutDependency(
        screen = this,
        name = "ScreenModelCoroutineScope",
        factory = { key -> MainScope() + CoroutineName(key) },
        onDispose = { scope -> scope.cancel() }
    )

@Composable
inline fun <reified T : ScreenModel> Screen.rememberScreenModel(
    tag: String? = null,
    crossinline factory: @DisallowComposableCalls () -> T
): T =
    remember(ScreenModelStore.getKey<T>(this, tag)) {
        ScreenModelStore.getOrPut(this, tag, factory)
    }

/**
 * Remembers the instance provided by [factory] and clears it whe screen leaves hierarchy.
 * You can clear resources in [onDispose], because it has the access to the instance.
 */
@ExperimentalModoApi
@Composable
inline fun <reified T : Any> Screen.rememberDependency(
    name: String,
    crossinline factory: @DisallowComposableCalls (DependencyKey) -> T,
    tag: String? = null,
    noinline onDispose: @DisallowComposableCalls (T) -> Unit = {},
): T {
    val key = remember { ScreenModelStore.getDependencyKey(this, name, tag) }
    return remember(key) {
        ScreenModelStore.getOrPutDependency(key = key, factory = factory, onDispose = onDispose)
    }
}

@PublishedApi
internal const val ON_SCREEN_REMOVED_CALLBACK_NAME = "OnScreenRemovedCallBack"

@Deprecated(
    "moved to the package com.github.terrakok.modo.lifecycle",
    ReplaceWith("OnScreenRemoved(tag, onScreenRemoved)", "com.github.terrakok.modo.lifecycle.OnScreenRemoved")
)
@Suppress("LambdaParameterInRestartableEffect")
@ExperimentalModoApi
@Composable
inline fun Screen.OnScreenRemoved(
    tag: String = rememberSaveable { UUID.randomUUID().toString() },
    crossinline onScreenRemoved: @DisallowComposableCalls () -> Unit
) {
    LaunchedEffect(tag) {
        ScreenModelStore.getOrPutDependency(
            screen = this@OnScreenRemoved,
            name = ON_SCREEN_REMOVED_CALLBACK_NAME,
            tag = tag,
            onDispose = { onScreenRemoved() },
            factory = { Any() }
        )
    }
}

interface ScreenModel {

    fun onDispose() {}
}

abstract class StateScreenModel<S>(initialState: S) : ScreenModel {

    protected val mutableState: MutableStateFlow<S> = MutableStateFlow(initialState)
    val state: StateFlow<S> = mutableState.asStateFlow()
}
