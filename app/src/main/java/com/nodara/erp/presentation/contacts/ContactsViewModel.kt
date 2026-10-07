package com.nodara.erp.presentation.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodara.erp.data.repository.ContactsRepository
import com.nodara.erp.domain.model.Contact
import com.nodara.erp.domain.model.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactsViewModel(private val repository: ContactsRepository) : ViewModel() {

    private val _contactsState = MutableStateFlow<ResourceState<List<Contact>>>(ResourceState.Idle)
    val contactsState: StateFlow<ResourceState<List<Contact>>> = _contactsState.asStateFlow()

    private val _actionState = MutableStateFlow<ResourceState<String>>(ResourceState.Idle)
    val actionState: StateFlow<ResourceState<String>> = _actionState.asStateFlow()

    init {
        loadContacts()
    }

    fun loadContacts(search: String? = null) {
        viewModelScope.launch {
            _contactsState.value = ResourceState.Loading
            repository.getContacts(search)
                .onSuccess { list ->
                    _contactsState.value = ResourceState.Success(list)
                }
                .onFailure { error ->
                    _contactsState.value = ResourceState.Error(error.message ?: "Error al cargar contactos")
                }
        }
    }

    fun createContact(name: String, type: String, taxId: String?, email: String?, phone: String?) {
        viewModelScope.launch {
            _actionState.value = ResourceState.Loading
            repository.createContact(name, type, taxId, email, phone)
                .onSuccess {
                    _actionState.value = ResourceState.Success("Contacto $name creado exitosamente")
                    loadContacts()
                }
                .onFailure { error ->
                    _actionState.value = ResourceState.Error(error.message ?: "Error al crear contacto")
                }
        }
    }

    fun deleteContact(id: String) {
        viewModelScope.launch {
            _actionState.value = ResourceState.Loading
            repository.deleteContact(id)
                .onSuccess {
                    _actionState.value = ResourceState.Success("Contacto eliminado")
                    loadContacts()
                }
                .onFailure { error ->
                    _actionState.value = ResourceState.Error(error.message ?: "Error al eliminar contacto")
                }
        }
    }

    fun resetActionState() {
        _actionState.value = ResourceState.Idle
    }
}
