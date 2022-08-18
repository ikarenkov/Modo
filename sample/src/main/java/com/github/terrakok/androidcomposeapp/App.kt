package com.github.terrakok.androidcomposeapp

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.android.compose.ComposeNavigationRenderer
import com.github.terrakok.modo.back

class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val render = ComposeNavigationRenderer { finish() }
        Modo.init(SampleScreen(1))
        Modo.setRenderer(render)
        setContent {
            BackHandler { Modo.navigator.back() }
            Surface(color = MaterialTheme.colors.background) { render.Content() }
        }
    }
}
