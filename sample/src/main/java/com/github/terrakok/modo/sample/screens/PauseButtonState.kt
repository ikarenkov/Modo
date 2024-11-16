package com.github.terrakok.modo.sample.screens

import android.content.Context
import android.content.Intent

fun PauseButtonSpec(context: Context) = ModoButtonSpec("Pause") {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "This is a test message.")
        type = "text/plain"
    }
    // Launch the share dialog
    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
}