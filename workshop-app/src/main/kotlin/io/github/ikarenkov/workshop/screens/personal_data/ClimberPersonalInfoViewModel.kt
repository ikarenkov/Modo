package io.github.ikarenkov.workshop.screens.personal_data

import androidx.lifecycle.ViewModel
import io.github.ikarenkov.workshop.data.ClimberProfileRepository
import io.github.ikarenkov.workshop.domain.ClimberProfile
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

class ClimberPersonalInfoViewModel(
    private val climberProfileRepository: ClimberProfileRepository
) : ViewModel() {

    val state: StateFlow<ClimberProfile> = climberProfileRepository.climberProfile

    fun setHeight(height: Int?) {
        climberProfileRepository.updateClimberProfile {
            copy(heightSm = height)
        }
    }

    fun setWeight(weight: Float?) {
        climberProfileRepository.updateClimberProfile {
            copy(weightKg = weight)
        }
    }

    fun setBirthDate(date: Date) {
        climberProfileRepository.updateClimberProfile {
            copy(dateOfBirth = date)
        }
    }
}