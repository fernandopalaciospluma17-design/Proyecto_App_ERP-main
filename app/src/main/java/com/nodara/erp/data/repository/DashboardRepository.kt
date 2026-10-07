package com.nodara.erp.data.repository

import com.nodara.erp.data.api.ApiService
import com.nodara.erp.domain.model.DashboardSummary
import com.nodara.erp.domain.model.RecentInvoice

class DashboardRepository(private val apiService: ApiService) {

    suspend fun getDashboardSummary(): Result<DashboardSummary> {
        return try {
            val response = apiService.getDashboardSummary()
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.data ?: return Result.failure(Exception("Datos vacíos"))
                val summary = DashboardSummary(
                    salesToday = dto.salesToday,
                    invoicesToday = dto.invoicesToday,
                    salesThisMonth = dto.salesThisMonth,
                    invoicesThisMonth = dto.invoicesThisMonth,
                    receivables = dto.receivables,
                    pendingInvoices = dto.pendingInvoices,
                    products = dto.products,
                    lowStock = dto.lowStock,
                    outOfStock = dto.outOfStock,
                    inventoryValue = dto.inventoryValue,
                    customers = dto.customers,
                    suppliers = dto.suppliers,
                    recentInvoices = dto.recentInvoices.map {
                        RecentInvoice(
                            id = it.realId,
                            number = it.number ?: "N/A",
                            customer = it.customerName,
                            total = it.total,
                            status = it.status ?: "pending",
                            issuedAt = it.issuedAt
                        )
                    }
                )
                Result.success(summary)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al cargar dashboard"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
