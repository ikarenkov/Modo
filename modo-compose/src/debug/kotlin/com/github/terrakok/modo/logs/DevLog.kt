package com.github.terrakok.modo.logs

import android.util.Log

@Suppress("LogConditional")
internal object DevLog {
    fun v(tag: String? = null, message: () -> String) {
        Log.v(tag, message())
    }

    fun d(tag: String? = null, message: () -> String) {
        Log.d(tag, message())
    }

    fun i(tag: String? = null, message: () -> String) {
        Log.i(tag, message())
    }

    fun w(tag: String? = null, message: () -> String) {
        Log.w(tag, message())
    }

    fun e(tag: String? = null, message: () -> String) {
        Log.e(tag, message())
    }
}