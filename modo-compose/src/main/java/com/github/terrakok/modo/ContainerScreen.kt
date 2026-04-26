package com.github.terrakok.modo

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

val LocalContainerScreen = staticCompositionLocalOf<ContainerScreen<*, *>?> { null }

fun interface ReducerAction<State : NavigationState> : NavigationAction<State> {
    fun reduce(oldState: State): State
}

@Stable
abstract class ContainerScreen<State : NavigationState, Action : NavigationAction<State>>(
    private val navModel: NavModel<State, Action>
) : Screen, NavigationContainer<State, Action> by navModel {

    /**
     * The reducer that can be used to control state updates.
     */
    open val reducer: NavigationReducer<State, Action>? = null

    internal val renderer: ComposeRenderer<State> = ComposeRenderer(this, navModel.navigationStateFlow)

    final override val screenKey: ScreenKey = navModel.screenKey

    /** Compose-observable view of the current navigation state. */
    val navigationState: State get() = renderer.state

    init {
        navModel.init(reducerProvider = { reducer })
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

    /**
     * Use this function to render the content of nested screens. It provides correct work of [rememberSaveable] by using [SaveableStateHolder].
     * It also provides other integrations like correct lifecycle, screen model, android integration and so on.
     * @param screen - the screen to render
     */
    @Composable
    protected fun InternalContent(
        screen: Screen,
        modifier: Modifier = Modifier,
        content: RendererContent<State> = defaultRendererContent
    ) {
        renderer.Content(screen, modifier, provideCompositionLocals(), content)
    }

    override fun toString(): String = this::class.java.simpleName + "(navModel: $navModel)"

}

typealias ReducerProvider<State, Action> = () -> NavigationReducer<State, Action>?

/**
 * Pure UDF implementation of [NavigationContainer]. Holds state in a [MutableStateFlow] and mutates it
 * exclusively through [dispatch]. Parcelable so it survives process death.
 * Intended to be owned by a [ContainerScreen], which delegates [NavigationContainer] to it.
 */
@Stable
class NavModel<State : NavigationState, Action : NavigationAction<State>>(
    initialState: State,
    val screenKey: ScreenKey = generateScreenKey()
) : NavigationContainer<State, Action>, Parcelable {

    private val _navigationState = MutableStateFlow(initialState)
    override val navigationStateFlow: StateFlow<State> = _navigationState.asStateFlow()

    private var reducerProvider: ReducerProvider<State, Action>? = null

    internal fun init(reducerProvider: ReducerProvider<State, Action>) {
        assert(this.reducerProvider == null) {
            "Trying to initialize navigation model again"
        }
        this.reducerProvider = reducerProvider
    }

    override fun dispatch(action: Action, vararg actions: Action) {
        val reducer = reducerProvider!!()
        var state = reduce(reducer, _navigationState.value, action)
        for (varargAction in actions) {
            state = reduce(reducer, state, varargAction)
        }
        _navigationState.value = state
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(_navigationState.value, flags)
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

    override fun toString(): String = "NavModel(navigationState=${_navigationState.value}, screenKey=$screenKey)"

    companion object CREATOR : Parcelable.Creator<NavModel<*, *>> {
        override fun createFromParcel(parcel: Parcel): NavModel<NavigationState, *> {
            val state = parcel.readParcelable<NavigationState>(NavModel::class.java.classLoader)!!
            val screenKey = parcel.readString()!!
            return NavModel(state, ScreenKey(screenKey))
        }

        override fun newArray(size: Int): Array<NavModel<*, *>?> = arrayOfNulls(size)
    }
}