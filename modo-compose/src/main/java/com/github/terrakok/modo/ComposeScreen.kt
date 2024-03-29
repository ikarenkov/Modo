package com.github.terrakok.modo

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface Screen : Parcelable {

    // TODO: https://issuetracker.google.com/issues/239435908 - support default valuer for the modifier param.
    @Composable
    fun Content(modifier: Modifier)

    val screenKey: ScreenKey

}