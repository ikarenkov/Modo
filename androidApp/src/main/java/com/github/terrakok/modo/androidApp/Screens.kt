package com.github.terrakok.modo.androidApp

import android.content.Intent
import android.net.Uri
import com.github.terrakok.modo.android.AppScreen
import com.github.terrakok.modo.android.ExternalScreen
import com.github.terrakok.modo.android.MultiAppScreen

object Screens {
    fun Sample(id: Int) = AppScreen(id.toString()) {
        SampleFragment.create(id)
    }

    fun MultiStack() = MultiAppScreen(
        "MultiStack",
        listOf(Sample(100), Sample(200), Sample(300)),
        1
    )

    fun Browser(url: String) = ExternalScreen {
        Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }
}