package com.github.terrakok.modo.android

import androidx.lifecycle.Lifecycle.State.CREATED
import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.Lifecycle.State.STARTED
import com.github.terrakok.modo.MockScreen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.android.ModoScreenAndroidAdapterTestUtils.cleanupArchTaskExecutor
import com.github.terrakok.modo.android.ModoScreenAndroidAdapterTestUtils.cleanupScreenModelStore
import com.github.terrakok.modo.android.ModoScreenAndroidAdapterTestUtils.setupArchTaskExecutor
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ModoScreenAndroidAdapterParentPropagationTest {

    private lateinit var screen: MockScreen
    private lateinit var adapter: ModoScreenAndroidAdapter
    private lateinit var parent: TestLifecycleOwner
    private lateinit var emulator: CompositionLifecycleEmulator

    @BeforeEach
    fun setup() {
        setupArchTaskExecutor()
        cleanupScreenModelStore()
        screen = MockScreen(ScreenKey("test-screen"))
        adapter = ModoScreenAndroidAdapter.get(screen)
        parent = TestLifecycleOwner()
        emulator = CompositionLifecycleEmulator(adapter, parent)
    }

    @AfterEach
    fun tearDown() {
        cleanupArchTaskExecutor()
        cleanupScreenModelStore()
    }

    // region Parent RESUMED, screen enter composition

    @Test
    fun `Given parent RESUMED and auto resume When enter composition - Then screen RESUMED`() {
        parent.lifecycleState = RESUMED
        emulator.enterComposition()

        assertEquals(RESUMED, emulator.lifecycleState)
    }

    @Test
    fun `Given parent RESUMED and manual resume When enter composition - Then screen STARTED`() {
        parent.lifecycleState = RESUMED
        emulator.enterComposition(usesTransitionLifecycle = true)

        assertEquals(STARTED, emulator.lifecycleState)
    }

    // endregion

    // region Parent STARTED (animation in progress), screen enter composition

    @Test
    fun `Given parent STARTED and auto resume When enter composition without manual resume - Then screen STARTED`() {
        parent.lifecycleState = STARTED

        emulator.enterComposition()
        assertEquals(STARTED, emulator.lifecycleState)
    }

    @Test
    fun `Given parent STARTED and manual resume When enter composition without manual resume - Then screen RESUMED`() {
        parent.lifecycleState = STARTED

        emulator.enterComposition(usesTransitionLifecycle = true)
        assertEquals(STARTED, emulator.lifecycleState)
    }

    // endregion

    // region lifecycle propagation

    // region Screen is not in transition, parent changes state

    @Test
    fun `Given auto resume When parent moves RESUMED to STARTED to RESUMED - Then adapter follows parent`() {
        parent.lifecycleState = RESUMED

        emulator.enterComposition()
        assertEquals(RESUMED, emulator.lifecycleState)

        parent.lifecycleState = STARTED
        assertEquals(STARTED, emulator.lifecycleState)

        parent.lifecycleState = RESUMED
        assertEquals(RESUMED, emulator.lifecycleState)
    }

    @Test
    fun `Given manual resume When parent moves RESUMED to STARTED to RESUMED - Then adapter follows parent`() {
        parent.lifecycleState = RESUMED

        emulator.enterComposition(usesTransitionLifecycle = true)
        assertEquals(STARTED, emulator.lifecycleState)

        emulator.showTransitionFinished()
        assertEquals(RESUMED, emulator.lifecycleState)

        parent.lifecycleState = STARTED
        assertEquals(STARTED, emulator.lifecycleState)

        parent.lifecycleState = RESUMED
        assertEquals(RESUMED, emulator.lifecycleState)
    }

    @Test
    fun `Given auto resume Test parent moves from RESUMED to CREATED to RESUMED`() {
        parent.lifecycleState = RESUMED

        emulator.enterComposition()
        assertEquals(RESUMED, emulator.lifecycleState)

        parent.lifecycleState = CREATED
        assertEquals(CREATED, emulator.lifecycleState)

        parent.lifecycleState = RESUMED
        assertEquals(RESUMED, emulator.lifecycleState)
    }

    // endregion

    @Test
    fun `Given manual resume and parent animating Verify states When transition finished after parent moves to RESUMED`() {
        parent.lifecycleState = STARTED

        emulator.enterComposition(usesTransitionLifecycle = true)
        assertEquals(STARTED, emulator.lifecycleState)

        parent.lifecycleState = RESUMED
        assertEquals(STARTED, emulator.lifecycleState)

        adapter.showTransitionFinished()
        assertEquals(RESUMED, emulator.lifecycleState)
    }

    @Test
    fun `Given manual resume and STARTED parent Test transition finished before parent moves to RESUMED`() {
        parent.lifecycleState = STARTED

        emulator.enterComposition(usesTransitionLifecycle = true)
        assertEquals(STARTED, emulator.lifecycleState)

        adapter.showTransitionFinished()
        assertEquals(STARTED, emulator.lifecycleState)

        parent.lifecycleState = RESUMED
        assertEquals(RESUMED, emulator.lifecycleState)
    }

    // endregion

    // Parent resumed all time
    // Child animating in and animating out
    @Test
    fun `Given manual resume parent RESUMED Test screen transition showing and hiding`() {
        parent.lifecycleState = RESUMED

        emulator.enterComposition(usesTransitionLifecycle = true)
        assertEquals(STARTED, emulator.lifecycleState)

        adapter.showTransitionFinished()
        assertEquals(RESUMED, emulator.lifecycleState)

        adapter.hideTransitionStarted()
        assertEquals(STARTED, emulator.lifecycleState)

        emulator.exitComposition()
        assertEquals(CREATED, emulator.lifecycleState)
    }

    // Parent animating (it is STARTED)
    // Child idle and then animating out during parent animation
    // Parent animation finishes
    // Child animation finishes
    @Test
    fun `Given manual resume parent STARTED Test showTransitionFinished then hideTransitionStarted`() {
        parent.lifecycleState = STARTED

        emulator.enterComposition(usesTransitionLifecycle = true)
        assertEquals(STARTED, emulator.lifecycleState)

        adapter.showTransitionFinished()
        assertEquals(STARTED, emulator.lifecycleState)

        adapter.hideTransitionStarted()
        assertEquals(STARTED, emulator.lifecycleState)

        parent.lifecycleState = RESUMED
        assertEquals(STARTED, emulator.lifecycleState)

        emulator.exitComposition()
        assertEquals(CREATED, emulator.lifecycleState)
    }

    @Test
    fun `Given in composition When exit composition - Then adapter moves to CREATED`() {
        parent.lifecycleState = RESUMED

        emulator.enterComposition()
        assertEquals(RESUMED, emulator.lifecycleState)

        emulator.exitComposition()
        assertEquals(CREATED, emulator.lifecycleState)
    }

    @Test
    fun `When screen is not in composition - Then parent lifecycle changes has no effect`() {
        parent.lifecycleState = RESUMED
        assertEquals(CREATED, emulator.lifecycleState)

        parent.lifecycleState = STARTED
        assertEquals(CREATED, emulator.lifecycleState)

        parent.lifecycleState = RESUMED
        assertEquals(CREATED, emulator.lifecycleState)

        parent.lifecycleState = CREATED
        assertEquals(CREATED, emulator.lifecycleState)

        parent.lifecycleState = DESTROYED
        assertEquals(CREATED, emulator.lifecycleState)
    }

    @Test
    fun `Given parent STARTED When entering composition and finish transition - Then screen STARTED`() {
        parent.lifecycleState = STARTED
        emulator.enterComposition(usesTransitionLifecycle = true)
        assertEquals(STARTED, emulator.lifecycleState)

        emulator.showTransitionFinished()
        assertEquals(STARTED, emulator.lifecycleState)
    }

    @Test
    fun `Given parent CREATED When entering composition and finish transition - Then screen STARTED`() {
        parent.lifecycleState = STARTED
        emulator.enterComposition(usesTransitionLifecycle = true)
        assertEquals(STARTED, emulator.lifecycleState)

        emulator.showTransitionFinished()
        assertEquals(STARTED, emulator.lifecycleState)
    }

    // region Parent ON_DESTROY propagation - Activity lifecycle scenarios

    @Test
    fun `When activity finishes normally - Then screen propagates ON_DESTROY and moves to DESTROYED`() {
        parent.lifecycleState = RESUMED
        emulator.enterComposition(
            isActivityFinishing = { true },
            isChangingConfigurations = { false }
        )
        assertEquals(RESUMED, emulator.lifecycleState)

        parent.lifecycleState = DESTROYED
        assertEquals(DESTROYED, emulator.lifecycleState)
    }

    @Test
    fun `When activity killed by system but not finishing - Then screen skips ON_DESTROY and persists in CREATED`() {
        parent.lifecycleState = RESUMED
        emulator.enterComposition(
            isActivityFinishing = { false },
            isChangingConfigurations = { false }
        )
        assertEquals(RESUMED, emulator.lifecycleState)

        parent.lifecycleState = DESTROYED
        assertEquals(CREATED, emulator.lifecycleState)
    }

    @Test
    fun `When activity recreating due to config change - Then screen skips ON_DESTROY to avoid SavedStateHandle crash`() {
        parent.lifecycleState = RESUMED
        emulator.enterComposition(
            isActivityFinishing = { false },
            isChangingConfigurations = { true }
        )
        assertEquals(RESUMED, emulator.lifecycleState)

        parent.lifecycleState = DESTROYED
        assertEquals(CREATED, emulator.lifecycleState)
    }

    @Test
    fun `When activity killed by system from STARTED state - Then screen skips ON_DESTROY and stays in CREATED`() {
        parent.lifecycleState = STARTED
        emulator.enterComposition(
            isActivityFinishing = { false },
            isChangingConfigurations = { false }
        )
        assertEquals(STARTED, emulator.lifecycleState)

        parent.lifecycleState = DESTROYED
        assertEquals(CREATED, emulator.lifecycleState)
    }

    // endregion

}
