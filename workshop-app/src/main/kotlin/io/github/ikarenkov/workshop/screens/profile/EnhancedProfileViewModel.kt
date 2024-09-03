package io.github.ikarenkov.workshop.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.ikarenkov.workshop.core.mapStateFlow
import io.github.ikarenkov.workshop.data.ClimberProfileRepository
import io.github.ikarenkov.workshop.domain.ClimberProfile
import kotlinx.coroutines.flow.StateFlow

class EnhancedProfileViewModel(
    private val climberProfileRepository: ClimberProfileRepository
) : ViewModel() {

    val state: StateFlow<UiState> = climberProfileRepository.climberProfile
        .mapStateFlow(viewModelScope) {
            it.toUiState()
        }

    private fun ClimberProfile.toUiState(): UiState = UiState(
        name = name,
        description = description,
        finishedClimbingSetup = boulderLevel.hasAllGrades() && sportLevel.hasAllGrades()
    )

    data class UiState(
        val name: String?,
        val description: String?,
        val finishedClimbingSetup: Boolean
    )
}