package com.github.terrakok.modo.sample.screens.lifecycle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.sample.screens.ModoButton
import com.github.terrakok.modo.sample.screens.ModoButtonSpec
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.back
import com.github.terrakok.modo.util.getActivity
import kotlinx.parcelize.Parcelize

/**
 * Sample of showing keyboard when animation is finished and hiding, when animation started.
 * Using ON_RESUME and ON_PAUSE to achieve this.
 */
@Parcelize
class KeyboardWithLifecycleScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {
    @Composable
    override fun Content(modifier: Modifier) {
        Column(modifier.windowInsetsPadding(WindowInsets.systemBars)) {
            val stackNavigation = LocalStackNavigation.current
            ModoButton(ModoButtonSpec("Back") { stackNavigation.back() })

            Text("Show ON_RESUME, hide ON_PAUSE")

            val (text, setText) = rememberSaveable { mutableStateOf("") }
            val focusRequester = remember { FocusRequester() }
            val lifecycleOwner = LocalLifecycleOwner.current
            val keyboardController = LocalSoftwareKeyboardController.current
            val context = LocalContext.current
            DisposableEffect(this) {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> {
                            focusRequester.requestFocus()
                        }
                        Lifecycle.Event.ON_PAUSE -> {
                            if (context.getActivity()?.isChangingConfigurations != true) {
                                focusRequester.freeFocus()
                                keyboardController?.hide()
                            }
                        }
                        else -> {}
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }
            TextField(text, setText, modifier = Modifier.focusRequester(focusRequester))
        }
    }
}