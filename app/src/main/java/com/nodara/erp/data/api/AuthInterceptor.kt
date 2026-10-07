package com.nodara.erp.data.api

import com.nodara.erp.data.local.SecureSessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SecureSessionManager,
    private val onUnauthorized: () -> Unit
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        val isPublicEndpoint = path.endsWith("/health") ||
                path.contains("/auth/login") ||
                path.contains("/auth/register") ||
                path.contains("/auth/resend-verification")

        val requestBuilder = originalRequest.newBuilder()

        if (!isPublicEndpoint) {
            val token = sessionManager.getToken()
            val tenantId = sessionManager.getTenantId()

            if (!token.isNullOrBlank()) {
                requestBuilder.header("Authorization", "Bearer $token")
            }
            if (!tenantId.isNullOrBlank()) {
                requestBuilder.header("X-Tenant-Id", tenantId)
                requestBuilder.header("x-tenant-id", tenantId)
            }
        }

        val request = requestBuilder.build()
        val response = chain.proceed(request)

        if (response.code == 401 && !isPublicEndpoint) {
            sessionManager.clearSession()
            onUnauthorized()
        }

        return response
    }
}
