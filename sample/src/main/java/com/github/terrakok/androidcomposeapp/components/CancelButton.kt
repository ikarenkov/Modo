package com.github.terrakok.androidcomposeapp.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CancelButton(
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painter = rememberVectorPainter(image = Icons.Default.Close),
            contentDescription = contentDescription
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MaterialTheme {
        CancelButton(onClick = {}, contentDescription = "cancel")
    }
}