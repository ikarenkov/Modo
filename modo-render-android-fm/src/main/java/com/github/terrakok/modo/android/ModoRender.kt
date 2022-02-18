package com.github.terrakok.modo.android

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.fragment.app.FragmentTransaction
import com.github.terrakok.modo.*
import com.github.terrakok.modo.android.multi.FragmentMultiScreen
import com.github.terrakok.modo.android.multi.MultiStackFragmentImpl

interface StackAction
data class Pop(val count: Int) : StackAction
data class Push(val screens: List<Screen>) : StackAction

/**
 * Navigation state render based on FragmentManager
 */
open class ModoRender(
    protected val fragmentManager: FragmentManager,
    protected val containerId: Int,
    protected val exitAction: () -> Unit
) : NavigationRender {
    internal var currentState: NavigationState
        private set

    constructor(
        activity: FragmentActivity,
        containerId: Int
    ) : this(activity.supportFragmentManager, containerId, { activity.finish() })

    init {
        val restoredScreens = mutableListOf<Screen>()
        for (i in 0 until fragmentManager.backStackEntryCount) {
            fragmentManager.getBackStackEntryAt(i).name?.let { restoredScreens.add(RestoredScreen(it)) }
        }
        currentState = NavigationState(restoredScreens)
    }

    override fun invoke(state: NavigationState) {
        val diff = diff(currentState, state)
        currentState = state
        if (diff.isNotEmpty()) {
            if (currentState.chain.isEmpty()) {
                exitAction()
            } else {
                diff.forEach { action ->
                    when (action) {
                        is Pop -> pop(action.count)
                        is Push -> push(action.screens)
                    }
                }
            }
        }
        val currentScreen = currentState.chain.lastOrNull()
        if (currentScreen is AbstractMultiScreen) {
            fragmentManager.executePendingTransactions()
            (fragmentManager.findFragmentById(containerId) as MultiStackFragment)
                .applyMultiState(currentScreen.multiScreenState)
        }
    }

    protected open fun pop(count: Int) {
        val entryIndex = fragmentManager.backStackEntryCount - count
        val entryName =
            if (entryIndex in 0 until fragmentManager.backStackEntryCount) {
                fragmentManager.getBackStackEntryAt(entryIndex).name
            } else {
                null
            }
        fragmentManager.popBackStack(entryName, POP_BACK_STACK_INCLUSIVE)
    }

    protected open fun push(screens: List<Screen>) {
        screens.forEach { screen ->
            if (screen is AppScreen) {
                fragmentManager.beginTransaction().apply {
                    setReorderingAllowed(true)
                    val fragment = screen.create()
                    setupTransaction(fragmentManager, this, screen, fragment)
                    if (screen.replacePreviousScreen) {
                        replace(containerId, fragment, screen.id)
                    } else {
                        add(containerId, fragment, screen.id)
                    }
                    addToBackStack(screen.id)
                }.commit()
            } else if (screen is AbstractMultiScreen) {
                require(screen is FragmentMultiScreen) {"ModoRender for multiscreen works with FragmentMultiScreen only! Received $screen"}
                pushMultiStackFragment(screen)
            } else {
                error("ModoRender works with AppScreens only! Received $screen")
            }
        }
    }

    protected open fun setupTransaction(
        fragmentManager: FragmentManager,
        transaction: FragmentTransaction,
        screen: AppScreen,
        newFragment: Fragment
    ) {
    }

    protected open fun pushMultiStackFragment(multiScreen: FragmentMultiScreen) {
        val fragment = createMultiStackFragment(multiScreen.multiScreenState)
        fragmentManager.beginTransaction().apply {
            setReorderingAllowed(true)
            replace(containerId, fragment, multiScreen.id)
            addToBackStack(multiScreen.id)
        }.commit()
        fragmentManager.executePendingTransactions()
    }

    protected open fun createMultiStackFragment(multiScreenState: MultiScreenState): MultiStackFragment = MultiStackFragmentImpl()

    private class RestoredScreen(override val id: String) : Screen {
        override fun toString() = "[$id]"
    }

    internal companion object {
        fun diff(prev: NavigationState, next: NavigationState): List<StackAction> = when {
            prev.chain.isEmpty() && next.chain.isEmpty() -> emptyList()
            prev.chain.isEmpty() -> listOf(Push(next.chain))
            next.chain.isEmpty() -> listOf(Pop(prev.chain.size))
            else -> {
                var result: List<StackAction>? = null
                for (i in 0 until maxOf(prev.chain.size, next.chain.size)) {
                    val p = prev.chain.getOrNull(i)?.id
                    val n = next.chain.getOrNull(i)?.id
                    if (p == n) continue
                    result = when {
                        p == null -> listOf(
                            Push(next.chain.subList(i, next.chain.size))
                        )
                        n == null -> listOf(
                            Pop(prev.chain.size - i)
                        )
                        else -> listOf(
                            Pop(prev.chain.size - i),
                            Push(next.chain.subList(i, next.chain.size))
                        )
                    }
                    break
                }
                result ?: emptyList()
            }
        }
    }
}