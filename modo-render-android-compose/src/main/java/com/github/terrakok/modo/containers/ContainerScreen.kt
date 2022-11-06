package com.github.terrakok.modo.containers

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.github.terrakok.modo.ComposeRenderer
import com.github.terrakok.modo.NavigationAction
import com.github.terrakok.modo.NavigationDispatcher
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.NavigationRenderer
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.RendererContent
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.defaultRendererContent
import com.github.terrakok.modo.generateScreenKey

val LocalContainerScreen = staticCompositionLocalOf<ContainerScreen<*>> { error("no screen provided") }

abstract class ContainerScreen<State : NavigationState>(
    private val navigationModel: NavigationModel<State>
) : Screen, NavigationDispatcher by navigationModel {

    abstract val reducer: NavigationReducer<State>

    val navigationState: State
        get() = navigationModel.navigationState

    internal val renderer: NavigationRenderer?
        get() = navigationModel.renderer

    final override val screenKey: String = navigationModel.screenKey

    init {
        navigationModel.init(
            reducerProvider = { reducer },
            renderer = ComposeRenderer(this)
        )
    }

    @Composable
    protected fun InternalContent(
        screen: Screen,
        content: RendererContent = defaultRendererContent
    ) {
        val composeRenderer = renderer as ComposeRenderer
        composeRenderer.Content(screen as Screen, content)
    }

    override fun toString(): String = screenKey

}

typealias ReducerProvider<State> = () -> NavigationReducer<State>

/**
 * Container for simple using [ContainerScreen] with [Parcelize]
 */
class NavigationModel<State : NavigationState>(
    initialState: State,
    val screenKey: String = generateScreenKey()
) : NavigationDispatcher, Parcelable {

    var navigationState: State = initialState
        get() = ((renderer as ComposeRenderer).state ?: field) as State
        set(value) {
            field = value
            renderer?.render(value)
        }

    private var reducerProvider: ReducerProvider<State>? = null
    internal var renderer: ComposeRenderer? = null
        private set

    internal fun init(
        reducerProvider: ReducerProvider<State>,
        renderer: ComposeRenderer
    ) {
        assert(this.reducerProvider == null && this.renderer == null) {
            "Trying to initialize navigation model again"
        }
        this.reducerProvider = reducerProvider
        this.renderer = renderer
    }

    override fun dispatch(action: NavigationAction) {
        navigationState = reducerProvider!!().reduce(action, navigationState)
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(navigationState, flags)
        parcel.writeString(screenKey)
    }

    companion object CREATOR : Parcelable.Creator<NavigationModel<*>> {
        override fun createFromParcel(parcel: Parcel): NavigationModel<NavigationState> {
            val state = parcel.readParcelable<NavigationState>(NavigationModel::class.java.classLoader)!!
            val screenKey = parcel.readString()!!
            return NavigationModel(state, screenKey)
        }

        override fun newArray(size: Int): Array<NavigationModel<*>?> = arrayOfNulls(size)
    }
}