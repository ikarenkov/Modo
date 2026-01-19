package com.github.terrakok.modo.sample.screens.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.android.ProvideOverlayIntegration
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.sample.screens.base.ButtonsScreenContent
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.StackNavContainer
import com.github.terrakok.modo.stack.StackScreen
import com.github.terrakok.modo.stack.back
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
@OptIn(ExperimentalModoApi::class)
class M3BottomSheet(
    private val screenIndex: Int,
    override val screenKey: ScreenKey = generateScreenKey()
) : DialogScreen {

    override fun provideDialogConfig(): DialogScreen.DialogConfig = DialogScreen.DialogConfig.Custom

    @OptIn(ExperimentalMaterial3Api::class)
    @Suppress("MagicNumber")
    @Composable
    override fun Content(modifier: Modifier) {
        val stackScreen = LocalStackNavigation.current
        val sheetState = rememberModalBottomSheetState()
        SetupNavigationAnimation(sheetState)

        var dismissHandled = rememberSaveable { false }

        ModalBottomSheet(
            modifier = modifier,
            onDismissRequest = {
                // Animation of hiding BottomSheet is taking some time.
                // We can press outside the BottomSheet and trigger onDismissRequest again.
                // This protection prevents from double-back.
                if (!dismissHandled) {
                    dismissHandled = true
                    stackScreen.back()
                }
            },
            containerColor = Color.Transparent,
            sheetState = sheetState,
            dragHandle = null,
            // Using it to fit content to the whole screen.
            // Otherwise there will be background with containerColor, when bottom sheet fully expanded
            contentWindowInsets = { WindowInsets(0, 0, 0, 0) }
        ) {
            ProvideOverlayIntegration {
                val coroutineScope = rememberCoroutineScope()
                ButtonsScreenContent(
                    screenIndex = screenIndex,
                    screenName = "SampleDialog",
                    state = rememberDialogsButtons(LocalContainerScreen.current as StackNavContainer, screenIndex),
                    topRightButtonSlot = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    when (sheetState.currentValue) {
                                        SheetValue.Hidden -> {}
                                        SheetValue.Expanded -> sheetState.partialExpand()
                                        SheetValue.PartiallyExpanded -> sheetState.expand()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Filled.ArrowDropDown),
                                contentDescription = null,
                                modifier = Modifier.rotate(if (sheetState.targetValue == SheetValue.Expanded) 0f else 180f)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.CenterHorizontally)
                        .clickable(
                            enabled = false,
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = null
                        ) {}
                )
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun SetupNavigationAnimation(sheetState: SheetState) {
        val coroutineScope = rememberCoroutineScope()
        val lifecycleOwner = LocalLifecycleOwner.current
        // Add observer to animate bottom sheet manually and avoid jumping on hide/show.
        // Since BottomSheet uses separate window and dialog under the hood, build in animations doesn't work.
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    // ON_START event sends when this screen inters the composition.
                    // If it was hidden before and on the screen, that means that hidden it before and now we need to show it.
                    Lifecycle.Event.ON_START -> {
                        if (sheetState.currentValue == SheetValue.Hidden) {
                            coroutineScope.launch {
                                sheetState.partialExpand()
                            }
                        }
                    }
                    // ON_PAUSE event sends when hide animation is started for this screen.
                    // Since there is no effect of animation in separate window, we need to hide it manually.
                    Lifecycle.Event.ON_PAUSE -> {
                        // Add this check to prevent sending hide second time after whe intentionally close it.
                        if (sheetState.targetValue != SheetValue.Hidden) {
                            coroutineScope.launch {
                                sheetState.hide()
                            }
                        }
                    }
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }
}