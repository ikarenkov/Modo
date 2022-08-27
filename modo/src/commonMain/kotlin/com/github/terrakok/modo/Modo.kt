package com.github.terrakok.modo

interface NavigationState {
    fun getAllScreens(): List<Screen>
    fun getActiveScreen(): Screen?
}

interface NavigationAction

interface NavigationReducer<State: NavigationState> {
    fun reduce(action: NavigationAction, state: State): State?
}

interface NavigationDispatcher {
    fun dispatch(action: NavigationAction)
}

interface NavigationRenderer {
    fun render(state: NavigationState)
}

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
}

fun NavigationState?.print(): String =
    if (this == null) {
        "null"
    } else {
        getNavigationStateString("", this).trimEnd() + "  <- current screen"
    }

private fun getNavigationStateString(prefix: String, navigationState: NavigationState): String =
    when (navigationState) {
        is StackNavigation -> {
            navigationState.stack.map { screen ->
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
            }.joinToString(separator = "")
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

internal expect fun logd(tag: String, msg: String)
