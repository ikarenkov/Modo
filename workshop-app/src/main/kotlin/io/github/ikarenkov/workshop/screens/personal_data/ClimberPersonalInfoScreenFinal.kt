package io.github.ikarenkov.workshop.screens.personal_data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import io.github.ikarenkov.workshop.screens.profile_setup.SetupStepScreen
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel

@Parcelize
class ClimberPersonalInfoScreenFinal(
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
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        focusRequester.requestFocus()
                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        keyboardController?.hide()
                        focusRequester.freeFocus()
                    }
                    else -> {}
                }
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