package com.nodara.erp.data.repository

import com.nodara.erp.data.api.ApiService
import com.nodara.erp.data.dto.SampleDataSummaryDto

class ReportsRepository(private val apiService: ApiService) {

    suspend fun seedSampleData(): Result<SampleDataSummaryDto> {
        return try {
            val response = apiService.seedSampleData()
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data ?: SampleDataSummaryDto()
                Result.success(data)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al cargar datos de muestra"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHealth(): Result<Boolean> {
        return try {
            val response = apiService.getHealth()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data?.status == "ok")
            } else {
                Result.success(false)
            }
        } catch (e: Exception) {
            Result.success(false)
        }
    }
}
