package com.github.terrakok.androidcomposeapp.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Immutable
data class ButtonsState(
    val buttons: List<Pair<String, () -> Unit>>,
)

@Composable
fun ButtonsList(
    buttonsState: ButtonsState,
    modifier: Modifier = Modifier
) {
    val buttons = buttonsState.buttons
    Column(modifier) {
        for (index in buttons.indices step 2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .height(IntrinsicSize.Max)
            ) {
                ModoButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    text = buttons[index].first
                ) {
                    buttons[index].second()
                }
                Spacer(modifier = Modifier.size(8.dp))
                if (index + 1 in buttons.indices) {
                    ModoButton(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        text = buttons[index + 1].first
                    ) {
                        buttons[index + 1].second()
                    }
                } else {
                    Box(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
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
        Text(text = text, textAlign = TextAlign.Center)
    }
}

@Preview
@Composable
private fun ButtonsListPreview() {
    ButtonsList(
        buttonsState = ButtonsState(
            listOf(
                "Button 1",
                "Button 2",
                "Button 3",
                "Button with a very long text",
            ).map { it to {} }
        ),
    )
}