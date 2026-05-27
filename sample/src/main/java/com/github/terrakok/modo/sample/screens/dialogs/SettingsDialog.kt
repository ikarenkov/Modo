package com.github.terrakok.modo.sample.screens.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.sample.SampleAppSettings
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.back
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

private const val DIALOG_WIDTH_FRACTION = 0.85f
private const val NAV_TREE_SLIDER_MAX = 10f
private const val NAV_TREE_SLIDER_STEPS = 8

@OptIn(ExperimentalModoApi::class)
@Parcelize
class SettingsDialog(
    override val screenKey: ScreenKey = generateScreenKey()
) : DialogScreen {

    override fun provideDialogConfig(): DialogScreen.DialogConfig = DialogScreen.DialogConfig.Custom

    @Composable
    override fun Content(modifier: Modifier) {
        val navigation = LocalStackNavigation.current
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth(DIALOG_WIDTH_FRACTION)
                    .clickable(
                        enabled = false,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {}
            ) {
                SettingsBody(onCloseClick = { navigation.back() })
            }
        }
    }
}

@Composable
internal fun SettingsBody(onCloseClick: () -> Unit) {
    Column(modifier = Modifier.padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Settings", style = MaterialTheme.typography.h6)
            IconButton(onClick = onCloseClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close settings"
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))
        val scope = rememberCoroutineScope()
        val showNavigationTree by SampleAppSettings.instance.showNavigationTree.stateFlow.collectAsState()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Show navigation tree",
                style = MaterialTheme.typography.body1
            )
            Switch(
                checked = showNavigationTree,
                onCheckedChange = { scope.launch { SampleAppSettings.instance.showNavigationTree.update(it) } }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        val navTreeVisibleScreens by SampleAppSettings.instance.navTreeVisibleScreens.stateFlow.collectAsState()
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = "Nav tree visible screens", style = MaterialTheme.typography.body1)
                Text(text = "$navTreeVisibleScreens", style = MaterialTheme.typography.body1)
            }
            Slider(
                value = navTreeVisibleScreens.toFloat(),
                onValueChange = { scope.launch { SampleAppSettings.instance.navTreeVisibleScreens.update(it.roundToInt()) } },
                valueRange = 1f..NAV_TREE_SLIDER_MAX,
                steps = NAV_TREE_SLIDER_STEPS,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
