package com.nodara.erp.presentation.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodara.erp.data.repository.TeamRepository
import com.nodara.erp.domain.model.ResourceState
import com.nodara.erp.domain.model.TeamUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeamViewModel(private val repository: TeamRepository) : ViewModel() {

    private val _teamState = MutableStateFlow<ResourceState<List<TeamUser>>>(ResourceState.Idle)
    val teamState: StateFlow<ResourceState<List<TeamUser>>> = _teamState.asStateFlow()

    private val _actionState = MutableStateFlow<ResourceState<String>>(ResourceState.Idle)
    val actionState: StateFlow<ResourceState<String>> = _actionState.asStateFlow()

    init {
        loadTeam()
    }

    fun loadTeam() {
        viewModelScope.launch {
            _teamState.value = ResourceState.Loading
            repository.getTeamMembers()
                .onSuccess { list ->
                    _teamState.value = ResourceState.Success(list)
                }
                .onFailure { error ->
                    _teamState.value = ResourceState.Error(error.message ?: "Error al cargar equipo")
                }
        }
    }

    fun createMember(name: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            _actionState.value = ResourceState.Loading
            repository.createTeamMember(name, email, password, role)
                .onSuccess {
                    _actionState.value = ResourceState.Success("Usuario $name agregado al equipo")
                    loadTeam()
                }
                .onFailure { error ->
                    _actionState.value = ResourceState.Error(error.message ?: "Error al agregar usuario")
                }
        }
    }

    fun toggleActive(id: String, currentActive: Boolean) {
        viewModelScope.launch {
            _actionState.value = ResourceState.Loading
            repository.updateTeamMember(id, null, !currentActive)
                .onSuccess {
                    _actionState.value = ResourceState.Success("Estado de usuario actualizado")
                    loadTeam()
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
