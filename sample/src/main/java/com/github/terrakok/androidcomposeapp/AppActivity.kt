package com.github.terrakok.androidcomposeapp

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.github.terrakok.modo.StackNavigationState
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.containers.Stack
import com.github.terrakok.modo.back

class AppActivity : AppCompatActivity() {

    private lateinit var rootScreen: Stack

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootScreen = Modo.restoreInstanceIfCan<Stack>(savedInstanceState, ::provideRootScreen)
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

    private fun provideRootScreen(): Stack = SampleStack(StackNavigationState(SampleScreen(1)))

}
