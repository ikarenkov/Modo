package com.github.terrakok.androidcomposeapp

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.terrakok.androidcomposeapp.saveable.ListScreen
import com.github.terrakok.modo.*
import com.github.terrakok.modo.android.compose.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import modo.sample.compose.feature.nestedNavigation.SampleNestedNavigationScreen
import modo.sample.compose.navigation.core.ButtonsList
import modo.sample.compose.navigation.core.RemovePrev

fun Browser(url: String) = ExternalScreen {
    Intent(Intent.ACTION_VIEW, Uri.parse(url))
}

@Parcelize
class SampleScreen(
    val i: Int,
    override val screenKey: String = uniqueScreenKey
) : ComposeScreen("ItemScreen $i") {

    @Composable
    override fun Content() {
        SampleContent(i, LocalNavigationDispatcher.current)
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
            "Back" to { modo.back() },
            "Forward" to { modo.forward(SampleScreen(i + 1)) },
            "Forward multiscreen" to {
                modo.forward(
                    SampleMultiComposeScreen(
                        MultiScreenState(List(3) { NavigationState(listOf(SampleScreen(0))) }, 0),
                    )
                )
            },
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
            "List sample" to { modo.forward(ListScreen()) },
            "Wrapped Modo" to { modo.forward(SampleNestedNavigationScreen(i + 1, NavigationState(listOf(SampleScreen(1))))) }
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
    SampleContent(0, Modo(AppReducer(LocalContext.current)))
}