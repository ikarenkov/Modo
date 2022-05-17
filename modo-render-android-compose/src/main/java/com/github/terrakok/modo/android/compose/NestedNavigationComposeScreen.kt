package com.github.terrakok.modo.android.compose

import android.os.Parcel
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.SaveableStateHolder
import com.github.terrakok.modo.*
import kotlinx.parcelize.Parceler
import java.util.*

val LocalModoDispatcher = staticCompositionLocalOf<ModoDispatcher> { error("Local ModoDispatcher wasn't provided!") }

abstract class NestedNavigationComposeScreen(
    id: String,
    initWreppeeState: NavigationState,
) : ComposeScreen(id), NestedNavigationScreen {

    abstract var saveableWreppeeState: NavigationState

    @Composable
    abstract fun Content(innerContent: @Composable () -> Unit)

    protected open val renderer = createRenderer().apply { invoke(initWreppeeState) }

    override var navigationState: NavigationState = initWreppeeState
        set(value) {
            field = value
            saveableWreppeeState = value
            renderer.invoke(value)
        }
        get() = renderer.state.value

    @OptIn(ExperimentalAnimationApi::class)
    protected open fun createRenderer() = ComposeRenderImpl(
        exitAction = {},
    ) {
        ScreenTransition(
            transitionSpec = {
                if (transitionType == ScreenTransitionType.Replace) {
                    scaleIn(initialScale = 2f) + fadeIn() with fadeOut()
                } else {
                    val (initialOffset, targetOffset) = when (transitionType) {
                        ScreenTransitionType.Pop -> ({ size: Int -> -size }) to ({ size: Int -> size })
                        else -> ({ size: Int -> size }) to ({ size: Int -> -size })
                    }
                    slideInHorizontally(initialOffsetX = initialOffset) with
                            slideOutHorizontally(targetOffsetX = targetOffset)
                }
            }
        )
    }

    fun onRemoveScreen(stateHolder: SaveableStateHolder) {
        renderer.clearStateHolder(stateHolder, true)
    }

    @Composable
    final override fun Content() {
        val outerModoDispatcher = LocalModoDispatcher.current
        val localModoDispatcher = remember {
            ModoDispatcher { outerModoDispatcher.dispatch(NestedAction(it)) }
        }
        // TODO: disable back handler during transition
        BackHandler {
            (if (navigationState.chain.isEmpty()) outerModoDispatcher else localModoDispatcher).back()
        }
        CompositionLocalProvider(
            LocalModoDispatcher provides localModoDispatcher
        ) {
            Content {
                renderer.Content()
            }
        }
    }

}

object NavigationStateParceler : Parceler<NavigationState> {

    override fun NavigationState.write(parcel: Parcel, flags: Int) {
        parcel.writeParcelableArray(chain.filterIsInstance<ComposeScreen>().toTypedArray(), 0)
    }

    override fun create(parcel: Parcel): NavigationState {
        val parcelableArray = parcel.readParcelableArray(ComposeScreen::class.java.classLoader).orEmpty()
        return NavigationState(Arrays.copyOf(parcelableArray, parcelableArray.size, Array<ComposeScreen>::class.java).toList())
    }

}