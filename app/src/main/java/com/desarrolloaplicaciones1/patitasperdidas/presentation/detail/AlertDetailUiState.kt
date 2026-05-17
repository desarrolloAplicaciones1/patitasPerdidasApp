package com.desarrolloaplicaciones1.patitasperdidas.presentation.detail

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Alert

sealed class AlertDetailUiState {
    object Loading                               : AlertDetailUiState()
    data class Success(val alert: Alert)         : AlertDetailUiState()
    data class Error(val message: String)        : AlertDetailUiState()
    object Deleted                               : AlertDetailUiState()
    object Resolved                              : AlertDetailUiState()
}
