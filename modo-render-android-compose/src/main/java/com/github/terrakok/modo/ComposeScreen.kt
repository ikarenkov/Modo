package com.github.terrakok.modo

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.github.terrakok.modo.containers.ContainerScreen

interface Screen : Parcelable {

    @Composable
    fun Content()

    val screenKey: String

    val screenName: String get() = "Screen"

}