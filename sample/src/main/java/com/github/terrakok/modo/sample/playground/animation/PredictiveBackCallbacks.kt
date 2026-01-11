package com.github.terrakok.modo.sample.playground.animation

import androidx.activity.BackEventCompat
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Simplified predictive back handler with pure callbacks.
 *
 * @param enabled whether the handler is enabled
 * @param onBackStarted called when predictive back gesture starts with the initial event
 * @param onBackProgressed called during the gesture with progress updates (0f to 1f)
 * @param onBackPressed called when gesture completes successfully
 * @param onBackCancelled called when gesture is cancelled
 */
@Composable
fun PredictiveBackCallbacks(
    enabled: Boolean = true,
    onBackStarted: (BackEventCompat) -> Unit = {},
    onBackProgressed: (BackEventCompat) -> Unit = {},
    onBackPressed: () -> Unit = {},
    onBackCancelled: () -> Unit = {}
) {
    // Capture current callbacks to avoid re-registration
    val currentOnBackStarted by rememberUpdatedState(onBackStarted)
    val currentOnBackProgressed by rememberUpdatedState(onBackProgressed)
    val currentOnBackPressed by rememberUpdatedState(onBackPressed)
    val currentOnBackCancelled by rememberUpdatedState(onBackCancelled)

    val backCallback = remember {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackStarted(backEvent: BackEventCompat) {
                currentOnBackStarted(backEvent)
            }

            override fun handleOnBackProgressed(backEvent: BackEventCompat) {
                currentOnBackProgressed(backEvent)
            }

            override fun handleOnBackPressed() {
                currentOnBackPressed()
            }

            override fun handleOnBackCancelled() {
                currentOnBackCancelled()
            }
        }
    }

    // Update enabled state
    LaunchedEffect(enabled) {
        backCallback.isEnabled = enabled
    }

    // Register callback with lifecycle
    val backDispatcher = checkNotNull(LocalOnBackPressedDispatcherOwner.current) {
        "No OnBackPressedDispatcherOwner was provided via LocalOnBackPressedDispatcherOwner"
    }.onBackPressedDispatcher

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, backDispatcher) {
        backDispatcher.addCallback(lifecycleOwner, backCallback)
        onDispose {
            backCallback.remove()
        }
    }
}