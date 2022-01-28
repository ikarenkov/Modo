package com.github.terrakok.modo.android.compose

import java.util.concurrent.atomic.AtomicInteger

internal val screenCounterKey = AtomicInteger(0)

val uniqueScreenKey: String
    get() = "Screen#${screenCounterKey.getAndIncrement()}"

internal fun restoreScreenCounter(value: Int) {
    require(screenCounterKey.get() == 0) {
        "Trying to restore screen count, when screen count is $screenCounterKey != 0. " +
                "You must only call restoreScreenKey when screenCount == 0."
    }
    screenCounterKey.set(value)
}