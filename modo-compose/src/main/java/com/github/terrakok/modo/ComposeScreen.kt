package com.github.terrakok.modo

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Basic interface for a Modo screen. Inherit for it directly if your screen is not going to have any nested screens.
 * Use [ContainerScreen] for screens with nested screens.
 * Use [DialogScreen] to display screen inside dialog.
 * @see ContainerScreen
 * @see DialogScreen
 */
interface Screen : Parcelable {

    // TODO: https://issuetracker.google.com/issues/239435908 - support default valuer for the modifier param.
    @Composable
    fun Content(modifier: Modifier)

    /**
     * Identifier for the screen that must be generated using [generateScreenKey] fun.
     * Must be unique for each screen, otherwise correctness is not guaranteed.
     */
    val screenKey: ScreenKey

}