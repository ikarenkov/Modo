package io.github.ikarenkov.workshop.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

fun <T, R> StateFlow<T>.mapStateFlow(
    coroutineScope: CoroutineScope,
    started: SharingStarted = SharingStarted.Eagerly,
    transform: (T) -> R
): StateFlow<R> = map(transform).stateIn(coroutineScope, started, transform(value))

fun <T1, T2, R> combineStateFlow(
    flow: StateFlow<T1>,
    flow2: StateFlow<T2>,
    coroutineScope: CoroutineScope,
    started: SharingStarted = SharingStarted.Eagerly,
    transform: (T1, T2) -> R
): StateFlow<R> = combine(flow, flow2, transform)
    .stateIn(
        scope = coroutineScope,
        started = started,
        initialValue = transform(flow.value, flow2.value)
    )