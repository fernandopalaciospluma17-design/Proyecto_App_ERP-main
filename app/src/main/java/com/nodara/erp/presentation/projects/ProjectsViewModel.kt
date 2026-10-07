package com.nodara.erp.presentation.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodara.erp.data.repository.ProjectsRepository
import com.nodara.erp.domain.model.Project
import com.nodara.erp.domain.model.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProjectsViewModel(private val repository: ProjectsRepository) : ViewModel() {

    private val _projectsState = MutableStateFlow<ResourceState<List<Project>>>(ResourceState.Idle)
    val projectsState: StateFlow<ResourceState<List<Project>>> = _projectsState.asStateFlow()

    private val _actionState = MutableStateFlow<ResourceState<String>>(ResourceState.Idle)
    val actionState: StateFlow<ResourceState<String>> = _actionState.asStateFlow()

    init {
        loadProjects()
    }

    fun loadProjects() {
        viewModelScope.launch {
            _projectsState.value = ResourceState.Loading
            repository.getProjects()
                .onSuccess { list ->
                    _projectsState.value = ResourceState.Success(list)
                }
                .onFailure { error ->
                    _projectsState.value = ResourceState.Error(error.message ?: "Error al cargar proyectos")
                }
        }
    }

    fun createProject(name: String, client: String, description: String, status: String, budget: Double, progress: Int) {
        viewModelScope.launch {
            _actionState.value = ResourceState.Loading
            repository.createProject(name, client, description, status, budget, progress, null)
                .onSuccess {
                    _actionState.value = ResourceState.Success("Proyecto $name creado exitosamente")
                    loadProjects()
                }
                .onFailure { error ->
                    _actionState.value = ResourceState.Error(error.message ?: "Error al crear proyecto")
                }
        }
    }

    fun resetActionState() {
        _actionState.value = ResourceState.Idle
    }
}
