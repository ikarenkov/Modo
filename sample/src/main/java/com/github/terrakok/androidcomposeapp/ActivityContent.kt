package com.github.terrakok.androidcomposeapp

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
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
import com.github.terrakok.androidcomposeapp.screens.dialogs.showingDialogsCount

@Composable
fun ComponentActivity.ActivityContent(content: @Composable () -> Unit) {
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
            content()
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

@Composable
private fun ComponentActivity.SetupStatusBarColor(isDarkMode: Boolean) {
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