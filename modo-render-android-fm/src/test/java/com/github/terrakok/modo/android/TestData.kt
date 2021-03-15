package com.github.terrakok.modo.android

import com.github.terrakok.modo.Screen

private fun AppScreen(id: String) = object : Screen {
    override val id = id
}

val A = AppScreen("A")
val B = AppScreen("B")
val C = AppScreen("C")
val D = AppScreen("D")
val E = AppScreen("E")
