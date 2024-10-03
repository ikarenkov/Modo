package io.github.ikarenkov.workshop.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.StackNavContainer
import com.github.terrakok.modo.stack.forward
import kotlinx.parcelize.Parcelize

// TODO: Workshop 7.1 - create first screen for email input
@Parcelize
class EmailScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        val navigation: StackNavContainer = LocalStackNavigation.current
        EmailScreenContent(
            modifier = modifier,
            onContinueClick = { email ->
                navigation.forward(AuthCodeScreen())
            }
        )
    }
}

@Composable
internal fun EmailScreenContent(
    modifier: Modifier = Modifier,
    onContinueClick: (email: String) -> Unit
) {
    Column(
        modifier.padding(16.dp)
    ) {
        Text("Authorization and registration", style = MaterialTheme.typography.headlineLarge)
        Text("Enter your email to continue", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.weight(1f))
        val (text, setText) = rememberSaveable { mutableStateOf("") }
        TextField(
            value = text,
            onValueChange = setText,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onContinueClick(text)
            }
        ) {
            Text("Continue")
        }
    }
}

@Preview
@Composable
private fun PreviewEmailScreenContent() {
    EmailScreenContent(Modifier.fillMaxSize(), {})
}