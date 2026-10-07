package com.nodara.erp.data.repository

import com.nodara.erp.data.api.ApiService
import com.nodara.erp.data.dto.CreateCashMovementRequestDto
import com.nodara.erp.domain.model.CashMovement
import com.nodara.erp.domain.model.FinanceSummary

class FinanceRepository(private val apiService: ApiService) {

    suspend fun getFinanceData(): Result<Pair<List<CashMovement>, FinanceSummary>> {
        return try {
            val response = apiService.getFinanceMovements()
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data ?: return Result.failure(Exception("Respuesta vacía"))
                val movements = data.items.map { dto ->
                    CashMovement(
                        id = dto.realId,
                        type = dto.type ?: "income",
                        category = dto.category ?: "General",
                        concept = dto.concept ?: "Sin concepto",
                        amount = dto.amount,
                        occurredAt = dto.occurredAt,
                        createdBy = dto.createdBy
                    )
                }
                val summaryData = data.summary
                val summary = FinanceSummary(
                    income = summaryData?.income ?: 0.0,
                    expense = summaryData?.expense ?: 0.0,
                    balance = summaryData?.balance ?: 0.0
                )
                Result.success(Pair(movements, summary))
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al cargar datos financieros"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createCashMovement(
        type: String,
        category: String,
        concept: String,
        amount: Double,
        occurredAt: String
    ): Result<CashMovement> {
        return try {
            val req = CreateCashMovementRequestDto(type, category, concept, amount, occurredAt)
            val response = apiService.createCashMovement(req)
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.data ?: return Result.failure(Exception("Respuesta vacía"))
                Result.success(
                    CashMovement(
                        id = dto.realId,
                        type = dto.type ?: type,
                        category = dto.category ?: category,
                        concept = dto.concept ?: concept,
                        amount = dto.amount,
                        occurredAt = dto.occurredAt,
                        createdBy = dto.createdBy
                    )
                )
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al registrar movimiento"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
