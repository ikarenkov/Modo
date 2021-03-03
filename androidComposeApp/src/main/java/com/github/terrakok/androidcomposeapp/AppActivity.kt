package com.github.terrakok.androidcomposeapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import com.github.terrakok.modo.android.compose.ComposeRender
import com.github.terrakok.modo.android.compose.ComposeScreen
import com.github.terrakok.modo.android.compose.init
import com.github.terrakok.modo.back
import com.github.terrakok.modo.forward

class AppActivity : AppCompatActivity() {

    private val modo = App.INSTANCE.modo
    private val render = ComposeRender(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        modo.init(savedInstanceState, render, HomeScreen())
        setContent {
            Surface(color = MaterialTheme.colors.background) {
                render.UI()
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

    override fun onBackPressed() {
        modo.back()
    }
}

fun HomeScreen() = ComposeScreen("HomeScreen") {
    Column {
        Text(text = "Home screen")
        Button(onClick = { App.INSTANCE.modo.forward(AccountListScreen()) }) {
            Text(text = "Go to Account List")
        }
    }
}

fun AccountListScreen() = ComposeScreen("AccountList") {
    Column {
        Text("Account List screen")
    }
}