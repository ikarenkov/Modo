package com.github.terrakok.modo

/**
 * Marker for actions which will be applied to state via reducer
 */
interface NavigationAction

class SetState(val state: NavigationState) : NavigationAction
class Forward(val screen: Screen, vararg val screens: Screen) : NavigationAction
class Replace(val screen: Screen, vararg val screens: Screen) : NavigationAction
class NewStack(val screen: Screen, vararg val screens: Screen) : NavigationAction
class BackTo(val screenId: String) : NavigationAction
object BackToRoot : NavigationAction
object Back : NavigationAction
object Exit : NavigationAction

fun ModoDispatcher.forward(screen: Screen, vararg screens: Screen) = dispatch(Forward(screen, *screens))
fun ModoDispatcher.replace(screen: Screen, vararg screens: Screen) = dispatch(Replace(screen, *screens))
fun ModoDispatcher.newStack(screen: Screen, vararg screens: Screen) = dispatch(NewStack(screen, *screens))
fun ModoDispatcher.backTo(screenId: String) = dispatch(BackTo(screenId))
fun ModoDispatcher.backToRoot() = dispatch(BackToRoot)
fun ModoDispatcher.back() = dispatch(Back)
fun ModoDispatcher.exit() = dispatch(Exit)
