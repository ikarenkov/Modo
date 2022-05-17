package com.github.terrakok.modo.android.compose

import android.content.Context
import android.content.Intent
import com.github.terrakok.modo.*


/**
 * Screen for opening external activity via Intent.
 */
class ExternalScreen(
    val createIntent: () -> Intent
)

/**
 * Action for launching external activities.
 */
class Launch(val screen: ExternalScreen) : NavigationAction

fun ModoDispatcher.launch(screen: ExternalScreen) = dispatch(Launch(screen))

/**
 * Navigation reducer for building single activity application.
 */
class AppReducer(
    private val context: Context,
    private val origin: NavigationReducer = ModoReducer()
) : NavigationReducer {

    override fun invoke(action: NavigationAction, state: NavigationState): NavigationState {
        if (action is NestedAction) {
            reduceInternal(action.lastAction(), state)?.let { return it }
        }
        return reduceInternal(action, state) ?: origin.invoke(action, state)
    }

    private fun reduceInternal(action: NavigationAction, state: NavigationState): NavigationState? = when (action) {
        is Launch -> {
            val intent = action.screen.createIntent().apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            state
        }
        else -> null
    }

    private fun NestedAction.lastAction(): NavigationAction = (navigationAction as? NestedAction)?.lastAction() ?: navigationAction

}