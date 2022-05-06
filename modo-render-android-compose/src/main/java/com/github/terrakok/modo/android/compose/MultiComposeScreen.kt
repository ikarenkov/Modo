package com.github.terrakok.modo.android.compose

import android.os.Parcel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.github.terrakok.modo.MultiScreen
import com.github.terrakok.modo.MultiScreenState
import com.github.terrakok.modo.NavigationState
import kotlinx.parcelize.Parceler
import java.util.*

abstract class MultiComposeScreen(initialMultiScreenState: MultiScreenState, id: String) : ComposeScreen(id), MultiScreen {

    /**
     * @param innerContent - content of currently displayed screen
     */
    @Composable
    abstract fun Content(innerContent: @Composable () -> Unit)

    /**
     * Override this field in constructor for simple state saving/restoration using Parcelize kotlin plugin
     */
    abstract var saveableMultiScreenState: MultiScreenState

    // Field allows to work with snapshot compose system through multiScreenState
    private val renderState = mutableStateOf(initialMultiScreenState)

    override var multiScreenState = initialMultiScreenState
        set(value) {
            field = value
            saveableMultiScreenState = value
            renderState.value = value
        }
        get() = renderState.value

    @Composable
    final override fun Content() {
        Content {
            val selectedScreen = multiScreenState.stacks[multiScreenState.selectedStack].chain.last()
            require(selectedScreen is ComposeScreen)
            selectedScreen.Content()
        }
    }

}

/**
 * Parceler implementation for [MultiScreenState]
 */
object MultiScreenStateParceler : Parceler<MultiScreenState> {

    override fun MultiScreenState.write(parcel: Parcel, flags: Int) {
        parcel.writeInt(stacks.size)
        for (navigationState in stacks) {
            parcel.writeParcelableArray(navigationState.chain.filterIsInstance<ComposeScreen>().toTypedArray(), 0)
        }
        parcel.writeInt(selectedStack)
    }

    override fun create(parcel: Parcel): MultiScreenState {
        val stackSize = parcel.readInt()
        val stacks = List(stackSize) {
            val parcelableArray = parcel.readParcelableArray(ComposeScreen::class.java.classLoader).orEmpty()
            NavigationState(Arrays.copyOf(parcelableArray, parcelableArray.size, Array<ComposeScreen>::class.java).toList())
        }
        return MultiScreenState(
            stacks,
            parcel.readInt()
        )
    }

}