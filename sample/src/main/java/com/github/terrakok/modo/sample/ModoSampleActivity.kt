package com.github.terrakok.modo.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.Modo.rememberRootScreen
import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.multiscreen.MultiScreenState
import com.github.terrakok.modo.sample.screens.MainScreen
import com.github.terrakok.modo.sample.screens.containers.SampleStack
import com.github.terrakok.modo.stack.StackState
import com.github.terrakok.modo.subtreeStateFlow

class ModoSampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ActivityContent {
                // Remember root screen using rememberSeaveable under the hood.
                val rootScreen = rememberRootScreen {
                    SampleStack(MainScreen(1))
                }
                rootScreen.Content(modifier = Modifier.fillMaxSize())
            }
        }
    }

}
