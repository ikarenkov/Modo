package com.github.terrakok.modo.containers

import android.os.Parcel
import androidx.compose.runtime.Composable
import com.github.terrakok.modo.MultiNavigation
import com.github.terrakok.modo.MultiReducer
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.RendererContent
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.defaultRendererContent

abstract class MultiScreen(
    initState: MultiNavigation,
    override val screenKey: String,
) : ContainerScreen<MultiNavigation>(
    initState,
) {

    override val reducer: NavigationReducer<MultiNavigation> = MultiReducer()

    constructor(parcel: Parcel) : this(
        parcel.readParcelable<MultiNavigation>(MultiNavigation::class.java.classLoader)!!,
        parcel.readString()!!
    )

    @Composable
    override fun Content() {
        SelectedScreen()
    }

    @Composable
    fun SelectedScreen(
        content: RendererContent = defaultRendererContent
    ) {
        Content(navigationState.containers[navigationState.selected], content)
    }

    @Composable
    fun Content(
        screen: Screen,
        content: RendererContent = defaultRendererContent
    ) {
        // create issue at google issue tracker
        super.InternalContent(screen, content)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(navigationState, flags)
        parcel.writeString(screenKey)
    }

    override fun describeContents(): Int = 0

}