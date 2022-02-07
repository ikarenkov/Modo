package com.github.terrakok.androidcomposeapp

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.terrakok.androidcomposeapp.saveable.ListScreen
import com.github.terrakok.modo.*
import com.github.terrakok.modo.android.compose.ComposeScreen
import com.github.terrakok.modo.android.compose.ExternalScreen
import com.github.terrakok.modo.android.compose.launch
import com.github.terrakok.modo.android.compose.uniqueScreenKey
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

fun Browser(url: String) = ExternalScreen {
    Intent(Intent.ACTION_VIEW, Uri.parse(url))
}

@Parcelize
class SampleScreen(
    val i: Int,
    override val screenKey: String = uniqueScreenKey
) : ComposeScreen("ItemScreen $i") {
    private val modo get() = App.INSTANCE.modo

    @Composable
    override fun Content() {
        SampleContent(i, modo)
    }

}

@Composable
private fun SampleContent(i: Int, modo: Modo) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxHeight()
    ) {
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = "Screen $i",
            fontSize = 28.sp,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.size(8.dp))
        val buttons = listOf(
            "Back" to { modo.back() },
            "Forward" to { modo.forward(SampleScreen(i + 1)) },
            "Replace" to { modo.replace(SampleScreen(i + 1)) },
            "Delete prev" to { modo.dispatch(RemovePrev()) },
            "Multi forward" to {
                modo.forward(
                    SampleScreen(i + 1),
                    SampleScreen(i + 2),
                    SampleScreen(i + 3)
                )
            },
            "New stack" to {
                modo.newStack(
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
            "GitHub" to {
                modo.launch(Browser("https://github.com/terrakok/Modo"))
            },
            "Exit" to { modo.exit() },
            "List sample" to { modo.forward(ListScreen()) }
        )
        for (index in buttons.indices step 2) {
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                ModoButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    text = buttons[index].first
                ) {
                    buttons[index].second()
                }
                if (index + 1 in buttons.indices) {
                    Spacer(modifier = Modifier.size(8.dp))
                    ModoButton(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        text = buttons[index + 1].first
                    ) {
                        buttons[index + 1].second()
                    }
                }
            }
        }
    }
}

@Composable
fun ModoButton(
    modifier: Modifier,
    text: String,
    action: () -> Unit
) {
    Button(onClick = action, modifier) {
        Text(text = text)
    }
}