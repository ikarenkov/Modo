package com.github.terrakok.modo

import androidx.compose.runtime.Composable

object Modo : NavigationDispatcher {
    @Suppress("VARIABLE_IN_SINGLETON_WITHOUT_THREAD_LOCAL")
    private lateinit var root: ContainerScreen<*>

    val navigationState: NavigationState get() = root.navigationState
    val navigator = Navigator(::dispatch)

    fun init(startScreen: Screen, reducer: NavigationReducer<StackNavigation> = StackReducer()) {
        root = ContainerScreen("root", StackNavigation(listOf(startScreen)), reducer)
    }

    fun setRenderer(renderer: NavigationRenderer) {
        root.renderer = renderer
    }

    override fun dispatch(action: NavigationAction) {
        root.getActiveScreen().container?.dispatch(action)
    }

    fun dispatchToRoot(action: NavigationAction) {
        root.dispatch(action)
    }

    @Composable
    fun Content() = root.Content()

    internal fun findScreenContainer(screen: Screen) = root.findScreenContainer(screen)

    private fun ContainerScreen<*>.findScreenContainer(screen: Screen): ContainerScreen<*>? {
        when (val state = navigationState) {
            is StackNavigation -> {
                state.stack.forEach { item ->
                    if (item === screen) return this
                    if (item is ContainerScreen<*>) {
                        val inner = item.findScreenContainer(screen)
                        if (inner != null) return inner
                    }
                }
            }
            is MultiNavigation -> {
                state.containers.forEach { container ->
                    if (container === screen) return this
                    val inner = container.findScreenContainer(screen)
                    if (inner != null) return inner
                }
            }
        }
        return null
    }

    private fun ContainerScreen<*>.getActiveScreen(): Screen = when (val state = navigationState) {
        is StackNavigation -> {
            when (val last = state.stack.lastOrNull()) {
                is ContainerScreen<*> -> last.getActiveScreen()
                is Screen -> last
                else -> this
            }
        }
        is MultiNavigation -> {
            state.containers[state.selected].getActiveScreen()
        }
        else -> throw IllegalStateException("Unknown navigation state $state!")
    }

    override fun toString() = root.navigationState.print()
}

fun NavigationState.print(): String =
    getNavigationStateString("", this).trimEnd() + "  <- current screen"

private fun getNavigationStateString(prefix: String, navigationState: NavigationState): String =
    when (navigationState) {
        is StackNavigation -> navigationState.stack.joinToString(separator = "") { screen ->
            when (screen) {
                is ContainerScreen<*> -> buildString {
                    append(prefix)
                    append(screen.id)
                    appendLine()
                    append(getNavigationStateString("$prefix|  ", screen.navigationState))
                }
                else -> {
                    "$prefix${screen.id}\n"
                }
            }
        }
        is MultiNavigation -> buildString {
            val screen = navigationState.containers[navigationState.selected]
            append(prefix)
            append(screen.id)
            appendLine()
            append(getNavigationStateString("$prefix|  ", screen.navigationState))
        }
        else -> "unknown state type: ${navigationState::class.simpleName}"
    }