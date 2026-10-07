package com.nodara.erp.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodara.erp.data.repository.DashboardRepository
import com.nodara.erp.domain.model.DashboardSummary
import com.nodara.erp.domain.model.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {

    private val _summaryState = MutableStateFlow<ResourceState<DashboardSummary>>(ResourceState.Idle)
    val summaryState: StateFlow<ResourceState<DashboardSummary>> = _summaryState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _summaryState.value = ResourceState.Loading
            repository.getDashboardSummary()
                .onSuccess { summary ->
                    _summaryState.value = ResourceState.Success(summary)
                }
                .onFailure { error ->
                    _summaryState.value = ResourceState.Error(error.message ?: "Error al cargar dashboard")
                }
        }
    }
}
