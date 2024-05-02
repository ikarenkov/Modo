package com.github.terrakok.androidcomposeapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.github.terrakok.androidcomposeapp.screens.MainScreen
import com.github.terrakok.androidcomposeapp.screens.containers.SampleStack
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.RootScreen
import com.github.terrakok.modo.stack.StackScreen

class ModoLegacyIntegrationActivity : AppCompatActivity() {

    private var rootScreen: RootScreen<StackScreen>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        rootScreen = Modo.init(savedInstanceState, rootScreen) {
            SampleStack(MainScreen(1))
        }
        setContent {
            ActivityContent {
                rootScreen?.Content(Modifier.fillMaxSize())
            }
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
