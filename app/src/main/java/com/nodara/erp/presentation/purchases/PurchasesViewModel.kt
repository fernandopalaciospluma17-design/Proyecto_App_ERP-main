package com.nodara.erp.presentation.purchases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodara.erp.data.dto.CreatePurchaseItemDto
import com.nodara.erp.data.repository.ContactsRepository
import com.nodara.erp.data.repository.InventoryRepository
import com.nodara.erp.data.repository.PurchasesRepository
import com.nodara.erp.domain.model.Contact
import com.nodara.erp.domain.model.Product
import com.nodara.erp.domain.model.Purchase
import com.nodara.erp.domain.model.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PurchasesViewModel(
    private val purchasesRepository: PurchasesRepository,
    private val inventoryRepository: InventoryRepository,
    private val contactsRepository: ContactsRepository
) : ViewModel() {

    private val _purchasesState = MutableStateFlow<ResourceState<List<Purchase>>>(ResourceState.Idle)
    val purchasesState: StateFlow<ResourceState<List<Purchase>>> = _purchasesState.asStateFlow()

    private val _suppliersState = MutableStateFlow<List<Contact>>(emptyList())
    val suppliersState: StateFlow<List<Contact>> = _suppliersState.asStateFlow()

    private val _productsState = MutableStateFlow<List<Product>>(emptyList())
    val productsState: StateFlow<List<Product>> = _productsState.asStateFlow()

    private val _actionState = MutableStateFlow<ResourceState<String>>(ResourceState.Idle)
    val actionState: StateFlow<ResourceState<String>> = _actionState.asStateFlow()

    init {
        loadPurchases()
        loadAuxiliaryData()
    }

    fun loadPurchases() {
        viewModelScope.launch {
            _purchasesState.value = ResourceState.Loading
            purchasesRepository.getPurchases()
                .onSuccess { list ->
                    _purchasesState.value = ResourceState.Success(list)
                }
                .onFailure { error ->
                    _purchasesState.value = ResourceState.Error(error.message ?: "Error al cargar compras")
                }
        }
    }

    private fun loadAuxiliaryData() {
        viewModelScope.launch {
            inventoryRepository.getProducts().onSuccess { _productsState.value = it }
            contactsRepository.getContacts().onSuccess { list ->
                _suppliersState.value = list.filter { c -> c.type == "Proveedor" }
            }
        }
    }

    fun createPurchase(supplierId: String, items: List<CreatePurchaseItemDto>) {
        viewModelScope.launch {
            _actionState.value = ResourceState.Loading
            purchasesRepository.createPurchase(supplierId, null, items)
                .onSuccess {
                    _actionState.value = ResourceState.Success("Orden de compra creada exitosamente")
                    loadPurchases()
                }
                .onFailure { error ->
                    _actionState.value = ResourceState.Error(error.message ?: "Error al registrar orden de compra")
                }
        }
    }

    fun updateStatus(id: String, status: String) {
        viewModelScope.launch {
            _actionState.value = ResourceState.Loading
            purchasesRepository.updatePurchaseStatus(id, status)
                .onSuccess {
                    _actionState.value = ResourceState.Success("Orden de compra $status")
                    loadPurchases()
                }
                .onFailure { error ->
                    _actionState.value = ResourceState.Error(error.message ?: "Error al actualizar estado")
                }
        }
    }

    fun resetActionState() {
        _actionState.value = ResourceState.Idle
    }
}
