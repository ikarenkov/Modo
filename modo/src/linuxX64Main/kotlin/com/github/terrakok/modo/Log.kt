package com.github.terrakok.modo

internal actual fun logd(tag: String, msg: String) {
    println("$tag: $msg")
}