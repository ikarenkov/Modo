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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.android.compose.ComposeRender
import com.github.terrakok.modo.android.compose.ComposeScreen
import com.github.terrakok.modo.android.compose.init
import com.github.terrakok.modo.android.compose.saveState
import com.github.terrakok.modo.back
import com.github.terrakok.modo.backTo
import com.github.terrakok.modo.exit
import com.github.terrakok.modo.forward
import kotlinx.parcelize.Parcelize

private val modo get() = App.INSTANCE.modo

class AppActivity : AppCompatActivity() {
    private val render = ComposeRender(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = MaterialTheme.colors.background) {
                modo.init(savedInstanceState, render, ListScreen())
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

@Parcelize
class ListScreen : ComposeScreen("ListScreen") {
    @Composable
    override fun Content() {
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
}

@Parcelize
class ItemScreen(val i: Int): ComposeScreen("ItemScreen $i") {
    @Composable
    override fun Content() {
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
                onClick = { modo.backTo(ListScreen().id) }
            ) {
                Text(text = "Back to list")
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
}