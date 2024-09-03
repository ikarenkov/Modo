package io.github.ikarenkov.workshop

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import io.github.ikarenkov.workshop.screens.WorkshopTheme

class WorkshopActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // TODO: Workshop 1.3.1 - remember hierarchy of screens, using build-in rememberRootScreen, DefaultStackScreen and Screen
            WorkshopTheme {
                Surface(color = Color.White) {
                    // TODO: Workshop 1.3.2 - displaying content of root screen
                }
            }
        }
    }
}