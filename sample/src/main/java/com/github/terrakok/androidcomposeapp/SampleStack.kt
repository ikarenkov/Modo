package com.github.terrakok.androidcomposeapp

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Composable
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.StackNavigationState
import com.github.terrakok.modo.containers.Stack

class SampleStack : Stack {

    constructor(parcel: Parcel) : super(parcel)

    constructor(initialState: StackNavigationState) : super(navigationState = initialState)

    constructor(rootScreen: Screen) : this(initialState = StackNavigationState(rootScreen))

    @Composable
    override fun Content() {
        TopScreenContent {
            SlideTransition()
        }
    }

    companion object CREATOR : Parcelable.Creator<SampleStack> {
        override fun createFromParcel(parcel: Parcel): SampleStack =
            SampleStack(parcel)

        override fun newArray(size: Int): Array<SampleStack?> =
            arrayOfNulls(size)
    }
}