package io.github.ikarenkov.workshop

import io.github.ikarenkov.workshop.domain.ClimberProfile
import java.util.GregorianCalendar

object WorkshopConfig {
    const val skipLogin = true
    val initialClimberProfile = ClimberProfile(dateOfBirth = GregorianCalendar(1990, 1, 1).time)
//        ClimberProfile(
//            dateOfBirth = GregorianCalendar(1990, 1, 1).time,
//            heightSm = 180,
//            weightKg = 70f,
//            sportLevel = ClimbingLevel(
//                redpointGrade = FrenchScaleGrade(7, FrenchScaleGrade.Letter.A),
//                onsightGrade = FrenchScaleGrade(6, FrenchScaleGrade.Letter.A),
//                flashGrade = FrenchScaleGrade(6, FrenchScaleGrade.Letter.A)
//            ),
//            boulderLevel = ClimbingLevel(
//                redpointGrade = FrenchScaleGrade(7, FrenchScaleGrade.Letter.A),
//                onsightGrade = FrenchScaleGrade(6, FrenchScaleGrade.Letter.A),
//                flashGrade = FrenchScaleGrade(6, FrenchScaleGrade.Letter.A)
//            ),
//        )
}