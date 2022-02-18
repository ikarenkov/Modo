package com.github.terrakok.androidcomposeapp

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.github.terrakok.modo.AbstractMultiScreen
import com.github.terrakok.modo.MultiScreenState
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.android.compose.ComposeMultiScreen
import com.github.terrakok.modo.android.compose.ComposeScreen
import com.github.terrakok.modo.android.compose.uniqueScreenKey
import com.github.terrakok.modo.selectStack
import com.github.terrakok.modo.selectedStack
import com.github.terrakok.modo.stacks
import kotlinx.parcelize.IgnoredOnParcel

class SampleMultiScreen(
    multiScreenState: MultiScreenState,
    override val screenKey: String = uniqueScreenKey
) : ComposeMultiScreen(multiScreenState, "SampleMultiScreen"), AbstractMultiScreen {

    @IgnoredOnParcel
    val modo = App.INSTANCE.modo

    constructor(parcel: Parcel) : this(
        readMultiscreenState(parcel),
        parcel.readString()!!
    )

    // FIXME: replace it with general solution for saving ComposeMultiScreen
    @Composable
    override fun Content(innerContent: @Composable () -> Unit) {
        Column {
            Box(Modifier.weight(1f)) {
                innerContent()
            }
            BottomNavigation(multiScreenState.stacks.size, multiScreenState.selectedStack) { modo.selectStack(it) }
        }
    }

    // FIXME: replace it with general solution for saving ComposeMultiScreen
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(stacks.size)
        for (navigationState in stacks) {
            parcel.writeParcelableArray(navigationState.chain.filterIsInstance<ComposeScreen>().toTypedArray(), 0)
        }
        parcel.writeInt(selectedStack)
        parcel.writeString(screenKey)
    }

    // FIXME: replace it with general solution for saving ComposeMultiScreen
    override fun describeContents(): Int {
        return 0
    }

    // FIXME: replace it with general solution for saving ComposeMultiScreen
    companion object CREATOR : Parcelable.Creator<SampleMultiScreen> {
        override fun createFromParcel(parcel: Parcel): SampleMultiScreen {
            return SampleMultiScreen(parcel)
        }

        override fun newArray(size: Int): Array<SampleMultiScreen?> {
            return arrayOfNulls(size)
        }

    }

}

// FIXME: replace it with general solution for saving ComposeMultiScreen
private fun readMultiscreenState(parcel: Parcel): MultiScreenState {
    val stackSize = parcel.readInt()
    val stacks = List(stackSize) {
        NavigationState(
            parcel.readParcelableArray(SampleMultiScreen::class.java.classLoader).orEmpty().toList() as List<ComposeScreen>
        )
    }
    return MultiScreenState(
        stacks,
        parcel.readInt()
    )
}

@Composable
fun BottomNavigation(stacksSize: Int, selectedStack: Int, onClick: (Int) -> Unit) {
    val items = sequenceOf(
        Icons.Default.Home,
        Icons.Default.Phone,
        Icons.Default.Star,
        Icons.Default.Person,
    )
    androidx.compose.material.BottomNavigation {
        items.take(stacksSize).forEachIndexed { index, item ->
            BottomNavigationItem(
                icon = { Icon(imageVector = item, contentDescription = "") },
                label = { Text(text = index.toString(), fontSize = 9.sp) },
                alwaysShowLabel = true,
                selected = index == selectedStack,
                onClick = { onClick(index) }
            )
        }
    }
}
