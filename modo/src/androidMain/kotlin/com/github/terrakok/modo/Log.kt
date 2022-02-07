package com.github.terrakok.modo

import android.util.Log

actual fun logd(tag: String, msg: String) {
    Log.d(tag, msg)
}