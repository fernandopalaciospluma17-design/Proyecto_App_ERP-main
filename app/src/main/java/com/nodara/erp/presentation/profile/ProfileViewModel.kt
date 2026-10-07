package com.nodara.erp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodara.erp.data.repository.AuthRepository
import com.nodara.erp.data.repository.ReportsRepository
import com.nodara.erp.domain.model.ResourceState
import com.nodara.erp.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val reportsRepository: ReportsRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<ResourceState<User>>(ResourceState.Idle)
    val userState: StateFlow<ResourceState<User>> = _userState.asStateFlow()

    private val _healthState = MutableStateFlow<Boolean?>(null)
    val healthState: StateFlow<Boolean?> = _healthState.asStateFlow()

    init {
        loadUser()
        checkHealth()
    }

    fun loadUser() {
        viewModelScope.launch {
            _userState.value = ResourceState.Loading
            authRepository.getCurrentUser()
                .onSuccess { user -> _userState.value = ResourceState.Success(user) }
                .onFailure { error -> _userState.value = ResourceState.Error(error.message ?: "Sesión requerida") }
        }
    }

    fun checkHealth() {
        viewModelScope.launch {
            reportsRepository.getHealth().onSuccess { online -> _healthState.value = online }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        authRepository.logout()
        onLoggedOut()
    }
}
