package com.nodara.erp.data.repository

import com.nodara.erp.data.api.ApiService
import com.nodara.erp.data.dto.CreatePurchaseItemDto
import com.nodara.erp.data.dto.CreatePurchaseRequestDto
import com.nodara.erp.data.dto.PurchaseDto
import com.nodara.erp.data.dto.UpdatePurchaseStatusRequestDto
import com.nodara.erp.domain.model.Purchase
import com.nodara.erp.domain.model.PurchaseItem

class PurchasesRepository(private val apiService: ApiService) {

    suspend fun getPurchases(): Result<List<Purchase>> {
        return try {
            val response = apiService.getPurchases()
            if (response.isSuccessful && response.body()?.success == true) {
                val items = response.body()?.data ?: emptyList()
                val purchases = items.map { mapPurchase(it) }
                Result.success(purchases)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al cargar compras"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPurchase(
        supplierId: String,
        expectedAt: String?,
        items: List<CreatePurchaseItemDto>
    ): Result<Purchase> {
        return try {
            val req = CreatePurchaseRequestDto(supplierId, expectedAt, items)
            val response = apiService.createPurchase(req)
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.data ?: return Result.failure(Exception("Respuesta vacía"))
                Result.success(mapPurchase(dto))
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al crear orden de compra"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePurchaseStatus(id: String, status: String): Result<Purchase> {
        return try {
            val response = apiService.updatePurchaseStatus(id, UpdatePurchaseStatusRequestDto(status))
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.data ?: return Result.failure(Exception("Respuesta vacía"))
                Result.success(mapPurchase(dto))
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al actualizar estado de compra"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapPurchase(dto: PurchaseDto): Purchase {
        return Purchase(
            id = dto.realId,
            number = dto.number ?: "OC-000",
            supplierId = dto.supplierId,
            supplierName = dto.supplierName,
            items = dto.items.map {
                PurchaseItem(
                    productId = it.productId ?: "",
                    sku = it.sku,
                    description = it.description,
                    quantity = it.quantity,
                    unitCost = it.unitCost
                )
            },
            total = dto.total,
            status = dto.status ?: "ordered",
            expectedAt = dto.expectedAt,
            receivedAt = dto.receivedAt,
            createdAt = dto.createdAt
        )
    }
}
