package com.github.terrakok.androidcomposeapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.github.terrakok.androidcomposeapp.screens.SampleScreen
import com.github.terrakok.androidcomposeapp.screens.containers.SampleStack
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.RootScreen
import com.github.terrakok.modo.stack.StackScreen

class AppActivity : AppCompatActivity() {

    private var rootScreen: RootScreen<StackScreen>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootScreen = Modo.init(savedInstanceState, rootScreen) {
            SampleStack(SampleScreen(1))
        }
        setContent {
            Surface(color = MaterialTheme.colors.background) {
                rootScreen?.Content()
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
