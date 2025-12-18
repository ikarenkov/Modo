package com.github.terrakok.modo.sample.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "Back to previous screen",
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painter = rememberVectorPainter(image = Icons.AutoMirrored.Filled.ArrowBack),
            contentDescription = contentDescription
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MaterialTheme {
        BackButton(onClick = {}, contentDescription = "cancel")
    }
}