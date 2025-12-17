package com.github.terrakok.modo.logs

internal object DevLog {
    fun v(tag: String? = null, message: () -> String) {
        android.util.Log.v(tag, message())
    }

    fun d(tag: String? = null, message: () -> String) {
        android.util.Log.d(tag, message())
    }

    fun i(tag: String? = null, message: () -> String) {
        android.util.Log.i(tag, message())
    }

    fun w(tag: String? = null, message: () -> String) {
        android.util.Log.w(tag, message())
    }

    fun e(tag: String? = null, message: () -> String) {
        android.util.Log.e(tag, message())
    }
}