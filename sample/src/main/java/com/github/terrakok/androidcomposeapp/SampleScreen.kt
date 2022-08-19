package com.github.terrakok.androidcomposeapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.*
import com.github.terrakok.modo.android.compose.ComposeScreen
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SampleScreen(
    val i: Int
) : ComposeScreen {
    override val id = i.toString()

    @Composable
    override fun Content() {
        SampleContent(i, navigator)
    }
}

@Composable
private fun SampleContent(i: Int, navigator: Navigator) {
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
            "Back to '3'" to { navigator.backTo(SampleScreen(3).id) },
            "Container" to { navigator.forward(SampleContainerScreen(i + 1)) },
            "Multiscreen" to { navigator.forward(SampleMultiScreen(i + 1)) },
            "Exit" to { navigator.exit() },
            "Exit App" to { Modo.dispatchToRoot(Exit) },
            "List/Details" to { navigator.forward(ListScreen()) },
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
    SampleContent(0, Navigator {})
}