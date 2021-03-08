package com.github.terrakok.modo.androidApp

import android.content.Intent
import android.net.Uri
import com.github.terrakok.modo.android.AppScreen
import com.github.terrakok.modo.android.ExternalScreen
import com.github.terrakok.modo.android.MultiAppScreen
import com.github.terrakok.modo.androidApp.fragment.CommandsFragment
import com.github.terrakok.modo.androidApp.fragment.StartFragment
import com.github.terrakok.modo.androidApp.fragment.TabFragment

object Screens {
    val Start = AppScreen("Start") {
        StartFragment()
    }

    fun Commands(id: Int) = AppScreen(id.toString()) {
        CommandsFragment.create(id)
    }

    fun Tab(tabId: Int, id: Int) = AppScreen("${tabId}:$id") {
        TabFragment.create(tabId, id)
    }

    fun MultiStack() = MultiAppScreen(
        "MultiStack",
        listOf(Tab(0, 1), Tab(1, 1), Tab(2, 1)),
        1
    )

    fun Browser(url: String) = ExternalScreen {
        Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }
}