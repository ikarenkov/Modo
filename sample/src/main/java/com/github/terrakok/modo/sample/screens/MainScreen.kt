package com.github.terrakok.modo.sample.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import com.github.terrakok.modo.sample.quickstart.QuickStartActivity
import com.github.terrakok.modo.sample.screens.base.ButtonsScreenContent
import com.github.terrakok.modo.sample.screens.containers.CustomStackSample
import com.github.terrakok.modo.sample.screens.containers.HorizontalPagerScreen
import com.github.terrakok.modo.sample.screens.containers.OpenActivityAction
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
            screenKey = screenKey,
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
            screenKey = screenKey,
            navigation = navigation,
            i = screenIndex,
            canOpenFragment = canOpenFragment
        ),
        counter = counter,
        modifier = modifier
    )
}

@Suppress("LongMethod")
@Composable
private fun rememberButtons(
    screenKey: ScreenKey,
    navigation: StackNavContainer?,
    i: Int,
    canOpenFragment: Boolean
): GroupedButtonsState {
    val context = LocalContext.current

    val isFirstScreen by remember {
        derivedStateOf {
            navigation?.navigationState?.stack?.first()?.screenKey == screenKey
        }
    }
    return remember(context, navigation, isFirstScreen) {
        val activity = context.getActivity() as? FragmentActivity
        GroupedButtonsState(
            listOf(
                GroupedButtonsState.Group(
                    title = null,
                    listOf(
                        ModoButtonSpec("Forward") { navigation?.forward(MainScreen(i + 1, canOpenFragment)) },
                        ModoButtonSpec("Back", isEnabled = !isFirstScreen) { navigation?.back() },
                    )
                ),
                GroupedButtonsState.Group(
                    title = "Navigation samples",
                    listOf(
                        ModoButtonSpec("Stack actions") { navigation?.forward(StackActionsScreen(i + 1)) },
                        ModoButtonSpec("HorizontalPager") { navigation?.forward(HorizontalPagerScreen()) },
                        ModoButtonSpec("Custom Stack") { navigation?.forward(CustomStackSample(i + 1)) },
                        ModoButtonSpec("Stacks in LazyColumn") { navigation?.forward(StackInLazyColumnScreen()) },
                        ModoButtonSpec("Dialogs & BottomSheets") { navigation?.forward(DialogsPlayground(i + 1)) },
                        ModoButtonSpec("Multiscreen") { navigation?.forward(SampleMultiScreen()) },
                        ModoButtonSpec("Screen Effects") { navigation?.forward(ScreenEffectsSampleScreen(i + 1)) },
                        ModoButtonSpec("Custom Container Actions") { navigation?.forward(SampleCustomContainerScreen()) },
                        ModoButtonSpec("Custom Container") { navigation?.forward(RemovableItemContainerScreen()) },
                        // Just experiments
//        ModoButtonSpec("2 items screen") { navigation.forward(TwoTopItemsStackScreen(i + 1)) },
//                ModoButtonSpec("Demo") { navigation.forward(SaveableStateHolderDemoScreen()) },
                        ModoButtonSpec("Sample Screen Model") { navigation?.forward(ModelSampleScreen()) },
                        ModoButtonSpec("Android ViewModel") { navigation?.forward(AndroidViewModelSampleScreen(i + 1)) },
                        ModoButtonSpec("List/Details") { navigation?.forward(ListScreen()) },
                    )
                ),
                GroupedButtonsState.Group(
                    title = "Integrations",
                    listOfNotNull(
                        ModoButtonSpec("Modern Activity integration") {
                            navigation?.dispatch(OpenActivityAction<ModoSampleActivity>(context))
                        },
                        ModoButtonSpec("Legacy Activity integration") {
                            navigation?.dispatch(OpenActivityAction<ModoLegacyIntegrationActivity>(context))
                        },
                        ModoButtonSpec("Quick Start Activity") {
                            navigation?.dispatch(OpenActivityAction<QuickStartActivity>(context))
                        },
                        ModoButtonSpec("Fragment integration") {
                            navigation?.dispatch(OpenActivityAction<ModoFragmentIntegrationActivity>(context))
                        },
                        activity?.let { activity ->
                            if (canOpenFragment) {
                                ModoButtonSpec("New fragment") {
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
                        ModoButtonSpec("Movable Content") {
                            navigation?.forward(MovableContentPlaygroundScreen())
                        },
                        ModoButtonSpec("Animation Playground") {
                            navigation?.forward(AnimationPlaygroundScreen())
                        },
                    )
                ),
            )
        )
    }
}