package com.github.terrakok.modo

import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.Parcelize
import java.util.concurrent.atomic.AtomicInteger

@Parcelize
@JvmInline
value class ScreenKey(val value: String) : Parcelable

internal val screenCounterKey = AtomicInteger(-1)

fun generateScreenKey(): ScreenKey = ScreenKey("Screen#${screenCounterKey.incrementAndGet()}")

internal fun restoreScreenCounter(value: Int) {
    if (screenCounterKey.get() == -1 || screenCounterKey.get() == value) {
        screenCounterKey.set(value)
    } else {
        Log.w(
            "Modo", "Trying to restore screen count, when screen count is $screenCounterKey != 0. " +
            "You must only call restoreScreenKey when screenCount == 0."
        )
    }
}