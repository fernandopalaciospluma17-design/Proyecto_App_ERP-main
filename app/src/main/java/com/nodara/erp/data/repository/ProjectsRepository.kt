package com.nodara.erp.data.repository

import com.nodara.erp.data.api.ApiService
import com.nodara.erp.data.dto.CreateProjectRequestDto
import com.nodara.erp.domain.model.Project

class ProjectsRepository(private val apiService: ApiService) {

    suspend fun getProjects(): Result<List<Project>> {
        return try {
            val response = apiService.getProjects()
            if (response.isSuccessful && response.body()?.success == true) {
                val items = response.body()?.data ?: emptyList()
                val projects = items.map { dto ->
                    Project(
                        id = dto.realId,
                        name = dto.name ?: "Proyecto",
                        client = dto.client ?: "",
                        description = dto.description ?: "",
                        status = dto.status ?: "planned",
                        budget = dto.budget,
                        progress = dto.progress,
                        dueAt = dto.dueAt
                    )
                }
                Result.success(projects)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al cargar proyectos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createProject(
        name: String,
        client: String,
        description: String,
        status: String,
        budget: Double,
        progress: Int,
        dueAt: String?
    ): Result<Project> {
        return try {
            val req = CreateProjectRequestDto(name, client, description, status, budget, progress, dueAt)
            val response = apiService.createProject(req)
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.data ?: return Result.failure(Exception("Respuesta vacía"))
                Result.success(
                    Project(
                        id = dto.realId,
                        name = dto.name ?: name,
                        client = dto.client ?: client,
                        description = dto.description ?: description,
                        status = dto.status ?: status,
                        budget = dto.budget,
                        progress = dto.progress,
                        dueAt = dto.dueAt
                    )
                )
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al crear proyecto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProject(
        id: String,
        name: String,
        client: String,
        description: String,
        status: String,
        budget: Double,
        progress: Int,
        dueAt: String?
    ): Result<Project> {
        return try {
            val req = CreateProjectRequestDto(name, client, description, status, budget, progress, dueAt)
            val response = apiService.updateProject(id, req)
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.data ?: return Result.failure(Exception("Respuesta vacía"))
                Result.success(
                    Project(
                        id = dto.realId,
                        name = dto.name ?: name,
                        client = dto.client ?: client,
                        description = dto.description ?: description,
                        status = dto.status ?: status,
                        budget = dto.budget,
                        progress = dto.progress,
                        dueAt = dto.dueAt
                    )
                )
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al actualizar proyecto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
