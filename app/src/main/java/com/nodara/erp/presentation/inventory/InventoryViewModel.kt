package com.nodara.erp.presentation.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodara.erp.data.repository.InventoryRepository
import com.nodara.erp.domain.model.Product
import com.nodara.erp.domain.model.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InventoryViewModel(private val repository: InventoryRepository) : ViewModel() {

    private val _productsState = MutableStateFlow<ResourceState<List<Product>>>(ResourceState.Idle)
    val productsState: StateFlow<ResourceState<List<Product>>> = _productsState.asStateFlow()

    private val _actionState = MutableStateFlow<ResourceState<String>>(ResourceState.Idle)
    val actionState: StateFlow<ResourceState<String>> = _actionState.asStateFlow()

    var searchQuery = MutableStateFlow("")

    init {
        loadProducts()
    }

    fun loadProducts(query: String? = searchQuery.value) {
        viewModelScope.launch {
            _productsState.value = ResourceState.Loading
            repository.getProducts(search = if (query.isNull_or_blank()) null else query)
                .onSuccess { list ->
                    _productsState.value = ResourceState.Success(list)
                }
                .onFailure { error ->
                    _productsState.value = ResourceState.Error(error.message ?: "Error al cargar productos")
                }
        }
    }

    fun createProduct(
        sku: String,
        barcode: String,
        name: String,
        imageUrl: String?,
        costo: Double,
        precio: Double,
        stockMinimo: Int,
        initialStock: Int
    ) {
        viewModelScope.launch {
            _actionState.value = ResourceState.Loading
            repository.createProduct(sku, barcode, name, imageUrl, costo, precio, stockMinimo, initialStock)
                .onSuccess {
                    _actionState.value = ResourceState.Success("Producto registrado exitosamente")
                    loadProducts()
                }
                .onFailure { error ->
                    _actionState.value = ResourceState.Error(error.message ?: "Error al crear producto")
                }
        }
    }

    fun updateProduct(
        id: String,
        sku: String,
        barcode: String,
        name: String,
        imageUrl: String?,
        costo: Double,
        precio: Double,
        stockMinimo: Int
    ) {
        viewModelScope.launch {
            _actionState.value = ResourceState.Loading
            repository.updateProduct(id, sku, barcode, name, imageUrl, costo, precio, stockMinimo)
                .onSuccess {
                    _actionState.value = ResourceState.Success("Producto actualizado")
                    loadProducts()
                }
                .onFailure { error ->
                    _actionState.value = ResourceState.Error(error.message ?: "Error al actualizar producto")
                }
        }
    }

    fun deleteProduct(id: String) {
        viewModelScope.launch {
            _actionState.value = ResourceState.Loading
            repository.deleteProduct(id)
                .onSuccess {
                    _actionState.value = ResourceState.Success("Producto eliminado")
                    loadProducts()
                }
                .onFailure { error ->
                    _actionState.value = ResourceState.Error(error.message ?: "Error al eliminar producto")
                }
        }
    }

    fun adjustStock(id: String, quantity: Int, reason: String) {
        viewModelScope.launch {
            _actionState.value = ResourceState.Loading
            repository.adjustStock(id, quantity, reason)
                .onSuccess {
                    _actionState.value = ResourceState.Success("Ajuste de inventario realizado")
                    loadProducts()
                }
                .onFailure { error ->
                    _actionState.value = ResourceState.Error(error.message ?: "Error al ajustar inventario")
                }
        }
    }

    fun resetActionState() {
        _actionState.value = ResourceState.Idle
    }

    private fun String?.isNull_or_blank(): Boolean = this == null || this.trim().isEmpty()
}
