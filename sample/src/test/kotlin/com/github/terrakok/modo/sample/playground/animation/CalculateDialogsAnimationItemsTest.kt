package com.github.terrakok.modo.sample.playground.animation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.sample.playground.animation.ScreenAnimationPhase.ENTER
import com.github.terrakok.modo.sample.playground.animation.ScreenAnimationPhase.EXIT
import com.github.terrakok.modo.sample.playground.animation.ScreenAnimationPhase.IDLE
import kotlinx.parcelize.Parcelize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class CalculateDialogsAnimationItemsTest {
    @OptIn(ExperimentalModoApi::class)
    @ParameterizedTest(name = "{0}")
    @MethodSource("provideTransitionData")
    fun `test calculate`(
        testName: String,
        oldDialogs: List<DialogScreen>,
        newDialogs: List<DialogScreen>,
        result: List<Pair<DialogScreen, ScreenAnimationPhase>>
    ) {
        val actualResult = calculateDialogsAnimationItems(
            oldDialogs = oldDialogs,
            newDialogs = newDialogs
        )
        assertEquals(result, actualResult, "Failed test: $testName")
    }

    @OptIn(ExperimentalModoApi::class)
    @Parcelize
    class MockDialogScreen(
        val i: Int,
        override val screenKey: ScreenKey = ScreenKey("mock_dialog_$i")
    ) : DialogScreen {

        override fun toString(): String {
            return i.toString()
        }

        @Composable
        override fun Content(modifier: Modifier) {
        }

    }

    companion object {
        @OptIn(ExperimentalModoApi::class)
        @JvmStatic
        fun provideTransitionData(): Stream<Arguments> {
            val d1 = MockDialogScreen(1)
            val d2 = MockDialogScreen(2)
            val d3 = MockDialogScreen(3)
            val d4 = MockDialogScreen(4)
            val d5 = MockDialogScreen(5)
            val d6 = MockDialogScreen(6)

            return Stream.of(
                Arguments.of(
                    "Single dialog stays (IDLE)",
                    listOf(d1),
                    listOf(d1),
                    listOf(d1 to IDLE)
                ),
                Arguments.of(
                    "Single dialog enters",
                    emptyList<DialogScreen>(),
                    listOf(d1),
                    listOf(d1 to ENTER)
                ),
                Arguments.of(
                    "Single dialog exits",
                    listOf(d1),
                    emptyList<DialogScreen>(),
                    listOf(d1 to EXIT)
                ),
                Arguments.of(
                    "Empty to empty",
                    emptyList<DialogScreen>(),
                    emptyList<DialogScreen>(),
                    emptyList<Pair<DialogScreen, ScreenAnimationPhase>>()
                ),
                Arguments.of(
                    "First exits, middle stay, last enters",
                    listOf(d1, d2, d3),
                    listOf(d2, d3, d4),
                    listOf(d1 to EXIT, d2 to IDLE, d3 to IDLE, d4 to ENTER)
                ),
                Arguments.of(
                    "All dialogs reordered (all IDLE)",
                    listOf(d1, d2, d3),
                    listOf(d3, d2, d1),
                    listOf(d3 to IDLE, d2 to IDLE, d1 to IDLE)
                ),
                Arguments.of(
                    "Complex interleaving with exits and enters",
                    listOf(d1, d2, d3, d4),
                    listOf(d2, d5, d3, d6),
                    listOf(d1 to EXIT, d2 to IDLE, d5 to ENTER, d3 to IDLE, d4 to EXIT, d6 to ENTER)
                ),
                Arguments.of(
                    "Multiple exits at the beginning",
                    listOf(d1, d2, d3, d4),
                    listOf(d4),
                    listOf(d1 to EXIT, d2 to EXIT, d3 to EXIT, d4 to IDLE)
                ),
                Arguments.of(
                    "Multiple enters at the beginning",
                    listOf(d4),
                    listOf(d1, d2, d3, d4),
                    listOf(d1 to ENTER, d2 to ENTER, d3 to ENTER, d4 to IDLE)
                ),
                Arguments.of(
                    "All dialogs exit, completely new set enters",
                    listOf(d1, d2, d3),
                    listOf(d4, d5, d6),
                    listOf(d1 to EXIT, d2 to EXIT, d3 to EXIT, d4 to ENTER, d5 to ENTER, d6 to ENTER)
                ),
                Arguments.of(
                    "Multiple exits between staying dialogs",
                    listOf(d1, d2, d3, d4, d5),
                    listOf(d1, d5),
                    listOf(d1 to IDLE, d2 to EXIT, d3 to EXIT, d4 to EXIT, d5 to IDLE)
                ),
                Arguments.of(
                    "Multiple enters on top of existing dialogs",
                    listOf(d1, d2),
                    listOf(d1, d2, d3, d4),
                    listOf(d1 to IDLE, d2 to IDLE, d3 to ENTER, d4 to ENTER)
                ),
                Arguments.of(
                    "All dialogs exit (multiple)",
                    listOf(d1, d2, d3),
                    emptyList<DialogScreen>(),
                    listOf(d1 to EXIT, d2 to EXIT, d3 to EXIT)
                ),
                Arguments.of(
                    "All new dialogs enter (multiple)",
                    emptyList<DialogScreen>(),
                    listOf(d1, d2, d3),
                    listOf(d1 to ENTER, d2 to ENTER, d3 to ENTER)
                ),
                Arguments.of(
                    "Only middle dialog stays",
                    listOf(d1, d2, d3, d4, d5),
                    listOf(d3),
                    listOf(d1 to EXIT, d2 to EXIT, d3 to IDLE, d4 to EXIT, d5 to EXIT)
                ),
                Arguments.of(
                    "Complete replacement (no common dialogs)",
                    listOf(d1, d2),
                    listOf(d3, d4),
                    listOf(d1 to EXIT, d2 to EXIT, d3 to ENTER, d4 to ENTER)
                ),
                Arguments.of(
                    "Interleaved pattern - alternating stay/enter",
                    listOf(d1, d3, d5),
                    listOf(d1, d2, d3, d4, d5, d6),
                    listOf(d1 to IDLE, d2 to ENTER, d3 to IDLE, d4 to ENTER, d5 to IDLE, d6 to ENTER)
                ),
                Arguments.of(
                    "Complex reorder with exits between stays",
                    listOf(d1, d2, d3, d4, d5, d6),
                    listOf(d6, d4, d2),
                    listOf(d1 to EXIT, d3 to EXIT, d6 to IDLE, d4 to IDLE, d5 to EXIT, d2 to IDLE)
                ),
                Arguments.of(
                    "First and last stay, middle changes",
                    listOf(d1, d2, d3, d4, d5),
                    listOf(d1, d6, d5),
                    listOf(d1 to IDLE, d2 to EXIT, d3 to EXIT, d4 to EXIT, d6 to ENTER, d5 to IDLE)
                ),
                Arguments.of(
                    "Reverse order with additions",
                    listOf(d1, d2),
                    listOf(d3, d2, d1, d4),
                    listOf(d3 to ENTER, d2 to IDLE, d1 to IDLE, d4 to ENTER)
                ),
            )
        }
    }
}