package com.nodara.erp.presentation.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodara.erp.data.repository.ReportsRepository
import com.nodara.erp.domain.model.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportsViewModel(private val repository: ReportsRepository) : ViewModel() {

    private val _seedState = MutableStateFlow<ResourceState<String>>(ResourceState.Idle)
    val seedState: StateFlow<ResourceState<String>> = _seedState.asStateFlow()

    fun seedSampleData() {
        viewModelScope.launch {
            _seedState.value = ResourceState.Loading
            repository.seedSampleData()
                .onSuccess { summary ->
                    val msg = summary.message ?: "Datos iniciales cargados: ${summary.products} productos, ${summary.contacts} contactos, ${summary.invoices} facturas."
                    _seedState.value = ResourceState.Success(msg)
                }
                .onFailure { error ->
                    _seedState.value = ResourceState.Error(error.message ?: "Error al cargar datos de muestra")
                }
        }
    }

    fun resetState() {
        _seedState.value = ResourceState.Idle
    }
}
