package com.github.terrakok.modo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.parcelize.Parcelize
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Verifies that [NavModel.dispatch] is atomic under concurrent invocation.
 *
 * The fix replaces a non-atomic `_navigationState.value = reducer.reduce(_navigationState.value)`
 * read-modify-write with `_navigationState.update { ... }`, which performs a CAS loop and is
 * safe under contention. Without the fix, concurrent appenders lose updates and the final stack
 * size is smaller than the number of dispatches.
 */
class NavModelConcurrencyTest {

    @Test
    fun `concurrent dispatch on Dispatchers Default - no updates are lost`() = runBlocking {
        val n = 5_000
        val navModel = NavModel<StackState>(StackState())

        val jobs = List(n) {
            async(Dispatchers.Default) {
                navModel.dispatch { state -> state.copy(screens = state.screens + MockScreen()) }
            }
        }
        jobs.awaitAll()

        assertEquals(n, navModel.stateFlow.value.screens.size)
    }
}

@Parcelize
private data class StackState(
    val screens: List<Screen> = emptyList()
) : NavigationState {
    override fun getChildScreens(): List<Screen> = screens
}
