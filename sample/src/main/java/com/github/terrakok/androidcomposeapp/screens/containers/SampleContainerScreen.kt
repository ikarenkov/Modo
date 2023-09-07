package com.github.terrakok.androidcomposeapp.screens.containers

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
import com.github.terrakok.androidcomposeapp.SlideTransition
import com.github.terrakok.androidcomposeapp.screens.SampleScreen
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.NavModel
import com.github.terrakok.modo.stack.StackNavModel
import com.github.terrakok.modo.stack.StackScreen
import com.github.terrakok.modo.stack.StackState
import com.github.terrakok.modo.stack.back
import kotlinx.parcelize.Parcelize

@Parcelize
class SampleContainerScreen(
    private val i: Int,
    private val navModel: StackNavModel
) : StackScreen(navModel) {

    constructor(
        i: Int,
        sampleNavigationState: StackState = StackState(SampleScreen(1))
    ) : this(i, NavModel(sampleNavigationState))

    @Composable
    override fun Content() {
        val parent = LocalContainerScreen.current as StackScreen
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

}

@Preview
@Composable
private fun PreviewContainerScreen() {
    SampleContainerScreen(1).Content()
}