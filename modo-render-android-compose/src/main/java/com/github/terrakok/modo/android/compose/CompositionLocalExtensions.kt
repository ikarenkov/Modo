package com.github.terrakok.modo.android.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal

val <T> ProvidableCompositionLocal<T?>.currentOrThrow: T
    @Composable
    get() = current ?: error("CompositionLocal is null")