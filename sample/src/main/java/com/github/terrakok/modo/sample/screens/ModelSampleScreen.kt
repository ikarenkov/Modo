package com.github.terrakok.modo.sample.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.model.ScreenModel
import com.github.terrakok.modo.model.coroutineScope
import com.github.terrakok.modo.model.rememberScreenModel
import com.github.terrakok.modo.sample.screens.base.COUNTER_DELAY_MS
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
class ModelSampleScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        val screenModel = rememberScreenModel {
            SampleScreenModel()
        }
        Column(Modifier.fillMaxSize()) {
            Text(text = screenModel.state.intValue.toString())
        }
    }

}

private class SampleScreenModel : ScreenModel {

    val state = mutableIntStateOf(0)

    init {
        coroutineScope.launch {
            while (isActive) {
                delay(COUNTER_DELAY_MS)
                state.intValue++
            }
        }
    }

}