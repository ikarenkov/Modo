package com.github.terrakok.androidcomposeapp

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.terrakok.modo.*
import com.github.terrakok.modo.android.compose.ComposeScreen
import com.github.terrakok.modo.android.compose.ExternalScreen
import com.github.terrakok.modo.android.compose.launch
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

fun Browser(url: String) = ExternalScreen {
    Intent(Intent.ACTION_VIEW, Uri.parse(url))
}

@Parcelize
class SampleScreen(val i: Int) : ComposeScreen("ItemScreen $i") {
    private val modo get() = App.INSTANCE.modo

    @Composable
    override fun Content() {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxHeight()
        ) {
            Text(
                text = "Screen $i",
                fontSize = 28.sp,
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
                    .fillMaxSize(),
                textAlign = TextAlign.Center
            )
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
                    text = "Back"
                ) {
                    modo.back()
                }
                Spacer(modifier = Modifier.size(8.dp))
                ModoButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    text = "Forward"
                ) {
                    modo.forward(SampleScreen(i + 1))
                }
            }
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
                    text = "Replace"
                ) {
                    modo.replace(SampleScreen(i + 1))
                }
                Spacer(modifier = Modifier.size(8.dp))
                ModoButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    text = "Multi forward"
                ) {
                    modo.forward(
                        SampleScreen(i + 1),
                        SampleScreen(i + 2),
                        SampleScreen(i + 3)
                    )
                }
            }
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
                    text = "New stack"
                ) {
                    modo.newStack(
                        SampleScreen(i + 1),
                        SampleScreen(i + 2),
                        SampleScreen(i + 3)
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                ModoButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    text = "New root"
                ) {
                    modo.newStack(SampleScreen(i + 1))
                }
            }
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
                    text = "Forward 3s"
                ) {
                    GlobalScope.launch {
                        delay(3000)
                        modo.forward(SampleScreen(i + 1))
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                ModoButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    text = "Back to '3'"
                ) {
                    modo.backTo(SampleScreen(3).id)
                }
            }
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
                    text = "GitHub"
                ) {
                    modo.launch(Browser("https://github.com/terrakok/Modo"))
                }
                Spacer(modifier = Modifier.size(8.dp))
                ModoButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    text = "Exit"
                ) {
                    modo.exit()
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
    Box(
        modifier = modifier
            .background(Color.Gray)
            .clickable { action() }
    ) {
        Text(
            text = text,
            fontSize = 28.sp,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            textAlign = TextAlign.Center
        )
    }
}