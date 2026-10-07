package com.nodara.erp.data.api

import com.nodara.erp.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("health")
    suspend fun getHealth(): Response<ApiResponseDto<HealthStatusDto>>

    // Auth
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): Response<ApiResponseDto<AuthDataDto>>

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequestDto): Response<ApiResponseDto<MessageResponseDto>>

    @POST("api/auth/resend-verification")
    suspend fun resendVerification(@Body body: ResendVerificationRequestDto): Response<ApiResponseDto<MessageResponseDto>>

    @GET("api/auth/me")
    suspend fun getCurrentUser(): Response<ApiResponseDto<UserDto>>

    // Dashboard
    @GET("api/dashboard/summary")
    suspend fun getDashboardSummary(): Response<ApiResponseDto<DashboardSummaryDto>>

    // Inventory
    @GET("api/inventory/products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("search") search: String? = null
    ): Response<ApiResponseDto<List<ProductDto>>>

    @POST("api/inventory/products")
    suspend fun createProduct(@Body body: CreateProductRequestDto): Response<ApiResponseDto<ProductDto>>

    @PATCH("api/inventory/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: String,
        @Body body: UpdateProductRequestDto
    ): Response<ApiResponseDto<ProductDto>>

    @DELETE("api/inventory/products/{id}")
    suspend fun deleteProduct(@Path("id") id: String): Response<ApiResponseDto<ProductDto>>

    @POST("api/inventory/products/{id}/image")
    suspend fun uploadProductImage(
        @Path("id") id: String,
        @Body body: ProductImageRequestDto
    ): Response<ApiResponseDto<ProductDto>>

    @POST("api/inventory/products/{id}/adjust-stock")
    suspend fun adjustStock(
        @Path("id") id: String,
        @Body body: StockAdjustmentRequestDto
    ): Response<ApiResponseDto<ProductDto>>

    // Contacts
    @GET("api/contacts")
    suspend fun getContacts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("search") search: String? = null
    ): Response<ApiResponseDto<List<ContactDto>>>

    @POST("api/contacts")
    suspend fun createContact(@Body body: CreateContactRequestDto): Response<ApiResponseDto<ContactDto>>

    @PATCH("api/contacts/{id}")
    suspend fun updateContact(
        @Path("id") id: String,
        @Body body: CreateContactRequestDto
    ): Response<ApiResponseDto<ContactDto>>

    @DELETE("api/contacts/{id}")
    suspend fun deleteContact(@Path("id") id: String): Response<ApiResponseDto<ContactDto>>

    // Sales
    @GET("api/sales/invoices")
    suspend fun getInvoices(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("search") search: String? = null,
        @Query("sortBy") sortBy: String? = "issuedAt",
        @Query("sortOrder") sortOrder: String? = "desc"
    ): Response<ApiResponseDto<List<InvoiceDto>>>

    @POST("api/sales/invoices")
    suspend fun createInvoice(@Body body: CreateInvoiceRequestDto): Response<ApiResponseDto<InvoiceDto>>

    @GET("api/sales/invoices/{id}")
    suspend fun getInvoiceDetail(@Path("id") id: String): Response<ApiResponseDto<InvoiceDto>>

    @PATCH("api/sales/invoices/{id}/status")
    suspend fun updateInvoiceStatus(
        @Path("id") id: String,
        @Body body: UpdateInvoiceStatusRequestDto
    ): Response<ApiResponseDto<InvoiceDto>>

    @POST("api/sales/invoices/{id}/document-link")
    suspend fun getInvoiceDocumentLink(@Path("id") id: String): Response<ApiResponseDto<DocumentLinkDto>>

    // Purchases
    @GET("api/purchases")
    suspend fun getPurchases(): Response<ApiResponseDto<List<PurchaseDto>>>

    @POST("api/purchases")
    suspend fun createPurchase(@Body body: CreatePurchaseRequestDto): Response<ApiResponseDto<PurchaseDto>>

    @PATCH("api/purchases/{id}/status")
    suspend fun updatePurchaseStatus(
        @Path("id") id: String,
        @Body body: UpdatePurchaseStatusRequestDto
    ): Response<ApiResponseDto<PurchaseDto>>

    // Finance
    @GET("api/finance")
    suspend fun getFinanceMovements(): Response<ApiResponseDto<FinanceResponseDataDto>>

    @POST("api/finance")
    suspend fun createCashMovement(@Body body: CreateCashMovementRequestDto): Response<ApiResponseDto<CashMovementDto>>

    // Projects
    @GET("api/projects")
    suspend fun getProjects(): Response<ApiResponseDto<List<ProjectDto>>>

    @POST("api/projects")
    suspend fun createProject(@Body body: CreateProjectRequestDto): Response<ApiResponseDto<ProjectDto>>

    @PATCH("api/projects/{id}")
    suspend fun updateProject(
        @Path("id") id: String,
        @Body body: CreateProjectRequestDto
    ): Response<ApiResponseDto<ProjectDto>>

    // Team
    @GET("api/team")
    suspend fun getTeamMembers(): Response<ApiResponseDto<List<TeamUserDto>>>

    @POST("api/team")
    suspend fun createTeamMember(@Body body: CreateTeamUserRequestDto): Response<ApiResponseDto<TeamUserDto>>

    @PATCH("api/team/{id}")
    suspend fun updateTeamMember(
        @Path("id") id: String,
        @Body body: UpdateTeamUserRequestDto
    ): Response<ApiResponseDto<TeamUserDto>>

    // Activity
    @GET("api/activity")
    suspend fun getActivityLogs(): Response<ApiResponseDto<List<AuditLogDto>>>

    // Setup / Initial Data
    @POST("api/setup/sample-data")
    suspend fun seedSampleData(): Response<ApiResponseDto<SampleDataSummaryDto>>
}
