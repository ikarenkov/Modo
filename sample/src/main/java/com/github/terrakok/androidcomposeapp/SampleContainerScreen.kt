package com.github.terrakok.androidcomposeapp

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
import com.github.terrakok.modo.StackNavigation
import com.github.terrakok.modo.Stack
import com.github.terrakok.modo.exit
import com.github.terrakok.modo.navigator
import java.util.concurrent.atomic.AtomicInteger

class SampleContainerScreen : Stack("c_${index.getAndIncrement()}", SampleScreen(1)) {
    companion object {
        private val index = AtomicInteger(0)
    }
    @Composable
    override fun Content(state: StackNavigation, screenContent: @Composable () -> Unit) {
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

@Preview
@Composable
private fun PreviewContainerScreen() {
    SampleMultiScreen().Content()
}