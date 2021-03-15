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
class Launch(val screen: ExternalScreen): NavigationAction
fun Modo.launch(screen: ExternalScreen) = dispatch(Launch(screen))

/**
 * Action for restore after death.
 */
internal data class Restore(val state: NavigationState): NavigationAction
internal fun Modo.restore(state: NavigationState) = dispatch(Restore(state))

/**
 * Navigation reducer for building single activity application.
 */
class AppReducer(
    private val context: Context,
    private val origin: NavigationReducer = ModoReducer()
) : NavigationReducer {
    override fun invoke(action: NavigationAction, state: NavigationState): NavigationState =
        when (action) {
            is Restore -> {
                action.state
            }
            is Launch -> {
                val intent = action.screen.createIntent().apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                state
            }
            else -> {
                origin.invoke(action, state)
            }
        }
}