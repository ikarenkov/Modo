package io.github.ikarenkov.workshop.data

import io.github.ikarenkov.workshop.WorkshopConfig
import io.github.ikarenkov.workshop.domain.ClimberProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ClimberProfileRepository {
    val dataSource = MutableStateFlow(WorkshopConfig.initialClimberProfile)
    val climberProfile: StateFlow<ClimberProfile>
        get() = dataSource

    fun updateClimberProfile(update: ClimberProfile.() -> ClimberProfile) {
        dataSource.update(update)
    }
}