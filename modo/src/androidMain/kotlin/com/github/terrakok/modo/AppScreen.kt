package com.github.terrakok.modo

import androidx.fragment.app.Fragment

interface AppScreen : Screen {
    fun create(): Fragment
}

fun AppScreen(
    id: String,
    create: () -> Fragment
) = object : AppScreen {
    override val id = id
    override fun create() = create()
    override fun toString() = "AppScreen[$id]"
}
