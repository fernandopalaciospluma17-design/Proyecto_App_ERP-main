package com.nodara.erp.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodara.erp.data.repository.ActivityRepository
import com.nodara.erp.domain.model.AuditLog
import com.nodara.erp.domain.model.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActivityViewModel(private val repository: ActivityRepository) : ViewModel() {

    private val _activityState = MutableStateFlow<ResourceState<List<AuditLog>>>(ResourceState.Idle)
    val activityState: StateFlow<ResourceState<List<AuditLog>>> = _activityState.asStateFlow()

    init {
        loadActivity()
    }

    fun loadActivity() {
        viewModelScope.launch {
            _activityState.value = ResourceState.Loading
            repository.getActivityLogs()
                .onSuccess { list ->
                    _activityState.value = ResourceState.Success(list)
                }
                .onFailure { error ->
                    _activityState.value = ResourceState.Error(error.message ?: "Error al cargar historial de auditoría")
                }
        }
    }
}
