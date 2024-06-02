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
import com.github.terrakok.modo.Modo.rememberRootScreen
import com.github.terrakok.modo.sample.screens.MainScreen
import com.github.terrakok.modo.sample.screens.containers.SampleStack

/**
 * Sample of integration Modo into the fragment
 */
class ModoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ComposeView(inflater.context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                Column {
                    Text(text = "ModoFragment", style = MaterialTheme.typography.h5)
                    val rootScreen = rememberRootScreen {
                        SampleStack(MainScreen(screenIndex = 1, canOpenFragment = true))
                    }
                    rootScreen.Content(modifier = Modifier.fillMaxSize())
                }
            }
        }
}