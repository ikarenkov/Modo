package com.github.terrakok.androidcomposeapp

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.StackNavigationState
import com.github.terrakok.modo.containers.Stack
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.back
import com.github.terrakok.modo.containers.LocalContainerScreen

class SampleStackScreen : Stack {

    constructor(
        i: Int,
        sampleNavigationState: StackNavigationState = StackNavigationState(SampleScreen(1))
    ) : super(sampleNavigationState, generateScreenKey()) {
        this.i = i
    }

    private constructor(parcel: Parcel) : super(parcel) {
        i = parcel.readInt()
    }

    private val i: Int

    override val reducer: NavigationReducer<StackNavigationState>
        get() = super.reducer

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    override fun Content() {
        val parent = LocalContainerScreen.current
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.padding(2.dp)) {
                Text("Container $i")
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    modifier = Modifier.clickable {
                        parent.back()
                    },
                    text = "[X]"
                )
            }
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.Gray)
                    .padding(2.dp)
                    .background(Color.White)
            ) {
                TopScreenContent {
                    SlideTransition()
                }
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(i)
    }

    companion object CREATOR : Parcelable.Creator<SampleStackScreen> {

        override fun createFromParcel(parcel: Parcel): SampleStackScreen = SampleStackScreen(parcel)

        override fun newArray(size: Int): Array<SampleStackScreen?> = arrayOfNulls(size)
    }

}

@Preview
@Composable
private fun PreviewContainerScreen() {
    SampleStackScreen(1).Content()
}