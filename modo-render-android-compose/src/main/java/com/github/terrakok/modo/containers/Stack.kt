package com.github.terrakok.modo.containers

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Dialog
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.RendererContent
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.StackNavigationState
import com.github.terrakok.modo.StackReducer
import com.github.terrakok.modo.back
import com.github.terrakok.modo.defaultRendererContent
import com.github.terrakok.modo.generateScreenKey
import kotlinx.parcelize.Parcelize

abstract class Stack(
    navigationState: StackNavigationState,
    override val screenKey: String,
) : ContainerScreen<StackNavigationState>(navigationState), Parcelable {

    constructor(rootScreen: Screen) : this(
        StackNavigationState(listOf(rootScreen)),
        generateScreenKey()
    )

    constructor(navigationState: StackNavigationState) : this(
        navigationState,
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
        val screensToRender: List<Screen> by remember {
            derivedStateOf {
                val stack = navigationState.stack
                val topScreen = stack.last()
                if (topScreen is DialogScreen) {
                    val screen = stack.findLast { it !is DialogScreen }
                    listOfNotNull(screen, topScreen)
                } else {
                    listOf(stack.last())
                }
            }
        }
        for (screen in screensToRender) {
            if (screen is DialogScreen) {
                Dialog(
                    onDismissRequest = { back() },
                    properties = remember { screen.provideDialogProperties() }
                ) {
                    Content(screen = screen, content)
                }
            } else {
                Content(screen, content)
            }
        }
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

    companion object {

        @JvmStatic
        protected fun readSavedState(parcel: Parcel): SaveState = parcel.readParcelable(SaveState::class.java.classLoader)!!

    }

}