package com.github.terrakok.androidcomposeapp.saveable

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import com.github.terrakok.modo.android.compose.ComposeScreen
import com.github.terrakok.modo.android.compose.uniqueScreenKey
import kotlinx.parcelize.Parcelize
import androidx.lifecycle.viewmodel.compose.viewModel

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
fun ProfileDetailsScreen(
    id: String,
    viewModel: DetailsViewModel = viewModel()
) {
    Box {
        Column(Modifier.align(Alignment.Center)) {
            Text(text = "Profile details $id")
        }
    }
}

class DetailsViewModel : ViewModel() {

    init {
        Log.d(this.javaClass.name, "init")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(this.javaClass.name, "onCleared")
    }
}
