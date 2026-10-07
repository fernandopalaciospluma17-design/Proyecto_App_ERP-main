package com.nodara.erp.data.repository

import com.nodara.erp.data.api.ApiService
import com.nodara.erp.data.dto.CreateTeamUserRequestDto
import com.nodara.erp.data.dto.UpdateTeamUserRequestDto
import com.nodara.erp.domain.model.TeamUser

class TeamRepository(private val apiService: ApiService) {

    suspend fun getTeamMembers(): Result<List<TeamUser>> {
        return try {
            val response = apiService.getTeamMembers()
            if (response.isSuccessful && response.body()?.success == true) {
                val items = response.body()?.data ?: emptyList()
                val users = items.map { dto ->
                    TeamUser(
                        id = dto.realId,
                        name = dto.name ?: "Usuario",
                        email = dto.email ?: "",
                        roles = dto.roles,
                        isActive = dto.isActive,
                        createdAt = dto.createdAt
                    )
                }
                Result.success(users)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al cargar miembros del equipo"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTeamMember(
        name: String,
        email: String,
        password: String,
        role: String
    ): Result<TeamUser> {
        return try {
            val req = CreateTeamUserRequestDto(name, email, password, role)
            val response = apiService.createTeamMember(req)
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.data ?: return Result.failure(Exception("Respuesta vacía"))
                Result.success(
                    TeamUser(
                        id = dto.realId,
                        name = dto.name ?: name,
                        email = dto.email ?: email,
                        roles = dto.roles,
                        isActive = dto.isActive,
                        createdAt = dto.createdAt
                    )
                )
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al agregar usuario al equipo"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTeamMember(
        id: String,
        role: String?,
        isActive: Boolean?
    ): Result<TeamUser> {
        return try {
            val req = UpdateTeamUserRequestDto(role, isActive)
            val response = apiService.updateTeamMember(id, req)
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.data ?: return Result.failure(Exception("Respuesta vacía"))
                Result.success(
                    TeamUser(
                        id = dto.realId,
                        name = dto.name ?: "Usuario",
                        email = dto.email ?: "",
                        roles = dto.roles,
                        isActive = dto.isActive,
                        createdAt = dto.createdAt
                    )
                )
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al actualizar usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
