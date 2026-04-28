package com.github.terrakok.modo

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.parcelize.Parcelize
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("DEPRECATION")
class ComposeRendererDisposalTest {

    @Test
    fun `When ComposeRenderer is created - Then scope is active`() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        try {
            val renderer = createTestRenderer()
            assertTrue(renderer.scope.isActive)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `When ComposeRenderer dispose is called - Then scope is cancelled`() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        try {
            val renderer = createTestRenderer()
            assertTrue(renderer.scope.isActive, "Scope should be active after creation")

            renderer.dispose()

            assertFalse(renderer.scope.isActive, "Scope should be inactive after dispose")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `When container screen is removed from tree - Then renderer can be disposed`() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        try {
            val renderer = createTestRenderer()

            // Simulate removing container screen and calling dispose
            assertTrue(renderer.scope.isActive)
            renderer.dispose()
            assertFalse(renderer.scope.isActive)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun createTestRenderer(): ComposeRenderer<MockNavigationState> {
        val state = MockNavigationState()
        val navModel: NavModel<MockNavigationState> = NavModel(state)
        @Suppress("UNCHECKED_CAST")
        val containerScreen = object : ContainerScreen<MockNavigationState>(navModel),
            Parcelable {
            @Composable
            override fun Content(modifier: Modifier) = Unit

            override fun describeContents(): Int = 0

            override fun writeToParcel(parcel: Parcel, flags: Int) {}
        }
        return ComposeRenderer(containerScreen, navModel.stateFlow)
    }

    @Parcelize
    private class MockNavigationState : NavigationState {
        override fun getChildScreens(): List<Screen> = emptyList()
    }
}
