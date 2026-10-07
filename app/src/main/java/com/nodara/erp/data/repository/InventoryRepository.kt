package com.nodara.erp.data.repository

import com.nodara.erp.data.api.ApiService
import com.nodara.erp.data.dto.CreateProductRequestDto
import com.nodara.erp.data.dto.ProductImageRequestDto
import com.nodara.erp.data.dto.StockAdjustmentRequestDto
import com.nodara.erp.data.dto.UpdateProductRequestDto
import com.nodara.erp.domain.model.Product

class InventoryRepository(private val apiService: ApiService) {

    suspend fun getProducts(page: Int = 1, limit: Int = 100, search: String? = null): Result<List<Product>> {
        return try {
            val response = apiService.getProducts(page, limit, search)
            if (response.isSuccessful && response.body()?.success == true) {
                val items = response.body()?.data ?: emptyList()
                val products = items.map { dto ->
                    Product(
                        id = dto.realId,
                        sku = dto.sku ?: "S/N",
                        barcode = dto.barcode ?: "S/N",
                        name = dto.name ?: "Producto sin nombre",
                        imageUrl = dto.imageUrl,
                        costo = dto.costo,
                        precio = dto.precio,
                        stockMinimo = dto.stockMinimo,
                        currentStock = dto.currentStock ?: 0
                    )
                }
                Result.success(products)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al cargar inventario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createProduct(
        sku: String,
        barcode: String,
        name: String,
        imageUrl: String?,
        costo: Double,
        precio: Double,
        stockMinimo: Int,
        initialStock: Int
    ): Result<Product> {
        return try {
            val req = CreateProductRequestDto(
                sku = sku,
                barcode = barcode,
                name = name,
                imageUrl = if (imageUrl.isNull_or_blank()) null else imageUrl,
                costo = costo,
                precio = precio,
                stockMinimo = stockMinimo,
                initialStock = initialStock
            )
            val response = apiService.createProduct(req)
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.data ?: return Result.failure(Exception("Respuesta vacía"))
                val p = Product(
                    id = dto.realId,
                    sku = dto.sku ?: sku,
                    barcode = dto.barcode ?: barcode,
                    name = dto.name ?: name,
                    imageUrl = dto.imageUrl,
                    costo = dto.costo,
                    precio = dto.precio,
                    stockMinimo = dto.stockMinimo,
                    currentStock = dto.currentStock ?: initialStock
                )
                Result.success(p)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al crear producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(
        id: String,
        sku: String?,
        barcode: String?,
        name: String?,
        imageUrl: String?,
        costo: Double?,
        precio: Double?,
        stockMinimo: Int?
    ): Result<Product> {
        return try {
            val req = UpdateProductRequestDto(
                sku = sku,
                barcode = barcode,
                name = name,
                imageUrl = imageUrl,
                costo = costo,
                precio = precio,
                stockMinimo = stockMinimo
            )
            val response = apiService.updateProduct(id, req)
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.data ?: return Result.failure(Exception("Respuesta vacía"))
                val p = Product(
                    id = dto.realId,
                    sku = dto.sku ?: (sku ?: ""),
                    barcode = dto.barcode ?: (barcode ?: ""),
                    name = dto.name ?: (name ?: ""),
                    imageUrl = dto.imageUrl,
                    costo = dto.costo,
                    precio = dto.precio,
                    stockMinimo = dto.stockMinimo,
                    currentStock = dto.currentStock ?: 0
                )
                Result.success(p)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al actualizar producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(id: String): Result<Boolean> {
        return try {
            val response = apiService.deleteProduct(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al eliminar producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun adjustStock(id: String, quantity: Int, reason: String): Result<Product> {
        return try {
            val req = StockAdjustmentRequestDto(quantity, reason)
            val response = apiService.adjustStock(id, req)
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.data ?: return Result.failure(Exception("Respuesta vacía"))
                val p = Product(
                    id = dto.realId,
                    sku = dto.sku ?: "S/N",
                    barcode = dto.barcode ?: "S/N",
                    name = dto.name ?: "Producto",
                    imageUrl = dto.imageUrl,
                    costo = dto.costo,
                    precio = dto.precio,
                    stockMinimo = dto.stockMinimo,
                    currentStock = dto.currentStock ?: 0
                )
                Result.success(p)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al ajustar stock"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadProductImage(id: String, imageDataBase64: String): Result<Product> {
        return try {
            val req = ProductImageRequestDto(imageDataBase64)
            val response = apiService.uploadProductImage(id, req)
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.data ?: return Result.failure(Exception("Respuesta vacía"))
                val p = Product(
                    id = dto.realId,
                    sku = dto.sku ?: "S/N",
                    barcode = dto.barcode ?: "S/N",
                    name = dto.name ?: "Producto",
                    imageUrl = dto.imageUrl,
                    costo = dto.costo,
                    precio = dto.precio,
                    stockMinimo = dto.stockMinimo,
                    currentStock = dto.currentStock ?: 0
                )
                Result.success(p)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al subir imagen"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun String?.isNull_or_blank(): Boolean = this == null || this.trim().isEmpty()
}
