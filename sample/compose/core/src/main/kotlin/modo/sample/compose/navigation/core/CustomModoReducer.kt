package modo.sample.compose.navigation.core

import com.github.terrakok.modo.ModoReducer
import com.github.terrakok.modo.NavigationAction
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.NavigationState

class CustomModoReducer(
    private val origin: NavigationReducer = ModoReducer()
) : NavigationReducer {

    override fun invoke(action: NavigationAction, state: NavigationState): NavigationState = when (action) {
        is RemovePrev -> {
            val screens = state.chain
            if (screens.size > 1) {
                state.copy(chain = screens.mapIndexedNotNull { index, screen -> if (index == screens.lastIndex - 1) null else screen })
            } else {
                state
            }
        }
        else -> origin(action, state)
    }

}

class RemovePrev : NavigationAction