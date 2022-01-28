package com.github.terrakok.modo.android.compose

import android.os.Parcelable
import androidx.compose.runtime.Composable
import com.github.terrakok.modo.Screen

abstract class ComposeScreen(
    override val id: String
) : Screen, Parcelable {

    abstract val screenKey: String

    @Composable
    abstract fun Content()

    override fun toString() = "[$id]"
}