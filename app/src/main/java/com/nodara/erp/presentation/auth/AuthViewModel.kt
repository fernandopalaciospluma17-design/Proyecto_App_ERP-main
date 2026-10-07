package com.nodara.erp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodara.erp.data.repository.AuthRepository
import com.nodara.erp.domain.model.ResourceState
import com.nodara.erp.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<ResourceState<User>>(ResourceState.Idle)
    val loginState: StateFlow<ResourceState<User>> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<ResourceState<String>>(ResourceState.Idle)
    val registerState: StateFlow<ResourceState<String>> = _registerState.asStateFlow()

    private val _resendState = MutableStateFlow<ResourceState<String>>(ResourceState.Idle)
    val resendState: StateFlow<ResourceState<String>> = _resendState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = ResourceState.Error("Introduce el correo y la contraseña.")
            return
        }
        viewModelScope.launch {
            _loginState.value = ResourceState.Loading
            repository.login(email.trim(), password)
                .onSuccess { user ->
                    _loginState.value = ResourceState.Success(user)
                }
                .onFailure { error ->
                    _loginState.value = ResourceState.Error(error.message ?: "Error al iniciar sesión")
                }
        }
    }

    fun register(name: String, companyName: String, email: String, password: String) {
        if (name.isBlank() || companyName.isBlank() || email.isBlank() || password.length < 10) {
            _registerState.value = ResourceState.Error("Por favor completa todos los campos. La contraseña debe tener al menos 10 caracteres.")
            return
        }
        viewModelScope.launch {
            _registerState.value = ResourceState.Loading
            repository.register(name.trim(), companyName.trim(), email.trim(), password)
                .onSuccess { msg ->
                    _registerState.value = ResourceState.Success(msg)
                }
                .onFailure { error ->
                    _registerState.value = ResourceState.Error(error.message ?: "Error al registrar cuenta")
                }
        }
    }

    fun resendVerification(email: String) {
        if (email.isBlank()) {
            _resendState.value = ResourceState.Error("Introduce tu correo electrónico.")
            return
        }
        viewModelScope.launch {
            _resendState.value = ResourceState.Loading
            repository.resendVerification(email.trim())
                .onSuccess { msg ->
                    _resendState.value = ResourceState.Success(msg)
                }
                .onFailure { error ->
                    _resendState.value = ResourceState.Error(error.message ?: "Error al reenviar correo")
                }
        }
    }

    fun resetState() {
        _loginState.value = ResourceState.Idle
        _registerState.value = ResourceState.Idle
        _resendState.value = ResourceState.Idle
    }
}
