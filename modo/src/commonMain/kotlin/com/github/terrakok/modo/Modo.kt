package com.github.terrakok.modo

interface ScreenId {
    val id: String
}

interface NavigationAction
interface NavigationState

interface NavigationReducer {
    fun reduce(action: NavigationAction, state: NavigationState): NavigationState?
}

interface NavigationDispatcher {
    fun dispatch(action: NavigationAction)
}

interface NavigationRenderer {
    fun render(state: NavigationState)
}

class Navigator(val dispatch: (action: NavigationAction) -> Unit)

open class Screen(
    override val id: String
) : ScreenId, NavigationDispatcher {
    val container: NavigationDispatcher? get() = getContainer()
    val navigator = Navigator(::dispatch)

    override fun dispatch(action: NavigationAction) {
        container?.dispatch(action)
    }
}

open class ContainerScreen(
    id: String,
    initialState: NavigationState,
    private val reducer: NavigationReducer
) : Screen(id) {

    var navigationState: NavigationState = initialState
        private set(value) {
            field = value
            renderer?.render(value)
        }

    var renderer: NavigationRenderer? = null
        set(value) {
            field = value
            value?.render(navigationState)
        }

    override fun dispatch(action: NavigationAction) {
        val newState = reducer.reduce(action, navigationState)
        if (newState != null) {
            navigationState = newState
        } else {
            container?.dispatch(action)
        }
    }
}

object Modo : NavigationDispatcher {
    private lateinit var root: ContainerScreen

    val navigationState: NavigationState get() = root.navigationState
    val navigator = Navigator(::dispatch)

    fun init(startScreen: Screen, reducer: NavigationReducer = StackReducer()) {
        root = ContainerScreen("root", StackNavigation(listOf(startScreen)), reducer)
    }

    fun setRenderer(renderer: NavigationRenderer) {
        root.renderer = renderer
    }

    override fun dispatch(action: NavigationAction) {
        root.getLeafScreen().dispatch(action)
    }

    fun dispatchToRoot(action: NavigationAction) {
        root.dispatch(action)
    }

    internal fun findScreenContainer(screen: Screen) = root.findScreenContainer(screen)
}

private fun Screen.getContainer() = Modo.findScreenContainer(this)
private fun ContainerScreen.findScreenContainer(screen: Screen): ContainerScreen? {
    when (val state = navigationState) {
        is StackNavigation -> {
            state.stack.forEach { screenId ->
                if (screenId === screen) return this
                if (screenId is ContainerScreen) {
                    val inner = screenId.findScreenContainer(screen)
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

private fun ContainerScreen.getLeafScreen(): Screen = when (val state = navigationState) {
    is StackNavigation -> {
        when (val last = state.stack.lastOrNull()) {
            is ContainerScreen -> last.getLeafScreen()
            is Screen -> last
            else -> this
        }
    }
    is MultiNavigation -> {
        state.containers[state.activeContainerIndex].getLeafScreen()
    }
    else -> throw IllegalStateException("Unknown navigation state $state!")
}
