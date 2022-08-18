package com.github.terrakok.androidcomposeapp

import androidx.compose.foundation.layout.*
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