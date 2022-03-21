package com.github.terrakok.modo

import android.util.Log

internal actual fun logd(tag: String, msg: String) {
    Log.d(tag, msg)
}