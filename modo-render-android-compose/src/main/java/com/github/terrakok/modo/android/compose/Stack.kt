package com.github.terrakok.modo.android.compose

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Composable
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.StackNavigationState
import com.github.terrakok.modo.StackReducer
import kotlinx.parcelize.Parcelize

open class Stack(
    navigationState: StackNavigationState,
    override val screenKey: String,
) : ComposeContainerScreen<StackNavigationState>(navigationState), Parcelable {

    constructor(rootScreen: ComposeScreen) : this(
        StackNavigationState(listOf(rootScreen)),
        generateScreenKey()
    )

    constructor(parcel: Parcel) : this(readSavedState(parcel))

    constructor(saveState: SaveState) : this(saveState.navigationState, saveState.screenKey)

    override val reducer: NavigationReducer<StackNavigationState> = StackReducer()

    /**
     * Default implementation last screen from stack.
     */
    @Composable
    override fun Content() {
        TopScreenContent()
    }

    /**
     * Renders last screen from stack.
     */
    @Composable
    protected fun TopScreenContent(
        content: RendererContent = defaultRendererContent
    ) {
        Content(navigationState.stack.last(), content)
    }

    @Composable
    protected fun Content(
        screen: Screen,
        content: RendererContent = defaultRendererContent
    ) {
        super.InternalContent(screen, content)
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(SaveState(navigationState, screenKey), flags)
    }

    @Parcelize
    data class SaveState(
        val navigationState: StackNavigationState,
        val screenKey: String
    ) : Parcelable

    companion object CREATOR : Parcelable.Creator<Stack> {

        @JvmStatic
        protected fun readSavedState(parcel: Parcel): SaveState = parcel.readParcelable(SaveState::class.java.classLoader)!!

        override fun createFromParcel(parcel: Parcel): Stack = Stack(parcel)

        override fun newArray(size: Int): Array<Stack?> = arrayOfNulls(size)
    }

}