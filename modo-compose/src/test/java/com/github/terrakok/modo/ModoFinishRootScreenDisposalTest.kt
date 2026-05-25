package com.github.terrakok.modo

import android.os.Parcel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.lifecycle.LifecycleDependency
import com.github.terrakok.modo.model.ScreenModelStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.parcelize.Parcelize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Drives [Modo.onRootScreenFinished] through a 3-level container tree and asserts the unified-cleanup
 * contract: every renderer scope is cancelled, every screen's [LifecycleDependency.onPreDispose] is
 * dispatched, and [ModoDevOptions.onScreenDisposeListener] fires for every screen in the subtree.
 */
@Suppress("DEPRECATION")
class ModoFinishRootScreenDisposalTest {

    private var originalDisposeListener: ((Screen) -> Unit)? = null

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        Modo.rootScreens.clear()
        screenCounterKey.set(-1)
        ScreenModelStore.removedScreenKeys.clear()
        ScreenModelStore.screenModels.clear()
        ScreenModelStore.dependencies.clear()
        ScreenModelStore.dependencyCounter.set(0L)
        ScreenModelStore.lastScreenModelKey.value = null
        ModoDevOptions.onIllegalScreenModelStoreAccess = ModoDevOptions.ValidationFailedStrategy { }
        ModoDevOptions.onIllegalClearState = ModoDevOptions.ValidationFailedStrategy { }
        originalDisposeListener = ModoDevOptions.onScreenDisposeListener
    }

    @AfterEach
    fun tearDown() {
        ModoDevOptions.onScreenDisposeListener = originalDisposeListener
        Dispatchers.resetMain()
    }

    @Test
    fun `When finishRootScreen called - Then every nested renderer scope is cancelled`() {
        val tree = build3LevelTree()
        assertTrue(tree.root.renderer.scope.isActive, "root scope should start active")
        assertTrue(tree.level2.renderer.scope.isActive, "level2 scope should start active")
        assertTrue(tree.level3.renderer.scope.isActive, "level3 scope should start active")

        Modo.onRootScreenFinished(tree.root)

        assertFalse(tree.root.renderer.scope.isActive, "root scope should be cancelled")
        assertFalse(tree.level2.renderer.scope.isActive, "level2 scope should be cancelled")
        assertFalse(tree.level3.renderer.scope.isActive, "level3 scope should be cancelled")
    }

    @Test
    fun `When finishRootScreen called - Then onScreenDisposeListener fires for every screen in the tree`() {
        val tree = build3LevelTree()
        val disposed = mutableListOf<Screen>()
        ModoDevOptions.onScreenDisposeListener = { disposed += it }

        Modo.onRootScreenFinished(tree.root)

        assertEquals(listOf(tree.root, tree.level2, tree.level3, tree.leaf), disposed)
    }

    @Test
    fun `When finishRootScreen called - Then LifecycleDependency onPreDispose fires for every screen in the tree`() {
        val tree = build3LevelTree()
        val preDisposed = mutableListOf<Screen>()
        listOf(tree.root, tree.level2, tree.level3, tree.leaf).forEach { screen ->
            ScreenModelStore.getOrPutDependency<RecordingLifecycleDependency>(
                screen = screen,
                name = LifecycleDependency.KEY,
                factory = { RecordingLifecycleDependency(screen, preDisposed) }
            )
        }

        Modo.onRootScreenFinished(tree.root)

        assertEquals(listOf(tree.root, tree.level2, tree.level3, tree.leaf), preDisposed)
    }

    private fun build3LevelTree(): Tree {
        val leaf = MockScreen(ScreenKey("leaf"))
        val level3 = TestContainerScreen(TestNavigationState(listOf(leaf)), ScreenKey("l3"))
        val level2 = TestContainerScreen(TestNavigationState(listOf(level3)), ScreenKey("l2"))
        val root = RootScreen(level2)
        Modo.rootScreens[root.screenKey] = root
        return Tree(root, level2, level3, leaf)
    }

    private data class Tree(
        val root: RootScreen<TestContainerScreen>,
        val level2: TestContainerScreen,
        val level3: TestContainerScreen,
        val leaf: Screen,
    )

    @Parcelize
    private class TestNavigationState(val children: List<Screen>) : NavigationState {
        override fun getChildScreens(): List<Screen> = children
    }

    private class TestContainerScreen(
        state: TestNavigationState,
        screenKey: ScreenKey,
    ) : ContainerScreen<TestNavigationState>(NavModel(state, screenKey)) {

        @Composable
        override fun Content(modifier: Modifier) = Unit

        override fun describeContents(): Int = 0
        override fun writeToParcel(parcel: Parcel, flags: Int) = Unit
    }

    private class RecordingLifecycleDependency(
        private val screen: Screen,
        private val tracker: MutableList<Screen>,
    ) : LifecycleDependency {
        override fun showTransitionFinished() = Unit
        override fun hideTransitionStarted() = Unit
        override fun onPreDispose() {
            tracker += screen
        }
    }
}
