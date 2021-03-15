package com.github.terrakok.androidcomposeapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.github.terrakok.modo.android.compose.ComposeRender
import com.github.terrakok.modo.android.compose.init
import com.github.terrakok.modo.android.compose.saveState
import com.github.terrakok.modo.back

class AppActivity : AppCompatActivity() {
    private val modo get() = App.INSTANCE.modo
    private val render = ComposeRender(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        modo.init(savedInstanceState, SampleScreen(1))
        setContent {
            Surface(color = MaterialTheme.colors.background) {
                render.Content()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        modo.render = render
    }

    override fun onPause() {
        super.onPause()
        modo.render = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        modo.saveState(outState)
    }

    override fun onBackPressed() {
        modo.back()
    }
}
