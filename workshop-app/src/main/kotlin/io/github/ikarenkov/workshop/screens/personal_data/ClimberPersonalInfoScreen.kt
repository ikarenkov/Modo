package io.github.ikarenkov.workshop.screens.personal_data

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import io.github.ikarenkov.workshop.screens.profile_setup.SetupStepScreen
import io.github.ikarenkov.workshop.ui.InputNumRow
import io.github.ikarenkov.workshop.ui.TitleCell
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import java.util.Date
import java.util.GregorianCalendar

@Parcelize
class ClimberPersonalInfoScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : SetupStepScreen {

    override val title: String
        get() = "Climbing Profile"

    @Composable
    override fun Content(modifier: Modifier) {
        val viewModel = koinViewModel<ClimberPersonalInfoViewModel>()
        val state by viewModel.state.collectAsState()
        val focusRequester = remember { FocusRequester() }
        val lifecycleOwner = LocalLifecycleOwner.current
        val keyboardController = LocalSoftwareKeyboardController.current
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                // TODO: Workshop 6.1 - use ON_RESUME and ON_PAUSE events to show and hide the keyboard
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
        ClimberProfileSetupScreenContent(
            modifier = modifier,
            heightTextFieldModifier = Modifier.focusRequester(focusRequester),
            dateOfBirth = state.dateOfBirth,
            setDateOfBirth = viewModel::setBirthDate,
            height = state.heightSm?.toString().orEmpty(),
            setHeight = { height ->
                viewModel.setHeight(height.toIntOrNull())
            },
            weight = state.weightKg?.toString().orEmpty(),
            setWeight = { weight ->
                viewModel.setWeight(weight.toFloatOrNull())
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClimberProfileSetupScreenContent(
    dateOfBirth: Date?,
    setDateOfBirth: (Date) -> Unit,
    height: String,
    setHeight: (String) -> Unit,
    weight: String,
    setWeight: (String) -> Unit,
    modifier: Modifier = Modifier,
    heightTextFieldModifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InputNumRow(
            title = "Height",
            value = height,
            onValueChange = setHeight,
            valueName = "sm",
            modifier = Modifier.fillMaxWidth(),
            textFieldModifier = heightTextFieldModifier
        )
        InputNumRow(
            title = "Weight",
            value = weight,
            onValueChange = setWeight,
            valueName = "kg",
            modifier = Modifier.fillMaxWidth()
        )
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dateOfBirth?.time,
            initialDisplayMode = DisplayMode.Input,
        )
        TitleDateInput(
            title = "Date of Birth",
            datePickerState = datePickerState,
            onDateConfirmClick = {
                datePickerState.selectedDateMillis?.let {
                    setDateOfBirth(Date())
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleDateInput(
    title: String,
    datePickerState: DatePickerState,
    onDateConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDateDialog by rememberSaveable { mutableStateOf(false) }
    if (showDateDialog) {
        DatePickerDialog(
            onDismissRequest = { showDateDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        onDateConfirmClick()
                        showDateDialog = false
                    }
                ) {
                    Text("Confirm")
                }
            }
        ) {
            DatePicker(datePickerState)
        }
    }
    TitleCell(
        modifier = modifier,
        title = title,
    ) {
        val context = LocalContext.current
        val dateString by remember {
            derivedStateOf {
                datePickerState.selectedDateMillis?.let {
                    DateFormat.getLongDateFormat(context).format(it)
                } ?: "Select date"
            }
        }
        TextButton(
            onClick = {
                showDateDialog = true
            }
        ) {
            Text(text = dateString)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewDateInput() {
    TitleDateInput(
        title = "Date of Birth",
        datePickerState = rememberDatePickerState(),
        modifier = Modifier.fillMaxWidth(),
        onDateConfirmClick = {}
    )
}

@Preview
@Composable
private fun PreviewScreen() {
    ClimberProfileSetupScreenContent(
        dateOfBirth = GregorianCalendar(1997, 1, 5).time,
        setDateOfBirth = {},
        height = "180",
        setHeight = {},
        weight = "70",
        setWeight = {},
        modifier = Modifier.fillMaxSize()
    )
}