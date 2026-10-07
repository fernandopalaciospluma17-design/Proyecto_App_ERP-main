package com.nodara.erp.data.repository

import com.nodara.erp.data.api.ApiService
import com.nodara.erp.data.dto.*
import com.nodara.erp.domain.model.Invoice
import com.nodara.erp.domain.model.InvoiceItem

class SalesRepository(private val apiService: ApiService) {

    suspend fun getInvoices(search: String? = null): Result<List<Invoice>> {
        return try {
            val response = apiService.getInvoices(search = search)
            if (response.isSuccessful && response.body()?.success == true) {
                val dtos = response.body()?.data ?: emptyList()
                val invoices = dtos.map { mapInvoice(it) }
                Result.success(invoices)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al cargar facturas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createInvoice(
        number: String,
        customerId: String,
        customerName: String,
        customerTaxId: String?,
        items: List<CreateInvoiceItemDto>,
        issuedAt: String
    ): Result<Invoice> {
        return try {
            val req = CreateInvoiceRequestDto(
                number = number,
                customer = CustomerSnapshotDto(id = customerId, name = customerName, taxId = customerTaxId),
                items = items,
                issuedAt = issuedAt
            )
            val response = apiService.createInvoice(req)
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.data ?: return Result.failure(Exception("Respuesta vacía"))
                Result.success(mapInvoice(dto))
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al crear factura"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateInvoiceStatus(id: String, status: String): Result<Invoice> {
        return try {
            val response = apiService.updateInvoiceStatus(id, UpdateInvoiceStatusRequestDto(status))
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.data ?: return Result.failure(Exception("Respuesta vacía"))
                Result.success(mapInvoice(dto))
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al cambiar estado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDocumentLink(id: String): Result<String> {
        return try {
            val response = apiService.getInvoiceDocumentLink(id)
            if (response.isSuccessful && response.body()?.success == true) {
                val url = response.body()?.data?.url ?: return Result.failure(Exception("URL vacía"))
                Result.success(url)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al obtener documento PDF"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapInvoice(dto: InvoiceDto): Invoice {
        return Invoice(
            id = dto.realId,
            number = dto.number ?: "FAC-000",
            customerName = dto.customerName,
            customerTaxId = dto.customerTaxId,
            items = dto.items.map {
                InvoiceItem(
                    productId = it.productId ?: "",
                    sku = it.sku ?: "",
                    description = it.description ?: "",
                    quantity = it.quantity,
                    unitPrice = it.unitPrice,
                    taxRate = it.taxRate
                )
            },
            subtotal = dto.subtotal,
            impuestos = dto.impuestos,
            total = dto.total,
            issuedAt = dto.issuedAt,
            status = dto.status ?: "pending",
            paidAt = dto.paidAt
        )
    }
}
