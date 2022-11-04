package com.github.terrakok.androidcomposeapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.NavigationDispatcher
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.back
import com.github.terrakok.modo.backTo
import com.github.terrakok.modo.containers.LocalContainerScreen
import com.github.terrakok.modo.exit
import com.github.terrakok.modo.forward
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.newStack
import com.github.terrakok.modo.replace
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
class SampleScreen(
    private val i: Int,
    override val screenKey: String = generateScreenKey()
) : Screen {

    @Composable
    override fun Content() {
        val parent = LocalContainerScreen.current
        SampleContent(i, parent)
    }
}

@Composable
internal fun SampleContent(i: Int, navigator: NavigationDispatcher) {
    var counter by rememberSaveable { mutableStateOf(0) }
    LaunchedEffect(key1 = Unit) {
        while (isActive) {
            delay(100)
            counter++
        }
    }
    Column(
        modifier = Modifier.padding(8.dp),
    ) {
        Text(
            text = counter.toString()
        )
        Text(
            text = "Screen $i",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(16.dp))
        ButtonsList(
            rememberButtons(navigator, i),
            Modifier.weight(1f, fill = false)
        )
    }
}

@Composable
private fun rememberButtons(
    navigator: NavigationDispatcher,
    i: Int
) = remember {
    listOf(
        "Forward" to { navigator.forward(SampleScreen(i + 1)) },
        "Back" to { navigator.back() },
        "Replace" to { navigator.replace(SampleScreen(i + 1)) },
        "New stack" to {
            navigator.newStack(
                SampleScreen(i + 1),
                SampleScreen(i + 2),
                SampleScreen(i + 3)
            )
        },
        "Multi forward" to {
            navigator.forward(
                SampleScreen(i + 1),
                SampleScreen(i + 2),
                SampleScreen(i + 3)
            )
        },
        "New root" to { navigator.newStack(SampleScreen(i + 1)) },
        "Forward 3 sec delay" to {
            GlobalScope.launch {
                delay(3000)
                navigator.forward(SampleScreen(i + 1))
            }
        },
        "Back to '3'" to { navigator.backTo(SampleScreen(3)) },
        "Container" to { navigator.forward(SampleStackScreen(i + 1)) },
        "Multiscreen" to { navigator.forward(SampleMultiScreen()) },
        "Exit" to { navigator.exit() },
        "Exit App" to { },
        "List/Details" to { navigator.forward(ListScreen()) },
        "2 items screen" to { navigator.forward(TwoTopItemsStackScreen(i + 1)) },
//                "Demo" to { navigator.forward(SaveableStateHolderDemoScreen()) },
        "Dialog" to { navigator.forward(SampleDialog(i + 1)) },
        "Model" to { navigator.forward(ModelSampleScreen()) },
    )
}

@Preview
@Composable
fun PreviewSampleContent() {
    SampleContent(0) {}
}