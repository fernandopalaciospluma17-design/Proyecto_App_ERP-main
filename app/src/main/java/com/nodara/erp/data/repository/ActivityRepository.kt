package com.nodara.erp.data.repository

import com.nodara.erp.data.api.ApiService
import com.nodara.erp.domain.model.AuditLog

class ActivityRepository(private val apiService: ApiService) {

    suspend fun getActivityLogs(): Result<List<AuditLog>> {
        return try {
            val response = apiService.getActivityLogs()
            if (response.isSuccessful && response.body()?.success == true) {
                val items = response.body()?.data ?: emptyList()
                val logs = items.map { dto ->
                    AuditLog(
                        id = dto.realId,
                        userId = dto.userId ?: "",
                        userName = dto.userName ?: "Sistema",
                        action = dto.action ?: "Acción",
                        resource = dto.resource ?: "Recurso",
                        statusCode = dto.statusCode,
                        timestamp = dto.timestamp ?: ""
                    )
                }
                Result.success(logs)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al cargar historial de auditoría"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
