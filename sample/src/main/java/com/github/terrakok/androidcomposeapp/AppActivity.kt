package com.github.terrakok.androidcomposeapp

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.github.terrakok.androidcomposeapp.screens.SampleScreen
import com.github.terrakok.androidcomposeapp.screens.containers.SampleStack
import com.github.terrakok.androidcomposeapp.screens.dialogs.showingDialogsCount
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.RootScreen
import com.github.terrakok.modo.stack.StackScreen

class AppActivity : AppCompatActivity() {

    private var rootScreen: RootScreen<StackScreen>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        rootScreen = Modo.init(savedInstanceState, rootScreen) {
            SampleStack(SampleScreen(1))
        }
        setContent {
            Surface(color = MaterialTheme.colors.background) {
                val systemBarPaddings = WindowInsets.systemBars.asPaddingValues()
                val hasDialog = showingDialogsCount.collectAsState().value > 0
                val resources = LocalContext.current.resources
                val isDarkMode = remember(resources) {
                    (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                        Configuration.UI_MODE_NIGHT_YES
                }
                SetupStatusBarColor(hasDialog || isDarkMode)
                Box {
                    rootScreen?.Content(Modifier.fillMaxSize())
                    val color by animateColorAsState(targetValue = if (hasDialog) Color.Black.copy(alpha = 0.5f) else Color.Transparent)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(systemBarPaddings.calculateTopPadding())
                            .background(color)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .height(systemBarPaddings.calculateBottomPadding())
                            .background(color)
                    )
                }
            }
        }
    }

    @Composable
    private fun SetupStatusBarColor(isDarkMode: Boolean) {
        DisposableEffect(isDarkMode) {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT,
                ) { isDarkMode },
                navigationBarStyle = SystemBarStyle.auto(
                    lightScrim,
                    darkScrim,
                ) { isDarkMode },
            )
            onDispose {}
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Modo.save(outState, rootScreen)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            Modo.onRootScreenFinished(rootScreen)
        }
    }

}
