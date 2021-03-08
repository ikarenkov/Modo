package com.github.terrakok.modo

/**
 * Marker for actions which will be applied to state via reducer
 */
interface NavigationAction

class Forward(val screen: Screen, vararg val screens: Screen) : NavigationAction
class Replace(val screen: Screen, vararg val screens: Screen) : NavigationAction
class NewStack(val screen: Screen, vararg val screens: Screen) : NavigationAction
class BackTo(val screenId: String) : NavigationAction
object BackToRoot : NavigationAction
object Back : NavigationAction
object Exit : NavigationAction

fun Modo.forward(screen: Screen, vararg screens: Screen) = dispatch(Forward(screen, *screens))
fun Modo.replace(screen: Screen, vararg screens: Screen) = dispatch(Replace(screen, *screens))
fun Modo.newStack(screen: Screen, vararg screens: Screen) = dispatch(NewStack(screen, *screens))
fun Modo.backTo(screenId: String) = dispatch(BackTo(screenId))
fun Modo.backToRoot() = dispatch(BackToRoot)
fun Modo.back() = dispatch(Back)
fun Modo.exit() = dispatch(Exit)
