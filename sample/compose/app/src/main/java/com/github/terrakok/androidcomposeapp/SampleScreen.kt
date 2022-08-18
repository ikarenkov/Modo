package com.github.terrakok.androidcomposeapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.*
import com.github.terrakok.modo.android.compose.ComposeContainerScreen
import com.github.terrakok.modo.android.compose.ComposeMultiScreen
import com.github.terrakok.modo.android.compose.ComposeScreen
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SampleScreen(
    val i: Int
) : ComposeScreen("SampleScreen $i") {

    @Composable
    override fun Content() {
        SampleContent(i, this)
    }
}

class SampleContainerScreen(i: Int) : ComposeContainerScreen(
    "c_$i",
    SampleScreen(1)
) {
    @Composable
    override fun Content(screenContent: @Composable () -> Unit) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.padding(2.dp)) {
                Text("Container")
                Spacer(modifier = Modifier.weight(1f))
                Text(modifier = Modifier.clickable { exit() }, text = "CLOSE")
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

class SampleMultiScreen(i: Int) : ComposeMultiScreen(
    "m_$i",
    1,
    SampleScreen(1),
    SampleScreen(1),
    SampleScreen(1)
) {

    @Composable
    private fun Tab(modifier: Modifier, stackIndex: Int, i: Int) = Text(
        modifier = modifier
            .clickable { selectContainer(i) }
            .background(if (stackIndex == i) Color.LightGray else Color.White)
            .padding(16.dp),
        textAlign = TextAlign.Center,
        fontStyle = if (stackIndex == i) FontStyle.Italic else FontStyle.Normal,
        color = if (stackIndex == i) Color.Red else Color.Black,
        text = "Tab $i"
    )

    @Composable
    override fun Content(stackIndex: Int, screenContent: @Composable () -> Unit) {
        Column {
            Box(modifier = Modifier.weight(1f)) {
                screenContent()
            }
            Row {
                Tab(modifier = Modifier.weight(1f), stackIndex, 0)
                Tab(modifier = Modifier.weight(1f), stackIndex, 1)
                Tab(modifier = Modifier.weight(1f), stackIndex, 2)
            }
        }
    }
}

@Composable
private fun SampleContent(i: Int, modo: NavigationDispatcher) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxHeight(),
    ) {
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = "Screen $i",
            style = MaterialTheme.typography.h3,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(16.dp))
        val buttons = listOf(
            "Forward" to { modo.forward(SampleScreen(i + 1)) },
            "Back" to { modo.back() },
            "Replace" to { modo.replace(SampleScreen(i + 1)) },
            "New stack" to {
                modo.newStack(
                    SampleScreen(i + 1),
                    SampleScreen(i + 2),
                    SampleScreen(i + 3)
                )
            },
            "Multi forward" to {
                modo.forward(
                    SampleScreen(i + 1),
                    SampleScreen(i + 2),
                    SampleScreen(i + 3)
                )
            },
            "New root" to { modo.newStack(SampleScreen(i + 1)) },
            "Forward 3 sec delay" to {
                GlobalScope.launch {
                    delay(3000)
                    modo.forward(SampleScreen(i + 1))
                }
            },
            "Back to '3'" to { modo.backTo(SampleScreen(3).id) },
            "Container" to { modo.forward(SampleContainerScreen(i + 1)) },
            "Multiscreen" to { modo.forward(SampleMultiScreen(i + 1)) },
            "Exit" to { modo.exit() },
//            "Delete prev" to { modo.dispatch(RemovePrev()) },
//            "GitHub" to {
//                modo.launch(Browser("https://github.com/terrakok/Modo"))
//            },
            "List/Details" to { modo.forward(ListScreen()) },
        )
        ButtonsList(
            buttons,
            Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
fun PreviewSampleContent() {
    SampleContent(0, Modo)
}