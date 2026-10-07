package com.nodara.erp.domain.model

data class User(
    val id: String,
    val tenantId: String,
    val email: String,
    val name: String,
    val roles: List<String>
)

data class Product(
    val id: String,
    val sku: String,
    val barcode: String,
    val name: String,
    val imageUrl: String?,
    val costo: Double,
    val precio: Double,
    val stockMinimo: Int,
    val currentStock: Int
)

data class Contact(
    val id: String,
    val name: String,
    val type: String,
    val taxId: String?,
    val email: String?,
    val phone: String?
)

data class InvoiceItem(
    val productId: String,
    val sku: String,
    val description: String,
    val quantity: Int,
    val unitPrice: Double,
    val taxRate: Double
)

data class Invoice(
    val id: String,
    val number: String,
    val customerName: String,
    val customerTaxId: String?,
    val items: List<InvoiceItem>,
    val subtotal: Double,
    val impuestos: Double,
    val total: Double,
    val issuedAt: String?,
    val status: String,
    val paidAt: String?
)

data class PurchaseItem(
    val productId: String,
    val sku: String?,
    val description: String?,
    val quantity: Int,
    val unitCost: Double
)

data class Purchase(
    val id: String,
    val number: String,
    val supplierId: String,
    val supplierName: String,
    val items: List<PurchaseItem>,
    val total: Double,
    val status: String,
    val expectedAt: String?,
    val receivedAt: String?,
    val createdAt: String?
)

data class CashMovement(
    val id: String,
    val type: String,
    val category: String,
    val concept: String,
    val amount: Double,
    val occurredAt: String?,
    val createdBy: String?
)

data class FinanceSummary(
    val income: Double,
    val expense: Double,
    val balance: Double
)

data class Project(
    val id: String,
    val name: String,
    val client: String,
    val description: String,
    val status: String,
    val budget: Double,
    val progress: Int,
    val dueAt: String?
)

data class TeamUser(
    val id: String,
    val name: String,
    val email: String,
    val roles: List<String>,
    val isActive: Boolean,
    val createdAt: String?
)

data class AuditLog(
    val id: String,
    val userId: String,
    val userName: String,
    val action: String,
    val resource: String,
    val statusCode: Int,
    val timestamp: String
)

data class DashboardSummary(
    val salesToday: Double,
    val invoicesToday: Int,
    val salesThisMonth: Double,
    val invoicesThisMonth: Int,
    val receivables: Double,
    val pendingInvoices: Int,
    val products: Int,
    val lowStock: Int,
    val outOfStock: Int,
    val inventoryValue: Double,
    val customers: Int,
    val suppliers: Int,
    val recentInvoices: List<RecentInvoice>
)

data class RecentInvoice(
    val id: String,
    val number: String,
    val customer: String,
    val total: Double,
    val status: String,
    val issuedAt: String?
)

sealed class ResourceState<out T> {
    object Idle : ResourceState<Nothing>()
    object Loading : ResourceState<Nothing>()
    data class Success<T>(val data: T) : ResourceState<T>()
    data class Error(val message: String) : ResourceState<Nothing>()
}
