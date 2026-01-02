package com.github.terrakok.modo.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.savedstate.compose.LocalSavedStateRegistryOwner
import com.github.terrakok.modo.LocalSaveableStateHolder
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.util.currentOrThrow

/**
 * Integrates window-based overlays (Dialog, ModalBottomSheet, Popup, etc.) with Modo screen lifecycle and state management.
 *
 * Wrap your window-based overlay content with this function if:
 * - Your code inside uses composition locals: [LocalLifecycleOwner], [LocalSavedStateRegistryOwner],
 *   [LocalSaveableStateRegistry].
 * - You need [rememberSaveable] state to survive navigation inside overlay (e.g., overlay hidden on forward navigation,
 *   restored with saved state on back navigation).
 *
 * ## Why you need this
 *
 * Window-based overlays (Dialog, ModalBottomSheet, Popup) create a separate `AndroidComposeView` with its own
 * composition tree. This new composition tree receives default composition locals from the Activity/Window level,
 * not from your Screen's composition.
 *
 * **Problem 1: Composition locals point to wrong owners**
 *
 * The overlay inherits Activity-level `LocalLifecycleOwner`, `LocalViewModelStoreOwner`, and `LocalSavedStateRegistryOwner`
 * instead of Screen-level ones. This means:
 * - ViewModels get scoped to Activity instead of Screen (survive when they shouldn't)
 * - Lifecycle observers observe Activity lifecycle instead of Screen lifecycle
 * - SavedState gets tied to Activity instead of Screen's navigation state
 *
 * **Problem 2: State restoration breaks during navigation**
 *
 * `rememberSaveable` inside the overlay uses the Activity's SavedStateRegistry, which only survives configuration changes.
 * When you navigate away from a Screen (moving it to backstack), Modo preserves the Screen's state, but the overlay's
 * `rememberSaveable` state is disconnected from this system. When you navigate back, a **new** `AndroidComposeView` is
 * created from scratch with empty state - the overlay's previous state is lost because it was never saved in Modo's navigation state.
 *
 * ## Usage
 *
 * ```kotlin
 * MyScreen(...): Screen {
 *     @Composable
 *     override fun Content(modifier: Modifier) {
 *         ModalBottomSheet(
 *             onDismissRequest = { navigation.back() }
 *         ) {
 *             ProvideOverlayIntegration {
 *                 MyBottomSheetContent()
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * @param content The overlay content with restored screen composition locals and proper state management
 */
@Composable
fun Screen.ProvideOverlayIntegration(
    content: @Composable () -> Unit
) {
    val androidAdapter = remember(this) {
        ModoScreenAndroidAdapter.getOrNull(this) ?: error(
            "ModoScreenAndroidAdapter is not found. Ensure Modo is properly initialized."
        )
    }
    val saveableStateHolder = LocalSaveableStateHolder.currentOrThrow
    // Create a dialog-scoped state container within Modo's state management.
    // It is not enough just propagate LocalSaveableStateRegistry and LocalSavedStateRegistryOwner because of the logic of saving rememberSaveable.
    // So we need to use a new SaveableStateProvider with a new key (the same key as before will lead to the exception).
    saveableStateHolder.SaveableStateProvider(overlaySaveableStateKey) {
        // Provide screen-level composition locals (lifecycle, ViewModelStore, SavedStateRegistry)
        androidAdapter.ProvideCompositionLocals {
            content()
        }
    }
}

internal val Screen.overlaySaveableStateKey: String get() = screenKey.value + ".dialog"