package io.github.ikarenkov.workshop.screens

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalModoApi::class)
@Parcelize
@Suppress("MagicNumbers")
class TrainingRecommendationsDialogScreenFinal(
    override val screenKey: ScreenKey = generateScreenKey()
) : DialogScreen {

    @Composable
    override fun Content(modifier: Modifier) {
        Surface(modifier, shape = RoundedCornerShape(16.dp)) {
            TrainingRecommendationsContent(Modifier.fillMaxHeight(0.8f))
        }
    }
}