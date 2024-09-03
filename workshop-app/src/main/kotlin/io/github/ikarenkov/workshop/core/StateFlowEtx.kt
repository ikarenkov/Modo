package io.github.ikarenkov.workshop.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

fun <T, R> StateFlow<T>.mapStateFlow(
    coroutineScope: CoroutineScope,
    started: SharingStarted = SharingStarted.Eagerly,
    transform: (T) -> R
): StateFlow<R> = map(transform).stateIn(coroutineScope, started, transform(value))