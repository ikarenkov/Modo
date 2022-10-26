package com.github.terrakok.modo.android.compose

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Composable
import com.github.terrakok.modo.MultiNavigation
import com.github.terrakok.modo.MultiReducer
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.Screen

open class MultiScreen(
    initState: MultiNavigation,
    override val screenKey: String,
) : ComposeContainerScreen<MultiNavigation>(
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
        // Завезти баг на IssueTracker гугла
        super.InternalContent(screen, content)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(navigationState, flags)
        parcel.writeString(screenKey)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<MultiScreen> {
        override fun createFromParcel(parcel: Parcel): MultiScreen = MultiScreen(parcel)

        override fun newArray(size: Int): Array<MultiScreen?> = arrayOfNulls(size)
    }

}