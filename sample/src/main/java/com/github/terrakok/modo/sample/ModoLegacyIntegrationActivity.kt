package com.github.terrakok.modo.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.Modo.rememberRootScreen
import com.github.terrakok.modo.RootScreen
import com.github.terrakok.modo.sample.screens.MainScreen
import com.github.terrakok.modo.sample.screens.containers.SampleStack
import com.github.terrakok.modo.stack.StackScreen

/**
 * Demonstrates manual (legacy) integration of Modo into an Activity without using [rememberRootScreen].
 *
 * This approach requires the host to manually call [Modo.getOrCreateRootScreen],
 * [Modo.save], and [Modo.onRootScreenFinished].
 *
 * **This is not the recommended way to integrate Modo.** Prefer [rememberRootScreen],
 * which handles all lifecycle concerns automatically. Use this only if you cannot use Compose at the Activity level.
 */
class ModoLegacyIntegrationActivity : AppCompatActivity() {

    private var rootScreen: RootScreen<StackScreen>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        rootScreen = Modo.getOrCreateRootScreen(savedInstanceState, rootScreen) {
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
