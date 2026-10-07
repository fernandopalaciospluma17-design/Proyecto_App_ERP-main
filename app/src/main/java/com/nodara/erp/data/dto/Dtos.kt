package com.nodara.erp.data.dto

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class ApiResponseDto<T>(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("data") val data: T? = null,
    @SerializedName("error") val error: String? = null,
    @SerializedName("details") val details: List<ApiErrorDetailDto>? = null,
    @SerializedName("meta") val meta: PaginationMetaDto? = null
)

data class ApiErrorDetailDto(
    @SerializedName("field") val field: String? = null,
    @SerializedName("message") val message: String = ""
)

data class PaginationMetaDto(
    @SerializedName("page") val page: Int = 1,
    @SerializedName("limit") val limit: Int = 20,
    @SerializedName("total") val total: Int = 0,
    @SerializedName("totalPages") val totalPages: Int = 1
)

// Auth
data class UserDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("tenantId") val tenantId: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("roles") val roles: List<String> = emptyList()
)

data class AuthDataDto(
    @SerializedName("token") val token: String? = null,
    @SerializedName("user") val user: UserDto? = null
)

data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("companyName") val companyName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class ResendVerificationRequestDto(
    @SerializedName("email") val email: String
)

data class MessageResponseDto(
    @SerializedName("message") val message: String? = null
)

// Inventory
data class ProductDto(
    @SerializedName("_id") val mongoId: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("tenantId") val tenantId: String? = null,
    @SerializedName("sku") val sku: String? = null,
    @SerializedName("barcode") val barcode: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("costo") val costo: Double = 0.0,
    @SerializedName("precio") val precio: Double = 0.0,
    @SerializedName("stockMinimo") val stockMinimo: Int = 0,
    @SerializedName("currentStock") val currentStock: Int? = null
) {
    val realId: String
        get() = id ?: mongoId ?: ""
}

data class CreateProductRequestDto(
    @SerializedName("sku") val sku: String,
    @SerializedName("barcode") val barcode: String,
    @SerializedName("name") val name: String,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("costo") val costo: Double,
    @SerializedName("precio") val precio: Double,
    @SerializedName("stockMinimo") val stockMinimo: Int,
    @SerializedName("initialStock") val initialStock: Int = 0
)

data class UpdateProductRequestDto(
    @SerializedName("sku") val sku: String? = null,
    @SerializedName("barcode") val barcode: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("costo") val costo: Double? = null,
    @SerializedName("precio") val precio: Double? = null,
    @SerializedName("stockMinimo") val stockMinimo: Int? = null
)

data class StockAdjustmentRequestDto(
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("reason") val reason: String
)

data class ProductImageRequestDto(
    @SerializedName("imageData") val imageData: String
)

// Contacts
data class ContactDto(
    @SerializedName("_id") val mongoId: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("tenantId") val tenantId: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("type") val type: String? = "Cliente",
    @SerializedName("taxId") val taxId: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String? = null
) {
    val realId: String
        get() = id ?: mongoId ?: ""
}

data class CreateContactRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String = "Cliente",
    @SerializedName("taxId") val taxId: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String? = null
)

// Sales
data class CustomerSnapshotDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("taxId") val taxId: String? = null
)

data class InvoiceItemDto(
    @SerializedName("productId") val productId: String? = null,
    @SerializedName("sku") val sku: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("quantity") val quantity: Int = 1,
    @SerializedName("unitPrice") val unitPrice: Double = 0.0,
    @SerializedName("taxRate") val taxRate: Double = 0.0
)

data class CreateInvoiceItemDto(
    @SerializedName("productId") val productId: String,
    @SerializedName("sku") val sku: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("taxRate") val taxRate: Double = 0.0
)

data class InvoiceDto(
    @SerializedName("_id") val mongoId: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("tenantId") val tenantId: String? = null,
    @SerializedName("number") val number: String? = null,
    @SerializedName("customer") val customer: JsonElement? = null,
    @SerializedName("items") val items: List<InvoiceItemDto> = emptyList(),
    @SerializedName("subtotal") val subtotal: Double = 0.0,
    @SerializedName("impuestos") val impuestos: Double = 0.0,
    @SerializedName("total") val total: Double = 0.0,
    @SerializedName("issuedAt") val issuedAt: String? = null,
    @SerializedName("status") val status: String? = "pending",
    @SerializedName("paidAt") val paidAt: String? = null
) {
    val realId: String
        get() = id ?: mongoId ?: ""

    val customerName: String
        get() {
            if (customer == null || customer.isJsonNull) return "Cliente sin nombre"
            if (customer.isJsonPrimitive) return customer.asString
            if (customer.isJsonObject) {
                val obj = customer.asJsonObject
                if (obj.has("name") && !obj.get("name").isJsonNull) return obj.get("name").asString
            }
            return "Cliente"
        }

    val customerTaxId: String?
        get() {
            if (customer != null && customer.isJsonObject) {
                val obj = customer.asJsonObject
                if (obj.has("taxId") && !obj.get("taxId").isJsonNull) return obj.get("taxId").asString
            }
            return null
        }
}

data class CreateInvoiceRequestDto(
    @SerializedName("number") val number: String,
    @SerializedName("customer") val customer: CustomerSnapshotDto,
    @SerializedName("items") val items: List<CreateInvoiceItemDto>,
    @SerializedName("issuedAt") val issuedAt: String
)

data class UpdateInvoiceStatusRequestDto(
    @SerializedName("status") val status: String
)

data class DocumentLinkDto(
    @SerializedName("url") val url: String? = null,
    @SerializedName("expiresInSeconds") val expiresInSeconds: Int = 300
)

// Purchases
data class PurchaseItemDto(
    @SerializedName("productId") val productId: String? = null,
    @SerializedName("sku") val sku: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("quantity") val quantity: Int = 1,
    @SerializedName("unitCost") val unitCost: Double = 0.0
)

data class CreatePurchaseItemDto(
    @SerializedName("productId") val productId: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unitCost") val unitCost: Double
)

data class PurchaseDto(
    @SerializedName("_id") val mongoId: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("tenantId") val tenantId: String? = null,
    @SerializedName("number") val number: String? = null,
    @SerializedName("supplier") val supplier: JsonElement? = null,
    @SerializedName("items") val items: List<PurchaseItemDto> = emptyList(),
    @SerializedName("total") val total: Double = 0.0,
    @SerializedName("status") val status: String? = "ordered",
    @SerializedName("expectedAt") val expectedAt: String? = null,
    @SerializedName("receivedAt") val receivedAt: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
) {
    val realId: String
        get() = id ?: mongoId ?: ""

    val supplierName: String
        get() {
            if (supplier == null || supplier.isJsonNull) return "Proveedor sin nombre"
            if (supplier.isJsonPrimitive) return supplier.asString
            if (supplier.isJsonObject) {
                val obj = supplier.asJsonObject
                if (obj.has("name") && !obj.get("name").isJsonNull) return obj.get("name").asString
            }
            return "Proveedor"
        }

    val supplierId: String
        get() {
            if (supplier != null && supplier.isJsonObject) {
                val obj = supplier.asJsonObject
                if (obj.has("id") && !obj.get("id").isJsonNull) return obj.get("id").asString
            }
            return ""
        }
}

data class CreatePurchaseRequestDto(
    @SerializedName("supplierId") val supplierId: String,
    @SerializedName("expectedAt") val expectedAt: String? = null,
    @SerializedName("items") val items: List<CreatePurchaseItemDto>
)

data class UpdatePurchaseStatusRequestDto(
    @SerializedName("status") val status: String
)

// Finance
data class CashMovementDto(
    @SerializedName("_id") val mongoId: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("tenantId") val tenantId: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("concept") val concept: String? = null,
    @SerializedName("amount") val amount: Double = 0.0,
    @SerializedName("occurredAt") val occurredAt: String? = null,
    @SerializedName("createdBy") val createdBy: String? = null
) {
    val realId: String
        get() = id ?: mongoId ?: ""
}

data class FinanceSummaryDto(
    @SerializedName("income") val income: Double = 0.0,
    @SerializedName("expense") val expense: Double = 0.0,
    @SerializedName("balance") val balance: Double = 0.0
)

data class FinanceResponseDataDto(
    @SerializedName("items") val items: List<CashMovementDto> = emptyList(),
    @SerializedName("summary") val summary: FinanceSummaryDto? = FinanceSummaryDto()
)

data class CreateCashMovementRequestDto(
    @SerializedName("type") val type: String,
    @SerializedName("category") val category: String,
    @SerializedName("concept") val concept: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("occurredAt") val occurredAt: String
)

// Projects
data class ProjectDto(
    @SerializedName("_id") val mongoId: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("tenantId") val tenantId: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("client") val client: String? = "",
    @SerializedName("description") val description: String? = "",
    @SerializedName("status") val status: String? = "planned",
    @SerializedName("budget") val budget: Double = 0.0,
    @SerializedName("progress") val progress: Int = 0,
    @SerializedName("dueAt") val dueAt: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
) {
    val realId: String
        get() = id ?: mongoId ?: ""
}

data class CreateProjectRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("client") val client: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("status") val status: String = "planned",
    @SerializedName("budget") val budget: Double = 0.0,
    @SerializedName("progress") val progress: Int = 0,
    @SerializedName("dueAt") val dueAt: String? = null
)

// Team
data class TeamUserDto(
    @SerializedName("_id") val mongoId: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("roles") val roles: List<String> = emptyList(),
    @SerializedName("isActive") val isActive: Boolean = true,
    @SerializedName("createdAt") val createdAt: String? = null
) {
    val realId: String
        get() = id ?: mongoId ?: ""
}

data class CreateTeamUserRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("role") val role: String
)

data class UpdateTeamUserRequestDto(
    @SerializedName("role") val role: String? = null,
    @SerializedName("isActive") val isActive: Boolean? = null
)

// Activity
data class AuditLogDto(
    @SerializedName("_id") val mongoId: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("userName") val userName: String? = "Sistema",
    @SerializedName("action") val action: String? = null,
    @SerializedName("resource") val resource: String? = null,
    @SerializedName("statusCode") val statusCode: Int = 200,
    @SerializedName("timestamp") val timestamp: String? = null
) {
    val realId: String
        get() = id ?: mongoId ?: ""
}

// Dashboard
data class RecentInvoiceDto(
    @SerializedName("_id") val mongoId: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("number") val number: String? = null,
    @SerializedName("customer") val customer: JsonElement? = null,
    @SerializedName("total") val total: Double = 0.0,
    @SerializedName("status") val status: String? = "pending",
    @SerializedName("issuedAt") val issuedAt: String? = null
) {
    val realId: String
        get() = id ?: mongoId ?: ""

    val customerName: String
        get() {
            if (customer == null || customer.isJsonNull) return "Cliente"
            if (customer.isJsonPrimitive) return customer.asString
            if (customer.isJsonObject) {
                val obj = customer.asJsonObject
                if (obj.has("name") && !obj.get("name").isJsonNull) return obj.get("name").asString
            }
            return "Cliente"
        }
}

data class DashboardSummaryDto(
    @SerializedName("salesToday") val salesToday: Double = 0.0,
    @SerializedName("invoicesToday") val invoicesToday: Int = 0,
    @SerializedName("salesThisMonth") val salesThisMonth: Double = 0.0,
    @SerializedName("invoicesThisMonth") val invoicesThisMonth: Int = 0,
    @SerializedName("receivables") val receivables: Double = 0.0,
    @SerializedName("pendingInvoices") val pendingInvoices: Int = 0,
    @SerializedName("products") val products: Int = 0,
    @SerializedName("lowStock") val lowStock: Int = 0,
    @SerializedName("outOfStock") val outOfStock: Int = 0,
    @SerializedName("inventoryValue") val inventoryValue: Double = 0.0,
    @SerializedName("customers") val customers: Int = 0,
    @SerializedName("suppliers") val suppliers: Int = 0,
    @SerializedName("recentInvoices") val recentInvoices: List<RecentInvoiceDto> = emptyList()
)

data class SampleDataSummaryDto(
    @SerializedName("products") val products: Int = 0,
    @SerializedName("contacts") val contacts: Int = 0,
    @SerializedName("invoices") val invoices: Int = 0,
    @SerializedName("message") val message: String? = null
)

data class HealthStatusDto(
    @SerializedName("status") val status: String? = null,
    @SerializedName("service") val service: String? = null,
    @SerializedName("timestamp") val timestamp: String? = null
)
