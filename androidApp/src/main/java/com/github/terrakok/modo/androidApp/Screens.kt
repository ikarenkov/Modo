package com.github.terrakok.modo.androidApp

import com.github.terrakok.modo.AppScreen

object Screens {
    fun Sample(id: Int) = AppScreen(id.toString()) {
        SampleFragment.create(id)
    }
}