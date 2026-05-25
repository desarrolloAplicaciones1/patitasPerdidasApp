package com.uade.huellitas.domain.usecase.onboarding

import com.uade.huellitas.data.local.OnboardingPreferences

class CompleteOnboardingUseCase(private val onboardingPreferences: OnboardingPreferences) {
    suspend operator fun invoke() {
        onboardingPreferences.setOnboardingCompleted()
    }
}
