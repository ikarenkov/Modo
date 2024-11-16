package com.github.terrakok.modo.sample.screens.base

import android.content.Context
import android.content.Intent

fun Context.openShareText(
    text: String = "This is a test message.",
    title: String = "Share via"
) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    // Launch the share dialog
    startActivity(Intent.createChooser(shareIntent, title))
}