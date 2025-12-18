package com.github.terrakok.modo.logs

@Suppress("UnusedParameter")
internal object DevLog {

    inline fun v(tag: String? = null, message: () -> String) = Unit

    inline fun d(tag: String? = null, message: () -> String) = Unit

    inline fun i(tag: String? = null, message: () -> String) = Unit

    inline fun w(tag: String? = null, message: () -> String) = Unit

    inline fun e(tag: String? = null, message: () -> String) = Unit

}