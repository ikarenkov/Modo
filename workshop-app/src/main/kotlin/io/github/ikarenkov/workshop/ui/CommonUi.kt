package io.github.ikarenkov.workshop.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TitleCell(
    title: String,
    modifier: Modifier = Modifier,
    @SuppressLint("ComposableLambdaParameterNaming") contentRight: @Composable () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.labelLarge)
        contentRight()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputNumRow(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    valueName: String,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier
) {
    TitleCell(title, modifier) {
        val interactionSource = remember { MutableInteractionSource() }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Number
            ),
            modifier = textFieldModifier.width(100.dp),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End)
        ) {
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = it,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
                suffix = {
                    Text(valueName)
                }
            )
        }
    }
}

@Preview
@Composable
private fun PreviewInputStringRow() {
    InputNumRow(
        title = "Height",
        value = "180",
        onValueChange = {},
        valueName = "sm",
        modifier = Modifier.fillMaxWidth()
    )
}
