package com.github.terrakok.androidcomposeapp.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.github.terrakok.androidcomposeapp.ListScreen
import com.github.terrakok.androidcomposeapp.ModelSampleScreen
import com.github.terrakok.androidcomposeapp.ModoLegacyIntegrationActivity
import com.github.terrakok.androidcomposeapp.ModoSampleActivity
import com.github.terrakok.androidcomposeapp.screens.base.ButtonsScreenContent
import com.github.terrakok.androidcomposeapp.screens.containers.HorizontalPagerScreen
import com.github.terrakok.androidcomposeapp.screens.containers.OpenActivityAction
import com.github.terrakok.androidcomposeapp.screens.containers.SampleContainerScreen
import com.github.terrakok.androidcomposeapp.screens.containers.SampleMultiScreen
import com.github.terrakok.androidcomposeapp.screens.containers.StackInLazyColumnScreen
import com.github.terrakok.androidcomposeapp.screens.containers.custom.MovableContentPlaygroundScreen
import com.github.terrakok.androidcomposeapp.screens.containers.custom.RemovableItemContainerScreen
import com.github.terrakok.androidcomposeapp.screens.containers.custom.SampleCustomContainerScreen
import com.github.terrakok.androidcomposeapp.screens.dialogs.DialogsPlayground
import com.github.terrakok.androidcomposeapp.screens.stack.StackActionsScreen
import com.github.terrakok.androidcomposeapp.screens.viewmodel.AndroidViewModelSampleScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.lifecycle.LifecycleScreenEffect
import com.github.terrakok.modo.model.OnScreenRemoved
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.StackNavContainer
import com.github.terrakok.modo.stack.back
import com.github.terrakok.modo.stack.forward
import kotlinx.parcelize.Parcelize
import logcat.logcat

@Parcelize
class MainScreen(
    private val screenIndex: Int,
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @OptIn(ExperimentalModoApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        OnScreenRemoved {
            logcat { "Screen $screenKey was removed" }
        }
        LifecycleScreenEffect {
            LifecycleEventObserver { _: LifecycleOwner, event: Lifecycle.Event ->
                logcat { "$screenKey: Lifecycle.Event $event" }
            }
        }
        MainScreenContent(screenIndex, screenKey, LocalStackNavigation.current, modifier)
    }
}

@Composable
internal fun MainScreenContent(
    screenIndex: Int,
    screenKey: ScreenKey,
    navigation: StackNavContainer?,
    modifier: Modifier = Modifier,
) {
    ButtonsScreenContent(
        screenIndex = screenIndex,
        screenName = "MainScreen",
        screenKey = screenKey,
        state = rememberButtons(navigation = navigation, i = screenIndex),
        modifier = modifier,
    )
}

@Composable
internal fun MainScreenContent(
    screenIndex: Int,
    screenKey: ScreenKey,
    counter: Int,
    navigation: StackNavContainer,
    modifier: Modifier
) {
    ButtonsScreenContent(
        screenIndex = screenIndex,
        screenName = "MainScreen",
        screenKey = screenKey,
        state = rememberButtons(navigation = navigation, i = screenIndex),
        counter = counter,
        modifier = modifier
    )
}

@Composable
private fun rememberButtons(
    navigation: StackNavContainer?,
    i: Int
): GroupedButtonsState {
    val context = LocalContext.current
    return remember {
        GroupedButtonsState(
            listOf(
                GroupedButtonsState.Group(
                    title = null,
                    listOf(
                        "Forward" to { navigation?.forward(MainScreen(i + 1)) },
                        "Back" to { navigation?.back() },
                    )
                ),
                GroupedButtonsState.Group(
                    title = "Navigation samples",
                    listOf(
                        "Stack" to { navigation?.forward(StackActionsScreen(i + 1)) },
                        "Multiscreen" to { navigation?.forward(SampleMultiScreen()) },
                        "Screen Effects" to { navigation?.forward(ScreenEffectsSampleScreen(i + 1)) },
                        "Dialogs & BottomSheets" to { navigation?.forward(DialogsPlayground(i + 1)) },
                        "Container" to { navigation?.forward(SampleContainerScreen(i + 1)) },
                        "Custom Container Actions" to { navigation?.forward(SampleCustomContainerScreen()) },
                        "Custom Container" to { navigation?.forward(RemovableItemContainerScreen()) },
                        "Horizontal Pager" to { navigation?.forward(HorizontalPagerScreen()) },
                        "Stacks in LazyColumn" to { navigation?.forward(StackInLazyColumnScreen()) },
                        "List/Details" to { navigation?.forward(ListScreen()) },
                        // Just experiments
//        "2 items screen" to { navigation.forward(TwoTopItemsStackScreen(i + 1)) },
//                "Demo" to { navigation.forward(SaveableStateHolderDemoScreen()) },
                        "Sample Screen Model" to { navigation?.forward(ModelSampleScreen()) },
                        "Android ViewModel" to { navigation?.forward(AndroidViewModelSampleScreen(i + 1)) },
                    )
                ),
                GroupedButtonsState.Group(
                    title = "Integrations",
                    listOf(
                        "Modern Activity integration" to { navigation?.dispatch(OpenActivityAction<ModoSampleActivity>(context)) },
                        "Legacy Activity integration" to { navigation?.dispatch(OpenActivityAction<ModoLegacyIntegrationActivity>(context)) },
                    )
                ),
                GroupedButtonsState.Group(
                    title = "Dev playground",
                    listOf(
                        "Movable Content" to { navigation?.forward(MovableContentPlaygroundScreen()) },
                        "Animation Playground" to { navigation?.forward(AnimationPlaygroundScreen()) },
                    )
                ),
            )
        )
    }
}