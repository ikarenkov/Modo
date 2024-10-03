package io.github.ikarenkov.workshop.screens.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun ProfileScreenContent(
    name: String,
    description: String,
    onSetupProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier, contentPadding = PaddingValues(16.dp)) {
        item {
            ProfileHeader(
                name = name,
                description = description
            )
        }
        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSetupProfileClick
            ) {
                Text("Get training insights")
            }
        }
    }
}

@Composable
fun ProfileHeader(
    name: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp)) {
            Icon(
                rememberVectorPainter(image = Icons.Default.Face),
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Gray, CircleShape),
                contentDescription = "Avatar"
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(name, style = MaterialTheme.typography.titleLarge)
                Text(description, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Preview
@Composable
private fun PreviewProfile() {
    ProfileScreenContent(
        name = "Igor Karenkov",
        description = "Software Engineer",
        modifier = Modifier.fillMaxSize(),
        onSetupProfileClick = {}
    )
}