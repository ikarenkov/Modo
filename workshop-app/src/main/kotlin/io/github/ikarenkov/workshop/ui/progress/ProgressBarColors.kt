package io.github.ikarenkov.workshop.ui.progress

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

/**
 * Описание цветов прогрессбара
 *
 * @param progressColor Цвет заполнения прогресса, по-умолчанию null - 3х-цветный
 * @param delimiterColor Цвет разделителей между шагами, по-умолчанию null.
 * Разделитель цвета фона родителя, те прозрачный
 * @param emptyProgressColor Цвет пустого (незаполненного) прогрессбара
 */
@Stable
data class ProgressBarColors(
    val progressColor: Color?,
    val delimiterColor: Color?,
    val emptyProgressColor: Color,
) {

    companion object {

        @Stable
        val Default: ProgressBarColors
            @Composable
            get() = ProgressBarColors(
                progressColor = null,
                delimiterColor = null,
                emptyProgressColor = MaterialTheme.colorScheme.background,
            )

    }

}