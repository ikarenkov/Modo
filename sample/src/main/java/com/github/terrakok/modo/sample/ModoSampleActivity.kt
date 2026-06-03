package com.github.terrakok.modo.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.Modo.rememberRootScreen
import com.github.terrakok.modo.sample.components.NavigationTreeStrip
import com.github.terrakok.modo.sample.screens.MainScreen
import com.github.terrakok.modo.sample.screens.containers.SampleStack
import com.github.terrakok.modo.sample.screens.dialogs.SettingsDialog
import com.github.terrakok.modo.stack.forward

class ModoSampleActivity : AppCompatActivity() {

    @OptIn(ExperimentalModoApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ActivityContent {
                val rootScreen = rememberRootScreen {
                    SampleStack(MainScreen(1))
                }
                val stackScreen = rootScreen.screen
                val showNavigationTree by SampleAppSettings.instance.showNavigationTree.stateFlow.collectAsState()
                val navTreeVisibleScreens by SampleAppSettings.instance.navTreeVisibleScreens.stateFlow.collectAsState()
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        rootScreen.Content(modifier = Modifier.fillMaxSize())
                        IconButton(
                            onClick = { stackScreen.forward(SettingsDialog()) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .statusBarsPadding()
                                .padding(end = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "App settings",
                                tint = Color.White
                            )
                        }
                    }
                    if (showNavigationTree) {
                        NavigationTreeStrip(
                            container = stackScreen,
                            visibleScreens = navTreeVisibleScreens,
                            modifier = Modifier
                                .fillMaxWidth()
                                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
                        )
                    }
                }
            }
        }
    }

}
