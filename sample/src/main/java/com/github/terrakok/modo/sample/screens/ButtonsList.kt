package com.github.terrakok.modo.sample.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

fun ButtonsState(buttons: List<ModoButtonSpec>) = GroupedButtonsState(
    listOf(GroupedButtonsState.Group(null, buttons))
)

@Immutable
data class GroupedButtonsState(
    val groups: List<Group>,
) {
    @Immutable
    data class Group(
        val title: String?,
        val buttons: List<ModoButtonSpec>,
    )

}

@Composable
fun GroupedButtonsList(
    state: GroupedButtonsState,
    modifier: Modifier = Modifier
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        for (group in state.groups) {
            if (group.title != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            val buttons = group.buttons
            for (index in buttons.indices step 2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    ModoButton(
                        spec = buttons[index],
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    if (index + 1 in buttons.indices) {
                        ModoButton(
                            spec = buttons[index + 1],
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        )
                    } else {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Immutable
data class ModoButtonSpec(
    val text: String,
    val isEnabled: Boolean = true,
    val action: () -> Unit
)

@Composable
fun ModoButton(
    spec: ModoButtonSpec,
    modifier: Modifier = Modifier,
) {
    Button(onClick = spec.action, enabled = spec.isEnabled, modifier = modifier) {
        Text(text = spec.text, textAlign = TextAlign.Center)
    }
}

@Preview
@Composable
private fun GroupedButtonsListPreview() {
    GroupedButtonsList(
        state = GroupedButtonsState(
            listOf(
                GroupedButtonsState.Group(
                    "Group 1",
                    listOf(
                        ModoButtonSpec("Button 1") {},
                        ModoButtonSpec("Button 2", isEnabled = false) {},
                        ModoButtonSpec("Button 3") {},
                        ModoButtonSpec("Button with a very long text") {}
                    )
                )
            ),
        )
    )
}