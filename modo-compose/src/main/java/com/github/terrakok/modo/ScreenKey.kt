package com.github.terrakok.modo

import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.Parcelize
import java.util.concurrent.atomic.AtomicInteger

/**
 * Identifier to distinguish screens and screen dependencies.
 * @see [Screen]
 */
@Parcelize
@JvmInline
value class ScreenKey(val value: String) : Parcelable

internal val screenCounterKey = AtomicInteger(-1)

fun generateScreenKey(): ScreenKey = ScreenKey("Screen#${screenCounterKey.incrementAndGet()}")

/**
 * Restores the screen counter to the given value.
 * It's safe to call this multiple times, because it restores the value only if it's not already set.
 */
internal fun restoreScreenCounter(value: Int) {
    if (screenCounterKey.get() == -1 || screenCounterKey.get() == value) {
        screenCounterKey.set(value)
    } else {
        Log.w(
            "Modo",
            "Trying to restore screen count, when screen count is $screenCounterKey != 0. " +
                "You must only call restoreScreenKey when screenCount == 0."
        )
    }
}