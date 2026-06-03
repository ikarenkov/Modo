package com.github.terrakok.modo

import android.os.Parcel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.parcelize.Parcelize
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Regression for the deprecated `navigationStateFlow()` migration shim: it must emit only
 * when THIS container's state changes (matching the pre-refactor `snapshotFlow { navigationState }`
 * semantics), not on every descendant dispatch. The previous implementation delegated to
 * [subtreeFlow] which re-emits on any nested change — a silent semantic regression for
 * pre-compiled callers linked against the shim.
 */
@Suppress("DEPRECATION_ERROR")
class NavigationStateFlowShimTest {

    @Test
    fun `dispatch on nested container - root shim does NOT emit`() = runTest {
        val nested = ShimFakeContainer(ShimState())
        val root = ShimFakeContainer(ShimState(listOf(nested)))
        val emissions = collectInBackground(root)
        val initialSize = emissions.size

        nested.dispatch { it.copy(children = it.children + MockScreen()) }
        runCurrent()

        assertEquals(initialSize, emissions.size, "shim must not re-emit on nested dispatch")
    }

    @Test
    fun `dispatch on root - root shim emits exactly once`() = runTest {
        val root = ShimFakeContainer(ShimState())
        val emissions = collectInBackground(root)
        val initialSize = emissions.size

        root.dispatch { it.copy(children = it.children + MockScreen()) }
        runCurrent()

        assertEquals(initialSize + 1, emissions.size, "shim must emit on this container's own dispatch")
    }
}

@Suppress("DEPRECATION_ERROR")
private fun TestScope.collectInBackground(container: ShimFakeContainer): MutableList<NavigationState> {
    val emissions = mutableListOf<NavigationState>()
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
        (container as NavigationContainer<ShimState>).navigationStateFlow().collect { emissions += it }
    }
    runCurrent()
    return emissions
}

@Parcelize
private data class ShimState(
    val children: List<Screen> = emptyList()
) : NavigationState {
    override fun getChildScreens(): List<Screen> = children
}

private class ShimFakeContainer(
    initialState: ShimState,
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen, NavigationContainer<ShimState> {

    private val navModel = NavModel(initialState, screenKey)

    override val stateFlow: StateFlow<ShimState> = navModel.stateFlow

    override fun dispatch(reducer: NavigationReducer<ShimState>) = navModel.dispatch(reducer)

    @Composable
    override fun Content(modifier: Modifier) = Unit

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) = Unit
}
