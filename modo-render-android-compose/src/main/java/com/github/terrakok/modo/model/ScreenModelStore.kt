package com.github.terrakok.modo.model

import androidx.compose.runtime.DisallowComposableCalls
import com.github.terrakok.modo.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.ConcurrentHashMap

private typealias ScreenModelKey = String

private typealias DependencyKey = String
private typealias DependencyInstance = Any
private typealias DependencyOnDispose = (Any) -> Unit
private typealias Dependency = Pair<DependencyInstance, DependencyOnDispose>

public object ScreenModelStore {

    @PublishedApi
    internal val screenModels: MutableMap<ScreenModelKey, ScreenModel> = ConcurrentHashMap()

    @PublishedApi
    internal val dependencies: MutableMap<DependencyKey, Dependency> = ConcurrentHashMap()

    @PublishedApi
    internal val lastScreenModelKey: MutableStateFlow<ScreenModelKey?> = MutableStateFlow(null)

    @PublishedApi
    internal inline fun <reified T : ScreenModel> getKey(screen: Screen, tag: String?): ScreenModelKey =
        "${screen.screenKey}:${T::class.qualifiedName}:${tag ?: "default"}"

    @PublishedApi
    internal fun getDependencyKey(screenModel: ScreenModel, name: String): DependencyKey =
        screenModels
            .firstNotNullOfOrNull {
                if (it.value == screenModel) it.key
                else null
            }
            ?: lastScreenModelKey.value
                ?.let { "$it:$name" }
            ?: "standalone:$name"

    @PublishedApi
    internal inline fun <reified T : ScreenModel> getOrPut(
        screen: Screen,
        tag: String?,
        factory: @DisallowComposableCalls () -> T
    ): T {
        val key = getKey<T>(screen, tag)
        lastScreenModelKey.value = key
        return screenModels.getOrPut(key, factory) as T
    }

    public inline fun <reified T : Any> getOrPutDependency(
        screenModel: ScreenModel,
        name: String,
        noinline onDispose: @DisallowComposableCalls (T) -> Unit = {},
        noinline factory: @DisallowComposableCalls (DependencyKey) -> T
    ): T {
        val key = getDependencyKey(screenModel, name)

        return dependencies
            .getOrPut(key) { (factory(key) to onDispose) as Dependency }
            .first as T
    }

    public fun remove(screen: Screen) {
        screenModels.onEach(screen) { key ->
            screenModels[key]?.onDispose()
            screenModels -= key
        }

        dependencies.onEach(screen) { key ->
            dependencies[key]?.let { (instance, onDispose) -> onDispose(instance) }
            dependencies -= key
        }
    }

    private fun Map<String, *>.onEach(screen: Screen, block: (String) -> Unit) =
        asSequence()
            .filter { it.key.startsWith(screen.screenKey) }
            .map { it.key }
            .forEach(block)
}
