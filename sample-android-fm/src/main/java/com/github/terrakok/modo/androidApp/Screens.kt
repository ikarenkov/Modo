package com.github.terrakok.modo.androidApp

import android.content.Intent
import android.net.Uri
import com.github.terrakok.modo.android.AppScreen
import com.github.terrakok.modo.android.ExternalScreen
import com.github.terrakok.modo.android.FlowAppScreen
import com.github.terrakok.modo.android.MultiAppScreen
import com.github.terrakok.modo.androidApp.fragment.CommandsFragment
import com.github.terrakok.modo.androidApp.fragment.StartFragment
import com.github.terrakok.modo.androidApp.fragment.TabFragment
import kotlinx.parcelize.Parcelize

object Screens {
    @Parcelize
    class Start : AppScreen("Start") {
        override fun create() = StartFragment()
    }

    @Parcelize
    class Commands(private val i: Int) : AppScreen("c_$i") {
        override fun create() = CommandsFragment.create(i)
    }

    @Parcelize
    class Tab(private val tabId: Int, private val i: Int) : AppScreen("t_$i") {
        override fun create() = TabFragment.create(tabId, i)
    }

    fun MultiStack() = MultiAppScreen(
        "MultiStack",
        listOf(Tab(0, 1), Tab(1, 1), Tab(2, 1)),
        1
    )

    fun FlowScreen() = FlowAppScreen(
        "Flow",
        Start()
    )

    fun Browser(url: String) = ExternalScreen {
        Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }
}