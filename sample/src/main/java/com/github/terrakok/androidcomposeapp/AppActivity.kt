package com.github.terrakok.androidcomposeapp

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.github.terrakok.androidcomposeapp.screens.SampleScreen
import com.github.terrakok.androidcomposeapp.screens.containers.SampleStack
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.containers.StackScreen
import com.github.terrakok.modo.containers.back

class AppActivity : AppCompatActivity() {

    private lateinit var rootScreen: StackScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootScreen = Modo.restoreInstanceIfCan<StackScreen>(savedInstanceState) {
            SampleStack(SampleScreen(1))
        }
        setContent {
            BackHandler { rootScreen.back() }
            Surface(color = MaterialTheme.colors.background) {
                rootScreen.Content()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Modo.saveInstanceState(outState, rootScreen)
        super.onSaveInstanceState(outState)
    }

}
