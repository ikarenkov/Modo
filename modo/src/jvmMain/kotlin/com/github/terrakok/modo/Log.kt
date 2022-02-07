package com.github.terrakok.modo

actual fun logd(tag: String, msg: String) {
    println("$tag: $msg")
}