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

val LocalContainerScreen = staticCompositionLocalOf<ContainerScreen<*>?> { null }

@Deprecated(
    message = "Use NavigationReducer instead",
    replaceWith = ReplaceWith("NavigationReducer<State>")
)
typealias ReducerAction<State> = NavigationReducer<State>

@Stable
abstract class ContainerScreen<State : NavigationState>(
    private val navModel: NavModel<State>
) : Screen, NavigationContainer<State> by navModel {

    /**
     * The reducer that can be used to control state updates.
     */
    @Deprecated(
        message = "Custom navigation behavior should move from screen-level external reducers to dispatch-time reducers. " +
            "This property is no longer used by the navigation system.",
        level = DeprecationLevel.ERROR
    )
    open val reducer: NavigationReducer<State>? = null

    internal val renderer: ComposeRenderer<State> = ComposeRenderer(this, navModel.stateFlow)

    final override val screenKey: ScreenKey = navModel.screenKey

    /** Compose-observable view of the current navigation state. */
    val navigationState: State get() = renderer.state

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
    open fun provideNavigationContainer(): ProvidedValue<out NavigationContainer<*>>? = null

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

/**
 * Pure UDF implementation of [NavigationContainer]. Holds state in a [MutableStateFlow] and mutates it
 * exclusively through [dispatch]. Parcelable so it survives process death.
 * Intended to be owned by a [ContainerScreen], which delegates [NavigationContainer] to it.
 */
class NavModel<State : NavigationState>(
    initialState: State,
    val screenKey: ScreenKey = generateScreenKey()
) : NavigationContainer<State>, Parcelable {

    private val _navigationState = MutableStateFlow(initialState)
    override val stateFlow: StateFlow<State> = _navigationState.asStateFlow()

    override fun dispatch(reducer: NavigationReducer<State>) {
        _navigationState.value = reducer.reduce(_navigationState.value)
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(_navigationState.value, flags)
        parcel.writeString(screenKey.value)
    }

    override fun toString(): String = "NavModel(navigationState=${_navigationState.value}, screenKey=$screenKey)"

    companion object CREATOR : Parcelable.Creator<NavModel<*>> {
        override fun createFromParcel(parcel: Parcel): NavModel<NavigationState> {
            val state = parcel.readParcelable<NavigationState>(NavModel::class.java.classLoader)!!
            val screenKey = parcel.readString()!!
            return NavModel(state, ScreenKey(screenKey))
        }

        override fun newArray(size: Int): Array<NavModel<*>?> = arrayOfNulls(size)
    }
}