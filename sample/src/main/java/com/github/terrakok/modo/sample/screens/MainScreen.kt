package com.github.terrakok.modo.sample.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.lifecycle.LifecycleScreenEffect
import com.github.terrakok.modo.model.OnScreenRemoved
import com.github.terrakok.modo.sample.ModoLegacyIntegrationActivity
import com.github.terrakok.modo.sample.ModoSampleActivity
import com.github.terrakok.modo.sample.fragment.ModoFragment
import com.github.terrakok.modo.sample.fragment.ModoFragmentIntegrationActivity
import com.github.terrakok.modo.sample.screens.base.ButtonsScreenContent
import com.github.terrakok.modo.sample.screens.containers.HorizontalPagerScreen
import com.github.terrakok.modo.sample.screens.containers.OpenActivityAction
import com.github.terrakok.modo.sample.screens.containers.SampleContainerScreen
import com.github.terrakok.modo.sample.screens.containers.SampleMultiScreen
import com.github.terrakok.modo.sample.screens.containers.StackInLazyColumnScreen
import com.github.terrakok.modo.sample.screens.containers.custom.MovableContentPlaygroundScreen
import com.github.terrakok.modo.sample.screens.containers.custom.RemovableItemContainerScreen
import com.github.terrakok.modo.sample.screens.containers.custom.SampleCustomContainerScreen
import com.github.terrakok.modo.sample.screens.dialogs.DialogsPlayground
import com.github.terrakok.modo.sample.screens.stack.StackActionsScreen
import com.github.terrakok.modo.sample.screens.viewmodel.AndroidViewModelSampleScreen
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.StackNavContainer
import com.github.terrakok.modo.stack.back
import com.github.terrakok.modo.stack.forward
import com.github.terrakok.modo.util.getActivity
import kotlinx.parcelize.Parcelize
import logcat.logcat

@Parcelize
class MainScreen(
    private val screenIndex: Int,
    private val canOpenFragment: Boolean = false,
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
        MainScreenContent(
            screenIndex = screenIndex,
            screenKey = screenKey,
            navigation = LocalStackNavigation.current,
            modifier = modifier,
            canOpenFragment = canOpenFragment,
        )
    }
}

@Composable
internal fun MainScreenContent(
    screenIndex: Int,
    screenKey: ScreenKey,
    navigation: StackNavContainer?,
    modifier: Modifier = Modifier,
    canOpenFragment: Boolean = false,
) {
    ButtonsScreenContent(
        screenIndex = screenIndex,
        screenName = "MainScreen",
        screenKey = screenKey,
        state = rememberButtons(
            navigation = navigation,
            i = screenIndex,
            canOpenFragment = canOpenFragment
        ),
        modifier = modifier,
    )
}

@Composable
internal fun MainScreenContent(
    screenIndex: Int,
    screenKey: ScreenKey,
    counter: Int,
    navigation: StackNavContainer,
    modifier: Modifier = Modifier,
    canOpenFragment: Boolean = false,
) {
    ButtonsScreenContent(
        screenIndex = screenIndex,
        screenName = "MainScreen",
        screenKey = screenKey,
        state = rememberButtons(
            navigation = navigation,
            i = screenIndex,
            canOpenFragment = canOpenFragment
        ),
        counter = counter,
        modifier = modifier
    )
}

@Composable
private fun rememberButtons(
    navigation: StackNavContainer?,
    i: Int,
    canOpenFragment: Boolean
): GroupedButtonsState {
    val context = LocalContext.current
    return remember(context, navigation) {
        val activity = context.getActivity() as? FragmentActivity
        GroupedButtonsState(
            listOf(
                GroupedButtonsState.Group(
                    title = null,
                    listOf(
                        "Forward" to { navigation?.forward(MainScreen(i + 1, canOpenFragment)) },
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
                    listOfNotNull(
                        "Modern Activity integration" to { navigation?.dispatch(OpenActivityAction<ModoSampleActivity>(context)) },
                        "Legacy Activity integration" to { navigation?.dispatch(OpenActivityAction<ModoLegacyIntegrationActivity>(context)) },
                        "Fragment integration" to { navigation?.dispatch(OpenActivityAction<ModoFragmentIntegrationActivity>(context)) },
                        activity?.let { activity ->
                            if (canOpenFragment) {
                                "New fragment" to {
                                    activity.supportFragmentManager.beginTransaction()
                                        .replace(android.R.id.content, ModoFragment())
                                        .addToBackStack("ModoFragment")
                                        .commit()
                                }
                            } else {
                                null
                            }
                        },
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