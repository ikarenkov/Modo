package com.github.terrakok.modo.animation

import com.github.terrakok.modo.ComposeRendererScope
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.MockDialogScreen
import com.github.terrakok.modo.MockScreen
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.animation.StackTransitionType.Idle
import com.github.terrakok.modo.animation.StackTransitionType.Pop
import com.github.terrakok.modo.animation.StackTransitionType.Push
import com.github.terrakok.modo.animation.StackTransitionType.Replace
import com.github.terrakok.modo.stack.StackState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@OptIn(ExperimentalModoApi::class)
class CalculateStackTransitionTypeTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideTransitionData")
    fun `test calculateStackTransitionType`(
        expectedType: StackTransitionType,
        testName: String,
        oldStack: List<Screen>?,
        newStack: List<Screen>?,
        currentScreen: Screen,
    ) {
        val scope = ComposeRendererScope<StackState>(
            oldState = oldStack?.let(::StackState),
            newState = newStack?.let(::StackState),
            screen = currentScreen
        )

        val actualType = scope.calculateStackTransitionType()
        assertEquals(expectedType, actualType, "Failed on: $testName")
    }

    companion object {
        @JvmStatic
        fun provideTransitionData(): Stream<Arguments> {
            val s1 = MockScreen()
            val s2 = MockScreen()
            val s3 = MockScreen()
            val d1 = MockDialogScreen()
            val d2 = MockDialogScreen()
            val d3 = MockDialogScreen()

            return Stream.of(
                Arguments.of(Idle, "states are null", null, null, s1),
                Arguments.of(Idle, "stacks are same", listOf(s1, s2), listOf(s1, s2), s2),
                Arguments.of(Push, "when new screen added", listOf(s1), listOf(s1, s2), s2),
                Arguments.of(Pop, "returning to previous", listOf(s1, s2), listOf(s1), s1),
                Arguments.of(Replace, "top is different", listOf(s1, s2), listOf(s1, s3), s3),
                Arguments.of(Idle, "old stack is empty", emptyList<Screen>(), listOf(s1), s1),
                // Changes under dialogs
                Arguments.of(Replace, "replaces screen under dialogs", listOf(s1, d1, d2), listOf(s2, d1, d2), s2),
                Arguments.of(Push, "add screen under dialogs", listOf(s1, d1, d2), listOf(s1, s2, d1, d2), s2),
                Arguments.of(Pop, "remove screen under dialogs", listOf(s1, s2, d1, d2), listOf(s1, d1, d2), s1),
                Arguments.of(Idle, "remove screen before dialogs and screen", listOf(s1, s2, d1, d2), listOf(s2, d1, d2), s2),
            )
        }
    }
}