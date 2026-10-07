package com.nodara.erp.presentation.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodara.erp.data.dto.CreateInvoiceItemDto
import com.nodara.erp.data.repository.ContactsRepository
import com.nodara.erp.data.repository.InventoryRepository
import com.nodara.erp.data.repository.SalesRepository
import com.nodara.erp.domain.model.Contact
import com.nodara.erp.domain.model.Invoice
import com.nodara.erp.domain.model.Product
import com.nodara.erp.domain.model.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SalesViewModel(
    private val salesRepository: SalesRepository,
    private val inventoryRepository: InventoryRepository,
    private val contactsRepository: ContactsRepository
) : ViewModel() {

    private val _invoicesState = MutableStateFlow<ResourceState<List<Invoice>>>(ResourceState.Idle)
    val invoicesState: StateFlow<ResourceState<List<Invoice>>> = _invoicesState.asStateFlow()

    private val _productsState = MutableStateFlow<List<Product>>(emptyList())
    val productsState: StateFlow<List<Product>> = _productsState.asStateFlow()

    private val _contactsState = MutableStateFlow<List<Contact>>(emptyList())
    val contactsState: StateFlow<List<Contact>> = _contactsState.asStateFlow()

    private val _actionState = MutableStateFlow<ResourceState<String>>(ResourceState.Idle)
    val actionState: StateFlow<ResourceState<String>> = _actionState.asStateFlow()

    init {
        loadInvoices()
        loadAuxiliaryData()
    }

    fun loadInvoices(search: String? = null) {
        viewModelScope.launch {
            _invoicesState.value = ResourceState.Loading
            salesRepository.getInvoices(search)
                .onSuccess { list ->
                    _invoicesState.value = ResourceState.Success(list)
                }
                .onFailure { error ->
                    _invoicesState.value = ResourceState.Error(error.message ?: "Error al cargar facturas")
                }
        }
    }

    private fun loadAuxiliaryData() {
        viewModelScope.launch {
            inventoryRepository.getProducts().onSuccess { _productsState.value = it }
            contactsRepository.getContacts().onSuccess { _contactsState.value = it.filter { c -> c.type == "Cliente" } }
        }
    }

    fun createInvoice(
        number: String,
        customerId: String,
        customerName: String,
        customerTaxId: String?,
        items: List<CreateInvoiceItemDto>,
        issuedAt: String
    ) {
        viewModelScope.launch {
            _actionState.value = ResourceState.Loading
            salesRepository.createInvoice(number, customerId, customerName, customerTaxId, items, issuedAt)
                .onSuccess {
                    _actionState.value = ResourceState.Success("Factura $number creada exitosamente")
                    loadInvoices()
                }
                .onFailure { error ->
                    _actionState.value = ResourceState.Error(error.message ?: "Error al emitir factura")
                }
        }
    }

    fun updateInvoiceStatus(id: String, status: String) {
        viewModelScope.launch {
            _actionState.value = ResourceState.Loading
            salesRepository.updateInvoiceStatus(id, status)
                .onSuccess {
                    _actionState.value = ResourceState.Success("Estado de factura actualizado a $status")
                    loadInvoices()
                }
                .onFailure { error ->
                    _actionState.value = ResourceState.Error(error.message ?: "Error al cambiar estado")
                }
        }
    }

    fun getDocumentLink(id: String, onLinkReady: (String) -> Unit) {
        viewModelScope.launch {
            salesRepository.getDocumentLink(id)
                .onSuccess { url -> onLinkReady(url) }
                .onFailure { error ->
                    _actionState.value = ResourceState.Error(error.message ?: "Error al generar enlace PDF")
                }
        }
    }

    fun resetActionState() {
        _actionState.value = ResourceState.Idle
    }
}
