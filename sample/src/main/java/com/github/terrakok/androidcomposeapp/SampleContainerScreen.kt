package com.github.terrakok.androidcomposeapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.StackNavigation
import com.github.terrakok.modo.android.compose.Stack
import com.github.terrakok.modo.exit
import com.github.terrakok.modo.navigator

class SampleContainerScreen(i: Int) : Stack("c_$i", SampleScreen(1)) {
    @Composable
    override fun Content(state: NavigationState, screenContent: @Composable () -> Unit) {
        state as StackNavigation
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.padding(2.dp)) {
                Text("Container")
                Spacer(modifier = Modifier.weight(1f))
                Text(modifier = Modifier.clickable { navigator.exit() }, text = "[X]")
            }
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.Gray)
                    .padding(2.dp)
                    .background(Color.White)
            ) {
                screenContent()
            }
        }
    }
}