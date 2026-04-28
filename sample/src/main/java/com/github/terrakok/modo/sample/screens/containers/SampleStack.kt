package com.github.terrakok.modo.sample.screens.containers

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.sample.SlideTransition
import com.github.terrakok.modo.sample.screens.base.LifecycleEventsHistory
import com.github.terrakok.modo.sample.screens.base.LogLifecycle
import com.github.terrakok.modo.sample.screens.dialogs.SampleBottomSheet
import com.github.terrakok.modo.sample.screens.dialogs.SampleBottomSheetStack
import com.github.terrakok.modo.stack.DialogPlaceHolder
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.sample.components.NavigationTreeStrip
import com.github.terrakok.modo.stack.StackNavModel
import com.github.terrakok.modo.stack.StackScreen
import com.github.terrakok.modo.stack.StackState
import com.github.terrakok.modo.stack.back
import kotlinx.parcelize.Parcelize

fun OpenActivityAction(
    context: Context,
    clazz: Class<*>
) = NavigationReducer<StackState> { oldState ->
    context.startActivity(
        Intent(context, clazz)
    )
    oldState
}

inline fun <reified T : Activity> OpenActivityAction(context: Context) = OpenActivityAction(context, T::class.java)

@Parcelize
open class SampleStack(
    private val stackNavModel: StackNavModel
) : StackScreen(stackNavModel) {

    constructor(rootScreen: Screen) : this(StackNavModel(rootScreen))

    @Composable
    override fun Content(modifier: Modifier) {
        LogLifecycle()
        Column {
            // The strip below physically sits at the bottom of the window and pads the bottom
            // system bar. Tell descendants of this Box to treat that inset as already handled,
            // otherwise ButtonsScreenContent.windowInsetsPadding(WindowInsets.systemBars) doubles
            // the bottom padding. consumeWindowInsets affects descendants only; the strip is a
            // sibling, so it still sees and pads the full bottom inset.
            Box(
                modifier
                    .weight(1f)
                    .consumeWindowInsets(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
            ) {
                TopScreenContent(
                    modifier = Modifier.fillMaxSize(),
                    dialogModifier = Modifier.fillMaxSize()
                ) { contentModifier ->
                    SlideTransition(contentModifier)
                }
                LifecycleEventsHistory(
                    fontSize = 8.sp,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.5f))
                        .align(Alignment.TopEnd)
                )
            }
            NavigationTreeStrip(
                this@SampleStack,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
            )
        }

    }

    @OptIn(ExperimentalModoApi::class)
    @Composable
    override fun DecorateCustomDialog(dialog: DialogScreen, modifier: Modifier, content: @Composable (Modifier) -> Unit) {
        val isDialogPlaceHolder = remember(dialog) {
            dialog is DialogPlaceHolder
        }
        val isBottomSheet = remember(dialog) {
            dialog is SampleBottomSheet || dialog is SampleBottomSheetStack
        }
        val background by animateColorAsState(
            targetValue = if (isDialogPlaceHolder || isBottomSheet) Color.Transparent else Color.Black.copy(alpha = 0.5f),
            label = "Dialog dim"
        )
        val backgroundClickableModifier = if (!isDialogPlaceHolder) {
            Modifier
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null
                ) {
                    back()
                }
        } else {
            Modifier
        }
        Box(
            modifier = modifier
                .fillMaxSize()
                .then(backgroundClickableModifier)
                .background(background),
        ) {
            content(Modifier)
        }
    }
}