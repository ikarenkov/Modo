package com.github.terrakok.modo.android

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.Lifecycle.State.CREATED
import androidx.lifecycle.Lifecycle.State.INITIALIZED
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.Lifecycle.State.STARTED
import com.github.terrakok.modo.android.ModoScreenAndroidAdapter.Companion.stateAlreadyReached
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class ModoScreenAndroidAdapterStateAlreadyReachedTest {

    @ParameterizedTest(name = "State={0}, Event={1} -> shouldSkip={2}")
    @MethodSource("testCases")
    fun `needSkipEvent returns correct result`(
        state: Lifecycle.State,
        event: Lifecycle.Event,
        shouldSkip: Boolean
    ) {
        assertEquals(shouldSkip, stateAlreadyReached(state, event))
    }

    companion object {
        @JvmStatic
        fun testCases() = listOf(
            Arguments.of(INITIALIZED, ON_CREATE, false),
            Arguments.of(INITIALIZED, ON_START, false),
            Arguments.of(INITIALIZED, ON_RESUME, false),
            Arguments.of(INITIALIZED, ON_PAUSE, true),
            Arguments.of(INITIALIZED, ON_STOP, true),
            Arguments.of(INITIALIZED, ON_DESTROY, false),
            Arguments.of(CREATED, ON_CREATE, true),
            Arguments.of(CREATED, ON_START, false),
            Arguments.of(CREATED, ON_RESUME, false),
            Arguments.of(CREATED, ON_PAUSE, true),
            Arguments.of(CREATED, ON_STOP, true),
            Arguments.of(CREATED, ON_DESTROY, false),
            Arguments.of(STARTED, ON_CREATE, true),
            Arguments.of(STARTED, ON_START, true),
            Arguments.of(STARTED, ON_RESUME, false),
            Arguments.of(STARTED, ON_PAUSE, true),
            Arguments.of(STARTED, ON_STOP, false),
            Arguments.of(STARTED, ON_DESTROY, false),
            Arguments.of(RESUMED, ON_CREATE, true),
            Arguments.of(RESUMED, ON_START, true),
            Arguments.of(RESUMED, ON_RESUME, true),
            Arguments.of(RESUMED, ON_PAUSE, false),
            Arguments.of(RESUMED, ON_STOP, false),
            Arguments.of(RESUMED, ON_DESTROY, false),
        )
    }
}
