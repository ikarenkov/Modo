package com.github.terrakok.modo.sample.screens.base

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.terrakok.modo.sample.SampleAppConfig
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun LifecycleEventsHistory(
    modifier: Modifier = Modifier,
    key: String? = null,
    enabled: Boolean = SampleAppConfig.displayLifecycleEvents,
    lifecycleEventsHistory: SnapshotStateList<Lifecycle.Event>? = null,
    fontSize: TextUnit = 16.sp,
) {
    if (enabled && !LocalInspectionMode.current) {
        val lifecycleEventsHistory = lifecycleEventsHistory ?: viewModel<LifecycleEventsViewModel>(key = key).lifecycleEventsHistory
        val lifecycleOwner = LocalLifecycleOwner.current
        val context = LocalContext.current

        DisposableEffect(Unit) {
            val observer = LifecycleEventObserver { _, event ->
                lifecycleEventsHistory += event
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        Column(
            modifier = modifier
                .width(IntrinsicSize.Max)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            lifecycleEventsHistory.clear()
                        },
                        onDoubleTap = {
                            context.openShareText()
                        }
                    )
                }
        ) {
            for (item in lifecycleEventsHistory) {
                Text(text = item.name, fontSize = fontSize)
                if (item == Lifecycle.Event.ON_STOP) {
                    Divider(
                        color = Color.Red,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun BoxScope.LifecycleEventsHistory(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopEnd,
) = LifecycleEventsHistory(
    fontSize = 8.sp,
    modifier = modifier
        .background(Color.White.copy(alpha = 0.5f))
        .align(alignment)
)

class LifecycleEventsViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val EVENTS_KEY = "events"
    }

    // Initialize the SnapshotStateList from saved state if available, or with default values
    val lifecycleEventsHistory: SnapshotStateList<Lifecycle.Event> = savedStateHandle
        .get<List<Int>>(EVENTS_KEY)
        ?.map { ordinal -> Lifecycle.Event.entries[ordinal] }
        ?.toMutableStateList()
        ?: mutableStateListOf()

    init {
        // Observe changes in the eventList and update the SavedStateHandle when it changes
        snapshotFlow {
            derivedStateOf { lifecycleEventsHistory.toList() }.value
        }
            .onEach {
                savedStateHandle[EVENTS_KEY] = it.map { it.ordinal }
            }
            .launchIn(viewModelScope)
    }

}