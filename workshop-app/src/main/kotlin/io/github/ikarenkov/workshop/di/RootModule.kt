package io.github.ikarenkov.workshop.di

import io.github.ikarenkov.workshop.data.ClimberProfileRepository
import io.github.ikarenkov.workshop.screens.climbing_level.ClimbingLevelViewModel
import io.github.ikarenkov.workshop.screens.personal_data.ClimberPersonalInfoViewModel
import io.github.ikarenkov.workshop.screens.profile.EnhancedProfileViewModel
import io.github.ikarenkov.workshop.screens.profile.EnhancedProfileViewModelFinal
import io.github.ikarenkov.workshop.screens.profile_setup.ProfileSetupFlowViewModelFinal
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val rootModule = module {
    single<ClimberProfileRepository> { ClimberProfileRepository() }
    viewModel { ClimbingLevelViewModel(it.get(), get()) }
    viewModel { EnhancedProfileViewModel(get()) }
    viewModel { EnhancedProfileViewModelFinal(it.get(), get()) }
    viewModel { ClimberPersonalInfoViewModel(get()) }
    // TODO: Workshop 5.1.2 - di define ProfileSetupViewModel
//    viewModel { ProfileSetupFlowViewModel(it.get(), it.get(), get()) }
    viewModel { ProfileSetupFlowViewModelFinal(it.get(), it.get(), it.get(), get()) }
}