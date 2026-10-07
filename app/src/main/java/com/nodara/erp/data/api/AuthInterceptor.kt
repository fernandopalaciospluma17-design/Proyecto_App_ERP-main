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
        val requestBuilder = originalRequest.newBuilder()

        val token = sessionManager.getToken()
        val tenantId = sessionManager.getTenantId()

        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }
        if (!tenantId.isNullOrBlank()) {
            requestBuilder.header("X-Tenant-Id", tenantId)
            requestBuilder.header("x-tenant-id", tenantId)
        }

        val request = requestBuilder.build()
        val response = chain.proceed(request)

        if (response.code == 401 && !originalRequest.url.encodedPath.contains("/auth/login")) {
            sessionManager.clearSession()
            onUnauthorized()
        }

        return response
    }
}
