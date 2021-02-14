package com.github.terrakok.modo

/**
 * Marker for actions which will be applied to state via reducer
 */
interface NavigationAction

object Back : NavigationAction
class Forward(vararg val screen: Screen) : NavigationAction
class Replace(vararg val screen: Screen) : NavigationAction
class NewStack(vararg val screen: Screen) : NavigationAction
class BackTo(val screenId: String?) : NavigationAction

fun Modo.back() = dispatch(Back)
fun Modo.forward(vararg screen: Screen) = dispatch(Forward(*screen))
fun Modo.replace(vararg screen: Screen) = dispatch(Replace(*screen))
fun Modo.newStack(vararg screen: Screen) = dispatch(NewStack(*screen))
fun Modo.backTo(screenId: String) = dispatch(BackTo(screenId))
fun Modo.exit() = dispatch(BackTo(null))
