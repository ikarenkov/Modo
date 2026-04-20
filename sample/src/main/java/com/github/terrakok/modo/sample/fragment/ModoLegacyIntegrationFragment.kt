package com.github.terrakok.modo.sample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.Modo.rememberRootScreen
import com.github.terrakok.modo.RootScreen
import com.github.terrakok.modo.isFragmentClosing
import com.github.terrakok.modo.sample.screens.MainScreen
import com.github.terrakok.modo.sample.screens.containers.SampleStack
import com.github.terrakok.modo.stack.StackScreen

/**
 * Demonstrates manual (legacy) integration of Modo into a Fragment without using [rememberRootScreen].
 *
 * This approach requires the host to manually call [Modo.getOrCreateRootScreen],
 * [Modo.save], and [Modo.onRootScreenFinished].
 *
 * **This is not the recommended way to integrate Modo.** Prefer [rememberRootScreen],
 * which handles all lifecycle concerns automatically. Use this only if you cannot use Compose at the Fragment level.
 */
class ModoLegacyIntegrationFragment : Fragment() {

    private var rootScreen: RootScreen<StackScreen>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ComposeView(inflater.context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            rootScreen = Modo.getOrCreateRootScreen(savedInstanceState, rootScreen) {
                SampleStack(MainScreen(1))
            }
            setContent {
                Column {
                    Text(text = "ModoLegacyFragment", style = MaterialTheme.typography.h5)
                    val rootScreen = rememberRootScreen {
                        SampleStack(MainScreen(screenIndex = 1, canOpenFragment = true))
                    }
                    rootScreen.Content(modifier = Modifier.fillMaxSize())
                }
            }
        }

    override fun onSaveInstanceState(outState: Bundle) {
        Modo.save(outState, rootScreen)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFragmentClosing(Lifecycle.Event.ON_DESTROY, requireActivity())) {
            Modo.onRootScreenFinished(rootScreen)
        }
    }

}
