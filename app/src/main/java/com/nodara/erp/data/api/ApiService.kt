package com.nodara.erp.data.api

import com.nodara.erp.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("../health")
    suspend fun getHealth(): Response<ApiResponseDto<HealthStatusDto>>

    // Auth
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequestDto): Response<ApiResponseDto<AuthDataDto>>

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequestDto): Response<ApiResponseDto<MessageResponseDto>>

    @POST("auth/resend-verification")
    suspend fun resendVerification(@Body body: ResendVerificationRequestDto): Response<ApiResponseDto<MessageResponseDto>>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<ApiResponseDto<UserDto>>

    // Dashboard
    @GET("dashboard/summary")
    suspend fun getDashboardSummary(): Response<ApiResponseDto<DashboardSummaryDto>>

    // Inventory
    @GET("inventory/products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("search") search: String? = null
    ): Response<ApiResponseDto<List<ProductDto>>>

    @POST("inventory/products")
    suspend fun createProduct(@Body body: CreateProductRequestDto): Response<ApiResponseDto<ProductDto>>

    @PATCH("inventory/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: String,
        @Body body: UpdateProductRequestDto
    ): Response<ApiResponseDto<ProductDto>>

    @DELETE("inventory/products/{id}")
    suspend fun deleteProduct(@Path("id") id: String): Response<ApiResponseDto<ProductDto>>

    @POST("inventory/products/{id}/image")
    suspend fun uploadProductImage(
        @Path("id") id: String,
        @Body body: ProductImageRequestDto
    ): Response<ApiResponseDto<ProductDto>>

    @POST("inventory/products/{id}/adjust-stock")
    suspend fun adjustStock(
        @Path("id") id: String,
        @Body body: StockAdjustmentRequestDto
    ): Response<ApiResponseDto<ProductDto>>

    // Contacts
    @GET("contacts")
    suspend fun getContacts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("search") search: String? = null
    ): Response<ApiResponseDto<List<ContactDto>>>

    @POST("contacts")
    suspend fun createContact(@Body body: CreateContactRequestDto): Response<ApiResponseDto<ContactDto>>

    @PATCH("contacts/{id}")
    suspend fun updateContact(
        @Path("id") id: String,
        @Body body: CreateContactRequestDto
    ): Response<ApiResponseDto<ContactDto>>

    @DELETE("contacts/{id}")
    suspend fun deleteContact(@Path("id") id: String): Response<ApiResponseDto<ContactDto>>

    // Sales
    @GET("sales/invoices")
    suspend fun getInvoices(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("search") search: String? = null,
        @Query("sortBy") sortBy: String? = "issuedAt",
        @Query("sortOrder") sortOrder: String? = "desc"
    ): Response<ApiResponseDto<List<InvoiceDto>>>

    @POST("sales/invoices")
    suspend fun createInvoice(@Body body: CreateInvoiceRequestDto): Response<ApiResponseDto<InvoiceDto>>

    @GET("sales/invoices/{id}")
    suspend fun getInvoiceDetail(@Path("id") id: String): Response<ApiResponseDto<InvoiceDto>>

    @PATCH("sales/invoices/{id}/status")
    suspend fun updateInvoiceStatus(
        @Path("id") id: String,
        @Body body: UpdateInvoiceStatusRequestDto
    ): Response<ApiResponseDto<InvoiceDto>>

    @POST("sales/invoices/{id}/document-link")
    suspend fun getInvoiceDocumentLink(@Path("id") id: String): Response<ApiResponseDto<DocumentLinkDto>>

    // Purchases
    @GET("purchases")
    suspend fun getPurchases(): Response<ApiResponseDto<List<PurchaseDto>>>

    @POST("purchases")
    suspend fun createPurchase(@Body body: CreatePurchaseRequestDto): Response<ApiResponseDto<PurchaseDto>>

    @PATCH("purchases/{id}/status")
    suspend fun updatePurchaseStatus(
        @Path("id") id: String,
        @Body body: UpdatePurchaseStatusRequestDto
    ): Response<ApiResponseDto<PurchaseDto>>

    // Finance
    @GET("finance")
    suspend fun getFinanceMovements(): Response<ApiResponseDto<FinanceResponseDataDto>>

    @POST("finance")
    suspend fun createCashMovement(@Body body: CreateCashMovementRequestDto): Response<ApiResponseDto<CashMovementDto>>

    // Projects
    @GET("projects")
    suspend fun getProjects(): Response<ApiResponseDto<List<ProjectDto>>>

    @POST("projects")
    suspend fun createProject(@Body body: CreateProjectRequestDto): Response<ApiResponseDto<ProjectDto>>

    @PATCH("projects/{id}")
    suspend fun updateProject(
        @Path("id") id: String,
        @Body body: CreateProjectRequestDto
    ): Response<ApiResponseDto<ProjectDto>>

    // Team
    @GET("team")
    suspend fun getTeamMembers(): Response<ApiResponseDto<List<TeamUserDto>>>

    @POST("team")
    suspend fun createTeamMember(@Body body: CreateTeamUserRequestDto): Response<ApiResponseDto<TeamUserDto>>

    @PATCH("team/{id}")
    suspend fun updateTeamMember(
        @Path("id") id: String,
        @Body body: UpdateTeamUserRequestDto
    ): Response<ApiResponseDto<TeamUserDto>>

    // Activity
    @GET("activity")
    suspend fun getActivityLogs(): Response<ApiResponseDto<List<AuditLogDto>>>

    // Setup / Initial Data
    @POST("setup/sample-data")
    suspend fun seedSampleData(): Response<ApiResponseDto<SampleDataSummaryDto>>
}
