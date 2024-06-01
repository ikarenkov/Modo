package com.github.terrakok.modo.sample.quickstart

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.Modo.rememberRootScreen
import com.github.terrakok.modo.stack.DefaultStackScreen
import com.github.terrakok.modo.stack.StackNavModel

class QuickStartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Remember root screen using rememberSeaveable under the hood.
            val rootScreen = rememberRootScreen {
                QuickStartStackScreen(StackNavModel(QuickStartScreen(1)))
            }
            rootScreen.Content(modifier = Modifier.fillMaxSize())
        }
    }

}
