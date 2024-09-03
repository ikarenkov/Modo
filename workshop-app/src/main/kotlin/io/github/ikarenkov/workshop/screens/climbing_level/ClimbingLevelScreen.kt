package io.github.ikarenkov.workshop.screens.climbing_level

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.sd.lib.compose.wheel_picker.FVerticalWheelPicker
import com.sd.lib.compose.wheel_picker.rememberFWheelPickerState
import io.github.ikarenkov.workshop.domain.ClimbingType
import io.github.ikarenkov.workshop.domain.FrenchScaleGrade
import io.github.ikarenkov.workshop.domain.sportRouteGrades
import io.github.ikarenkov.workshop.screens.profile_setup.SetupStepScreen
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
class ClimbingLevelScreen(
    val climbingType: ClimbingType,
    override val screenKey: ScreenKey = generateScreenKey()
) : SetupStepScreen {

    override val title: String
        get() = when (climbingType) {
            ClimbingType.Sport -> "Sport climbing level"
            ClimbingType.Bouldering -> "Bouldering level"
        }

    @Composable
    override fun Content(modifier: Modifier) {
        // TODO: workshop - ViewModel integration
        val viewModel = koinViewModel<ClimbingLevelViewModel> {
            parametersOf(climbingType)
        }
        val state = viewModel.state.collectAsState()
        ClimbingLevelSetupScreenContent(
            redpointGrade = state.value.redpointGrade,
            onsightGrade = state.value.onsightGrade,
            flashGrade = state.value.flashGrade,
            setRedpointGrade = viewModel::setRedpointGrade,
            setFlashGrade = viewModel::setFlashGrade,
            setOnsightGrade = viewModel::setOnsightGrade,
            modifier = modifier
        )
    }
}

@Composable
private fun ClimbingLevelSetupScreenContent(
    redpointGrade: FrenchScaleGrade?,
    onsightGrade: FrenchScaleGrade?,
    flashGrade: FrenchScaleGrade?,
    setRedpointGrade: (FrenchScaleGrade) -> Unit,
    setOnsightGrade: (FrenchScaleGrade) -> Unit,
    setFlashGrade: (FrenchScaleGrade) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        SelectGradeItem(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            grade = redpointGrade,
            setGrade = setRedpointGrade,
            title = "Max redpoint grade",
            description = "Max redpoint is the hardest grade you have climbed without falling or resting on the rope."
        )

        HorizontalDivider(Modifier.fillMaxWidth())

        SelectGradeItem(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            grade = onsightGrade,
            setGrade = setOnsightGrade,
            title = "Max onsight grade",
            description = "Max onsight grade is the hardest grade you have climbed on the first attempt without any prior knowledge of the route."
        )

        HorizontalDivider(Modifier.fillMaxWidth())

        SelectGradeItem(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            grade = flashGrade,
            setGrade = setFlashGrade,
            title = "Max flash grade",
            description = "Max flash grade is the hardest grade you have climbed on the first attempt with prior knowledge of the route."
        )
    }
}

@Composable
private fun SelectGradeItem(
    grade: FrenchScaleGrade?,
    setGrade: (FrenchScaleGrade) -> Unit,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    val (showDialog, setShowDialog) = rememberSaveable { mutableStateOf(false) }
    FrenchGradeDialog(grade, setGrade, showDialog, setShowDialog)
    Column(
        modifier =
        Modifier
            .clickable { setShowDialog(true) }
            .then(
                modifier.fillMaxWidth()
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = title, style = MaterialTheme.typography.labelLarge)
            Text(grade?.toString() ?: "Select grade")
        }
        Spacer(Modifier.height(10.dp))
        Text(text = description, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun FrenchGradeDialog(
    grade: FrenchScaleGrade?,
    setGrade: (FrenchScaleGrade) -> Unit,
    showDialog: Boolean,
    setShowDialog: (Boolean) -> Unit,
) {
    val initialPickerPos = remember { grade?.let { sportRouteGrades.indexOf(it) } ?: 0 }
    val pickerState = rememberFWheelPickerState(initialPickerPos)
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { setShowDialog(false) },
            title = { Text("Select grade") },
            text = {
                FVerticalWheelPicker(
                    state = pickerState,
                    count = sportRouteGrades.size
                ) { index: Int ->
                    Text(sportRouteGrades[index].toString())
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val currentIndex = pickerState.currentIndex
                        if (currentIndex != -1) {
                            setGrade(sportRouteGrades[pickerState.currentIndexSnapshot])
                            setShowDialog(false)
                        }
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { setShowDialog(false) }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview
@Composable
private fun PreviewClimbingLevelSetup() {
    ClimbingLevelSetupScreenContent(
        modifier = Modifier.fillMaxSize(),
        redpointGrade = FrenchScaleGrade(7, FrenchScaleGrade.Letter.A, true),
        setRedpointGrade = {},
        onsightGrade = null,
        setFlashGrade = {},
        flashGrade = null,
        setOnsightGrade = {}
    )
}