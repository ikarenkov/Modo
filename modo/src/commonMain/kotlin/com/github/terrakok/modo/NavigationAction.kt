package com.github.terrakok.modo

interface NavigationAction
object Back : NavigationAction
class Forward(vararg val screen: Screen) : NavigationAction
class Replace(vararg val screen: Screen) : NavigationAction
class NewStack(vararg val screen: Screen) : NavigationAction
class BackTo(val screen: Screen?) : NavigationAction

fun Modo.back() = dispatch(Back)
fun Modo.forward(vararg screen: Screen) = dispatch(Forward(*screen))
fun Modo.replace(vararg screen: Screen) = dispatch(Replace(*screen))
fun Modo.newStack(vararg screen: Screen) = dispatch(NewStack(*screen))
fun Modo.backTo(screen: Screen) = dispatch(BackTo(screen))
fun Modo.exit() = dispatch(BackTo(null))
