package com.github.terrakok.modo.android.compose

import android.os.Parcel
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.SaveableStateHolder
import com.github.terrakok.modo.*
import kotlinx.parcelize.Parceler
import java.util.*

val LocalModo = staticCompositionLocalOf<Modo> { TODO("error") }

abstract class WrapperComposeScreen(
    id: String,
    initWreppeeState: NavigationState,
    reducer: NavigationReducer,
) : ComposeScreen(id), WrapperScreen {

    abstract var saveableWreppeeState: NavigationState

    abstract fun onNewState(state: NavigationState)

    @Composable
    abstract fun Content(innerContent: @Composable () -> Unit)

    override val modo: Modo = ModoWrapper(reducer) {
        saveableWreppeeState = it
        onNewState(it)
    }.apply { dispatch(SetState(initWreppeeState)) }

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
        (modo.render as? ComposeRenderer)?.clearStateHolder(stateHolder, true)
    }

    @Composable
    final override fun Content() {
        val outerModo = LocalModo.current
        remember {
            if (modo.render == null) {
                modo.render = createRenderer()
            }
        }
        BackHandler {
            (if (modo.state.chain.size == 1) outerModo else modo).back()
        }
        CompositionLocalProvider(
            LocalModo provides modo
        ) {
            Content {
                (modo.render as? ComposeRenderer)?.Content()
            }
        }
    }

}

private class ModoWrapper(
    reducer: NavigationReducer,
    private val onNewState: (NavigationState) -> Unit
) : Modo(reducer) {

    override fun dispatch(action: NavigationAction) {
        super.dispatch(action)
        onNewState(state)
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