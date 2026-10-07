package com.nodara.erp.presentation.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodara.erp.data.repository.FinanceRepository
import com.nodara.erp.domain.model.CashMovement
import com.nodara.erp.domain.model.FinanceSummary
import com.nodara.erp.domain.model.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val _financeState = MutableStateFlow<ResourceState<Pair<List<CashMovement>, FinanceSummary>>>(ResourceState.Idle)
    val financeState: StateFlow<ResourceState<Pair<List<CashMovement>, FinanceSummary>>> = _financeState.asStateFlow()

    private val _actionState = MutableStateFlow<ResourceState<String>>(ResourceState.Idle)
    val actionState: StateFlow<ResourceState<String>> = _actionState.asStateFlow()

    init {
        loadFinanceData()
    }

    fun loadFinanceData() {
        viewModelScope.launch {
            _financeState.value = ResourceState.Loading
            repository.getFinanceData()
                .onSuccess { pair ->
                    _financeState.value = ResourceState.Success(pair)
                }
                .onFailure { error ->
                    _financeState.value = ResourceState.Error(error.message ?: "Error al cargar finanzas")
                }
        }
    }

    fun createCashMovement(type: String, category: String, concept: String, amount: Double, occurredAt: String) {
        viewModelScope.launch {
            _actionState.value = ResourceState.Loading
            repository.createCashMovement(type, category, concept, amount, occurredAt)
                .onSuccess {
                    _actionState.value = ResourceState.Success("Movimiento de $type registrado")
                    loadFinanceData()
                }
                .onFailure { error ->
                    _actionState.value = ResourceState.Error(error.message ?: "Error al registrar movimiento")
                }
        }
    }

    fun resetActionState() {
        _actionState.value = ResourceState.Idle
    }
}
