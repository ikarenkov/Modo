package com.github.terrakok.androidcomposeapp.saveable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.android.compose.ComposeScreen
import com.github.terrakok.modo.android.compose.uniqueScreenKey
import kotlinx.parcelize.Parcelize

@Parcelize
class DetailsScreen(
    private val userId: String,
    override val screenKey: String = uniqueScreenKey
) : ComposeScreen("DetailsScreen") {

    @Composable
    override fun Content() {
        ProfileDetailsScreen(id = userId)
    }

}

@Composable
fun ProfileDetailsScreen(id: String) {
    Box {
        Column(Modifier.align(Alignment.Center)) {
            Text(text = "Profile details $id")
        }
    }
}