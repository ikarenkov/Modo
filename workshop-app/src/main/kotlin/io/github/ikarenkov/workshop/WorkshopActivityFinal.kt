package io.github.ikarenkov.workshop

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.terrakok.modo.Modo.rememberRootScreen
import com.github.terrakok.modo.RootScreen
import com.github.terrakok.modo.stack.DefaultStackScreen
import com.github.terrakok.modo.stack.StackNavModel
import io.github.ikarenkov.workshop.screens.WorkshopTheme
import io.github.ikarenkov.workshop.screens.main.MainTabScreenFinal

class WorkshopActivityFinal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Workshop 1.3.2 - remember hierarchy of screens, using build-in rememberRootrememberRootScreen, DefaultStackScreen and Screen
            val rootScreen: RootScreen<DefaultStackScreen> = rememberRootScreen {
                DefaultStackScreen(
                    StackNavModel(
                        MainTabScreenFinal()
                    )
                )
            }
            WorkshopTheme {
                Surface(color = Color.White) {
                    // Workshop 1.3.2 - displaying content of root screen
                    rootScreen.Content(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }

}