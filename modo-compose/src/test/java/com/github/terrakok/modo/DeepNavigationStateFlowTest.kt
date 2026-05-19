package com.github.terrakok.modo

import android.os.Parcel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.parcelize.Parcelize
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DeepNavigationStateFlowTest {

    @Test
    fun `subtreeStateFlow emits initial state on subscription`() = runTest {
        val container = FakeContainer(NestedState())

        val first = container.subtreeStateFlow(backgroundScope).first()

        assertEquals(NestedState(), first)
    }

    @Test
    fun `direct dispatch on root - emits new state`() = runTest {
        val container = FakeContainer(NestedState())
        val emissions = collectInBackground(container)

        val newScreen = MockScreen()
        container.dispatch { it.copy(children = it.children + newScreen) }
        runCurrent()

        val expected: List<NavigationState> = listOf(NestedState(), NestedState(listOf(newScreen)))
        assertEquals(expected, emissions)
    }

    @Test
    fun `nested container dispatch - root re-emits its state`() = runTest {
        val nested = FakeContainer(NestedState())
        val rootInitial = NestedState(listOf(nested))
        val root = FakeContainer(rootInitial)
        val emissions = collectInBackground(root)

        val deepScreen = MockScreen()
        nested.dispatch { it.copy(children = it.children + deepScreen) }
        runCurrent()

        // Root emits twice: initial + re-emit on nested change. Both equal rootInitial
        // because the root state itself didn't change — observers re-walk getChildScreens().
        assertEquals(2, emissions.size)
        assertEquals(rootInitial, emissions[0])
        assertEquals(rootInitial, emissions[1])
        // And the nested container itself reflects the change
        assertEquals(
            NestedState(listOf(deepScreen)),
            nested.stateFlow.value
        )
    }

    @Test
    fun `two-level nested dispatch - root re-emits`() = runTest {
        val grandchild = FakeContainer(NestedState())
        val child = FakeContainer(NestedState(listOf(grandchild)))
        val root = FakeContainer(NestedState(listOf(child)))
        val emissions = collectInBackground(root)

        val initialSize = emissions.size

        grandchild.dispatch { it.copy(children = it.children + MockScreen()) }
        runCurrent()

        assertEquals(initialSize + 1, emissions.size)
    }

    @Test
    fun `removed child container - dispatching on orphan does not emit on root`() = runTest {
        val nested = FakeContainer(NestedState())
        val root = FakeContainer(NestedState(listOf(nested)))
        val emissions = collectInBackground(root)

        // Remove the nested container from the root.
        root.dispatch { NestedState(emptyList()) }
        runCurrent()
        val sizeAfterRemoval = emissions.size

        // Now dispatch on the orphaned container. The root must NOT emit, because
        // flatMapLatest dropped the inner subscription when root's state changed.
        nested.dispatch { it.copy(children = it.children + MockScreen()) }
        runCurrent()

        assertEquals(sizeAfterRemoval, emissions.size)
    }

    @Test
    fun `newly added child container - dispatch on it emits on root`() = runTest {
        val root = FakeContainer(NestedState())
        val emissions = collectInBackground(root)

        // Add a fresh nested container after subscription started.
        val nested = FakeContainer(NestedState())
        root.dispatch { it.copy(children = listOf(nested)) }
        runCurrent()
        val sizeAfterAdd = emissions.size

        // Dispatch on the new child — must propagate.
        nested.dispatch { it.copy(children = it.children + MockScreen()) }
        runCurrent()

        assertEquals(sizeAfterAdd + 1, emissions.size)
    }

    @Test
    fun `empty children then populated later - deep observation works through transition`() = runTest {
        // Root starts with zero children. The inner flow inside flatMapLatest emits the initial
        // state and then completes (empty merge). flatMapLatest is still subscribed to the
        // outer StateFlow, so when children appear later, a fresh inner flow walks the tree.
        val root = FakeContainer(NestedState())
        val emissions = collectInBackground(root)
        assertEquals(1, emissions.size, "initial emission only")

        // Add a nested container.
        val nested = FakeContainer(NestedState())
        root.dispatch { it.copy(children = listOf(nested)) }
        runCurrent()
        assertEquals(2, emissions.size, "root re-emits when children grow from empty to one")

        // Add a grandchild under the freshly-attached nested. Deep change must propagate
        // because the outer flatMapLatest restart wired up `nested`'s flow.
        val grandchild = FakeContainer(NestedState())
        nested.dispatch { it.copy(children = listOf(grandchild)) }
        runCurrent()
        assertEquals(3, emissions.size, "nested change propagates to root")

        // Dispatch at the deepest level — must propagate through both hops.
        grandchild.dispatch { it.copy(children = it.children + MockScreen()) }
        runCurrent()
        assertEquals(4, emissions.size, "grandchild change propagates two levels up")
    }

    @Test
    fun `swap one child for another - only the live child propagates`() = runTest {
        val oldChild = FakeContainer(NestedState())
        val newChild = FakeContainer(NestedState())
        val root = FakeContainer(NestedState(listOf(oldChild)))
        val emissions = collectInBackground(root)

        // Replace the child.
        root.dispatch { it.copy(children = listOf(newChild)) }
        runCurrent()
        val sizeAfterSwap = emissions.size

        // Dispatch on the old (orphaned) child — no propagation.
        oldChild.dispatch { it.copy(children = it.children + MockScreen()) }
        runCurrent()
        assertEquals(sizeAfterSwap, emissions.size)

        // Dispatch on the new child — propagates.
        newChild.dispatch { it.copy(children = it.children + MockScreen()) }
        runCurrent()
        assertEquals(sizeAfterSwap + 1, emissions.size)
    }

}

private fun TestScope.collectInBackground(container: FakeContainer): MutableList<NavigationState> {
    val emissions = mutableListOf<NavigationState>()
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
        container.subtreeStateFlow(backgroundScope).collect { emissions += it }
    }
    runCurrent()
    return emissions
}

@Parcelize
private data class NestedState(
    val children: List<Screen> = emptyList()
) : NavigationState {
    override fun getChildScreens(): List<Screen> = children
}

/**
 * Test double: both a [Screen] (so it can live inside another container's state) and a
 * [NavigationContainer]. Bypasses ContainerScreen/ComposeRenderer so we don't need a Main dispatcher.
 */
private class FakeContainer(
    initialState: NestedState,
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen, NavigationContainer<NestedState> {

    private val navModel = NavModel(initialState, screenKey)

    override val stateFlow: StateFlow<NestedState> = navModel.stateFlow

    override fun dispatch(reducer: NavigationReducer<NestedState>) = navModel.dispatch(reducer)

    @Composable
    override fun Content(modifier: Modifier) = Unit

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) = Unit
}
