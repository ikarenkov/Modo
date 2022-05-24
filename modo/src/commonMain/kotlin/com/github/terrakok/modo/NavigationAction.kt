package com.github.terrakok.modo

class SetState(val state: NavigationState) : NavigationAction
class Forward(val screen: Screen, vararg val screens: Screen) : NavigationAction
class Replace(val screen: Screen, vararg val screens: Screen) : NavigationAction
class NewStack(val screen: Screen, vararg val screens: Screen) : NavigationAction
class BackTo(val screenId: String) : NavigationAction
object BackToRoot : NavigationAction
object Back : NavigationAction
object Exit : NavigationAction

fun NavigationDispatcher.setState(state: NavigationState) = dispatch(SetState(state))
fun NavigationDispatcher.forward(screen: Screen, vararg screens: Screen) = dispatch(Forward(screen, *screens))
fun NavigationDispatcher.replace(screen: Screen, vararg screens: Screen) = dispatch(Replace(screen, *screens))
fun NavigationDispatcher.newStack(screen: Screen, vararg screens: Screen) = dispatch(NewStack(screen, *screens))
fun NavigationDispatcher.backTo(screenId: String) = dispatch(BackTo(screenId))
fun NavigationDispatcher.backToRoot() = dispatch(BackToRoot)
fun NavigationDispatcher.back() = dispatch(Back)
fun NavigationDispatcher.exit() = dispatch(Exit)
