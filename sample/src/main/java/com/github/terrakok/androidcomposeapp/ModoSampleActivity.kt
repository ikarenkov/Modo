package com.github.terrakok.androidcomposeapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.github.terrakok.androidcomposeapp.screens.MainScreen
import com.github.terrakok.androidcomposeapp.screens.containers.SampleStack
import com.github.terrakok.modo.Modo.rememberRootScreen

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
