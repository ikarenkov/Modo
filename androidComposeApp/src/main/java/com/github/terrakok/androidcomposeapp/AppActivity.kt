package com.github.terrakok.androidcomposeapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.android.compose.ComposeRender
import com.github.terrakok.modo.android.compose.ComposeScreen
import com.github.terrakok.modo.android.compose.init
import com.github.terrakok.modo.back
import com.github.terrakok.modo.exit
import com.github.terrakok.modo.forward

private val modo get() = App.INSTANCE.modo

class AppActivity : AppCompatActivity() {
    private val render = ComposeRender(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        modo.init(savedInstanceState, render, ListScreen())
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

fun ListScreen(): ComposeScreen = ComposeScreen("ListScreen") {
    LazyColumn {
        items(100) { i: Int ->
            Button(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                onClick = { modo.forward(ItemScreen(i)) }
            ) {
                Text(text = "Open item $i")
            }
        }
    }
}

fun ItemScreen(i: Int): ComposeScreen = ComposeScreen("ItemScreen") {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Text("Item $i")
        Button(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            onClick = { modo.forward(ItemScreen(i + 1)) }
        ) {
            Text(text = "Open item ${i + 1}")
        }
        Button(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            onClick = {
                modo.forward(
                    ItemScreen(i + 1),
                    ItemScreen(i + 2),
                    ItemScreen(i + 3)
                )
            }
        ) {
            Text(text = "Open [${i + 1} ${i + 2} ${i + 3}]")
        }
        Button(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            onClick = { modo.back() }
        ) {
            Text(text = "Back")
        }
        Button(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            onClick = { modo.exit() }
        ) {
            Text(text = "Exit")
        }
    }
}