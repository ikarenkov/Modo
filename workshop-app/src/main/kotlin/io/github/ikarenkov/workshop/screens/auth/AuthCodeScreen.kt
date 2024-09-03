package io.github.ikarenkov.workshop.screens.auth

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.setStack
import io.github.ikarenkov.workshop.screens.main.MainTabScreenFinal
import kotlinx.parcelize.Parcelize

// TODO: Workshop 7.2 - creating next screen
@Parcelize
class AuthCodeScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        val navigation = LocalStackNavigation.current
        val keyboardController = LocalSoftwareKeyboardController.current
        AuthCodeScreenContent(
            modifier = modifier.fillMaxWidth(),
            onCodeEntered = { code ->
                keyboardController?.hide()
                navigation.setStack(MainTabScreenFinal())
            }
        )
    }
}

@Composable
private fun AuthCodeScreenContent(
    onCodeEntered: (code: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TODO: Workshop 7.2 - keyboard focus when animation finished
        val focusRequester = remember { FocusRequester() }
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        focusRequester.requestFocus()
                    }
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        Text("Enter the code", style = MaterialTheme.typography.headlineLarge)

        Spacer(Modifier.height(20.dp))

        val (text, setText) = rememberSaveable { mutableStateOf("") }
        CodeInputTextField(
            codeLength = 4,
            value = text,
            onValueChange = {
                setText(it)
                if (it.length == 4) {
                    onCodeEntered(it)
                }
            },
            modifier = Modifier
                .focusRequester(focusRequester)
        )
    }
}

@Preview
@Composable
private fun PreviewAuthCodeScreen() {
    AuthCodeScreenContent(modifier = Modifier.fillMaxSize(), onCodeEntered = {})
}

@Composable
fun CodeInputTextField(
    codeLength: Int,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.length > codeLength) {
                onValueChange(newValue.take(codeLength))
            } else {
                onValueChange(newValue)
            }
        },
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Number,
            showKeyboardOnFocus = true
        ),
        modifier = modifier,
    ) { innerTextField ->
        Row {
            repeat(codeLength) { index ->
                val char = if (index < value.length) value[index] else null
                CharBox(
                    char,
                    focused = index == value.length
                )
                if (index != codeLength - 1) {
                    Spacer(Modifier.width(8.dp))
                }
            }
        }

    }
}

@Preview
@Composable
private fun PreviewCodeInputTextField() {
    CodeInputTextField(4, "1234", {})
}

@Composable
private fun CharBox(
    char: Char?,
    focused: Boolean = false
) {
    val borderColor by animateColorAsState(if (focused) Color.Black else Color.LightGray)
    Text(
        text = char?.toString().orEmpty(),
        modifier = Modifier
            .width(40.dp)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center
    )
}

@Preview
@Composable
private fun PreviewCharBox() {
    CharBox('1')
}

@Preview
@Composable
private fun PreviewEmptyCharBox() {
    CharBox(' ')
}