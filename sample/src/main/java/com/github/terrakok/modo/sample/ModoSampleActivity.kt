package com.github.terrakok.modo.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.github.terrakok.modo.Modo.rememberRootScreen
import com.github.terrakok.modo.sample.screens.MainScreen
import com.github.terrakok.modo.sample.screens.containers.SampleStack

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
