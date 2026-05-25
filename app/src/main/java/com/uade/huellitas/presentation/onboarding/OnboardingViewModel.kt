package com.uade.huellitas.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uade.huellitas.domain.usecase.onboarding.CompleteOnboardingUseCase
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val completeOnboardingUseCase: CompleteOnboardingUseCase
) : ViewModel() {

    fun onFinishOnboarding(onNavigate: () -> Unit) {
        viewModelScope.launch {
            completeOnboardingUseCase()
            onNavigate()
        }
    }
}
