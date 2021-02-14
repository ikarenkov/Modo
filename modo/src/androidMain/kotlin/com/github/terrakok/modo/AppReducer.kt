package com.github.terrakok.modo

import android.content.Context
import android.content.Intent

class ExternalScreen(
    val createIntent: () -> Intent
) : Screen {
    override val id: String = "external_screen"
}

/**
 * Action for launching external activities.
 */
class Launch(val screen: ExternalScreen): NavigationAction
fun Modo.launch(screen: ExternalScreen) = dispatch(Launch(screen))

/**
 * Navigation reducer for building single activity application.
 */
class AppReducer(
    private val context: Context,
    private val origin: NavigationReducer = ModoReducer()
) : NavigationReducer {
    override fun invoke(action: NavigationAction, state: NavigationState): NavigationState =
        if (action is Launch) {
            val intent = action.screen.createIntent().apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            state
        } else {
            origin.invoke(action, state)
        }
}