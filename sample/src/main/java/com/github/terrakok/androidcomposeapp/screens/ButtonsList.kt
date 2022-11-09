package com.github.terrakok.androidcomposeapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ButtonsList(
    buttons: List<Pair<String, () -> Any>>,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        for (index in buttons.indices step 2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                ModoButton(
                    modifier = Modifier.weight(1f),
                    text = buttons[index].first
                ) {
                    buttons[index].second()
                }
                Spacer(modifier = Modifier.size(8.dp))
                if (index + 1 in buttons.indices) {
                    ModoButton(
                        modifier = Modifier.weight(1f),
                        text = buttons[index + 1].first
                    ) {
                        buttons[index + 1].second()
                    }
                } else {
                    Box(modifier = Modifier.weight(1f))
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