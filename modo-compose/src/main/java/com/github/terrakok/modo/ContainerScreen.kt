package com.github.terrakok.modo

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

val LocalContainerScreen = staticCompositionLocalOf<ContainerScreen<*, *>?> { null }

fun interface ReducerAction<State : NavigationState> : NavigationAction<State> {
    fun reduce(oldState: State): State
}

abstract class ContainerScreen<State : NavigationState, Action : NavigationAction<State>>(
    private val navModel: NavModel<State, Action>
) : Screen, NavigationContainer<State, Action> by navModel {

    /**
     * The reducer that can be used to control state updates.
     */
    open val reducer: NavigationReducer<State, Action>? = null

    internal val renderer: NavigationRenderer<State>?
        get() = navModel.renderer

    final override val screenKey: ScreenKey = navModel.screenKey

    init {
        navModel.init(
            reducerProvider = { reducer },
            renderer = ComposeRenderer(this)
        )
    }

    /**
     * This function can be used to provide composition locals for inner screens.
     * This is used in implementations of ContainerScreen to provide typed composition locals to container.
     * @see com.github.terrakok.modo.stack.LocalStackNavigation
     */
    open fun provideCompositionLocals(): Array<ProvidedValue<*>> =
        provideNavigationContainer()
            ?.let { arrayOf(it) }
            ?: emptyArray()

    /**
     * Provides composition local for the nested hierarchy to receive NavigationContainer.
     * @see com.github.terrakok.modo.stack.LocalStackNavigation
     */
    open fun provideNavigationContainer(): ProvidedValue<out NavigationContainer<*, *>>? = null

    @Composable
    protected fun InternalContent(
        screen: Screen,
        modifier: Modifier = Modifier,
        content: RendererContent<State> = defaultRendererContent
    ) {
        val composeRenderer = renderer as ComposeRenderer
        composeRenderer.Content(screen, modifier, provideCompositionLocals(), content)
    }

    override fun toString(): String = this::class.java.simpleName + "(navModel: $navModel)"

}

typealias ReducerProvider<State, Action> = () -> NavigationReducer<State, Action>?

/**
 * Container for simple using [ContainerScreen] with [Parcelize]
 */
class NavModel<State : NavigationState, Action : NavigationAction<State>>(
    initialState: State,
    val screenKey: ScreenKey = generateScreenKey()
) : NavigationContainer<State, Action>, Parcelable {

    override var navigationState: State = initialState
        get() = renderer?.state ?: field
        set(value) {
            field = value
            renderer?.render(value)
        }

    private var reducerProvider: ReducerProvider<State, Action>? = null
    internal var renderer: ComposeRenderer<State>? = null
        private set

    internal fun init(
        reducerProvider: ReducerProvider<State, Action>,
        renderer: ComposeRenderer<State>
    ) {
        assert(this.reducerProvider == null && this.renderer == null) {
            "Trying to initialize navigation model again"
        }
        this.reducerProvider = reducerProvider
        this.renderer = renderer.also { it.render(navigationState) }
    }

    override fun dispatch(action: Action, vararg actions: Action) {
        val reducer = reducerProvider!!()
        var state = reduce(reducer, navigationState, action)
        for (varargAction in actions) {
            state = reduce(reducer, state, varargAction)
        }
        navigationState = state
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(navigationState, flags)
        parcel.writeString(screenKey.value)
    }

    private fun reduce(reducer: NavigationReducer<State, Action>?, state: State, action: Action): State =
        reducer?.reduce(action, state)
            ?: when (action) {
                is ReducerAction<*> -> (action as? ReducerAction<State>)?.reduce(state)
                else -> null
            }
            // TODO: print logs when fallback to state
            ?: state

    override fun toString(): String = "NavModel(navigationState=$navigationState, screenKey=$screenKey)"

    companion object CREATOR : Parcelable.Creator<NavModel<*, *>> {
        override fun createFromParcel(parcel: Parcel): NavModel<NavigationState, *> {
            val state = parcel.readParcelable<NavigationState>(NavModel::class.java.classLoader)!!
            val screenKey = parcel.readString()!!
            return NavModel(state, ScreenKey(screenKey))
        }

        override fun newArray(size: Int): Array<NavModel<*, *>?> = arrayOfNulls(size)
    }
}