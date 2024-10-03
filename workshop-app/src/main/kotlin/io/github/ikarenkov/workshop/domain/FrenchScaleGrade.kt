package io.github.ikarenkov.workshop.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FrenchScaleGrade(
    val digit: Int,
    val letter: Letter,
    val isPlus: Boolean = false
) : Parcelable {
    override fun toString(): String = "$digit${letter.name}${if (isPlus) "+" else ""}"

    enum class Letter {
        A, B, C
    }
}

val sportRouteGrades = arrayOf(
    FrenchScaleGrade(5, FrenchScaleGrade.Letter.A, false),
    FrenchScaleGrade(5, FrenchScaleGrade.Letter.A, true),
    FrenchScaleGrade(5, FrenchScaleGrade.Letter.B, false),
    FrenchScaleGrade(5, FrenchScaleGrade.Letter.B, true),
    FrenchScaleGrade(5, FrenchScaleGrade.Letter.C, false),
    FrenchScaleGrade(5, FrenchScaleGrade.Letter.C, true),
    FrenchScaleGrade(6, FrenchScaleGrade.Letter.A, false),
    FrenchScaleGrade(6, FrenchScaleGrade.Letter.A, true),
    FrenchScaleGrade(6, FrenchScaleGrade.Letter.B, false),
    FrenchScaleGrade(6, FrenchScaleGrade.Letter.B, true),
    FrenchScaleGrade(6, FrenchScaleGrade.Letter.C, false),
    FrenchScaleGrade(6, FrenchScaleGrade.Letter.C, true),
    FrenchScaleGrade(7, FrenchScaleGrade.Letter.A, false),
    FrenchScaleGrade(7, FrenchScaleGrade.Letter.A, true),
    FrenchScaleGrade(7, FrenchScaleGrade.Letter.B, false),
    FrenchScaleGrade(7, FrenchScaleGrade.Letter.B, true),
    FrenchScaleGrade(7, FrenchScaleGrade.Letter.C, false),
    FrenchScaleGrade(7, FrenchScaleGrade.Letter.C, true),
    FrenchScaleGrade(8, FrenchScaleGrade.Letter.A, false),
    FrenchScaleGrade(8, FrenchScaleGrade.Letter.A, true),
    FrenchScaleGrade(8, FrenchScaleGrade.Letter.B, false),
    FrenchScaleGrade(8, FrenchScaleGrade.Letter.B, true),
    FrenchScaleGrade(8, FrenchScaleGrade.Letter.C, false),
    FrenchScaleGrade(8, FrenchScaleGrade.Letter.C, true),
    FrenchScaleGrade(9, FrenchScaleGrade.Letter.A, false),
    FrenchScaleGrade(9, FrenchScaleGrade.Letter.A, true),
    FrenchScaleGrade(9, FrenchScaleGrade.Letter.B, false),
    FrenchScaleGrade(9, FrenchScaleGrade.Letter.B, true),
    FrenchScaleGrade(9, FrenchScaleGrade.Letter.C, false),
)