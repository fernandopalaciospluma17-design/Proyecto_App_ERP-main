package com.nodara.erp.data.repository

import com.nodara.erp.data.api.ApiService
import com.nodara.erp.data.dto.LoginRequestDto
import com.nodara.erp.data.dto.RegisterRequestDto
import com.nodara.erp.data.dto.ResendVerificationRequestDto
import com.nodara.erp.data.local.SecureSessionManager
import com.nodara.erp.domain.model.User

class AuthRepository(
    private val apiService: ApiService,
    private val sessionManager: SecureSessionManager
) {

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = apiService.login(LoginRequestDto(email, password))
            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()?.data ?: return Result.failure(Exception("Respuesta vacía del servidor"))
                val userDto = authData.user ?: return Result.failure(Exception("Datos de usuario vacíos"))
                val token = authData.token ?: return Result.failure(Exception("Token de sesión ausente"))
                sessionManager.saveSession(token, userDto)
                Result.success(
                    User(
                        id = userDto.id ?: "",
                        tenantId = userDto.tenantId ?: "",
                        email = userDto.email ?: email,
                        name = userDto.name ?: "Usuario",
                        roles = userDto.roles
                    )
                )
            } else {
                val errorMsg = response.body()?.error ?: response.errorBody()?.string() ?: "Error de autenticación"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, companyName: String, email: String, password: String): Result<String> {
        return try {
            val response = apiService.register(RegisterRequestDto(name, companyName, email, password))
            if (response.isSuccessful && response.body()?.success == true) {
                val msg = response.body()?.data?.message ?: "Cuenta creada exitosamente"
                Result.success(msg)
            } else {
                val errorMsg = response.body()?.error ?: "No se pudo registrar la cuenta"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resendVerification(email: String): Result<String> {
        return try {
            val response = apiService.resendVerification(ResendVerificationRequestDto(email))
            if (response.isSuccessful && response.body()?.success == true) {
                val msg = response.body()?.data?.message ?: "Correo de verificación reenviado"
                Result.success(msg)
            } else {
                val errorMsg = response.body()?.error ?: "Error al reenviar verificación"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): Result<User> {
        return try {
            val response = apiService.getCurrentUser()
            if (response.isSuccessful && response.body()?.success == true) {
                val userDto = response.body()?.data ?: return Result.failure(Exception("Usuario no encontrado"))
                val user = User(
                    id = userDto.id ?: "",
                    tenantId = userDto.tenantId ?: "",
                    email = userDto.email ?: "",
                    name = userDto.name ?: "Usuario",
                    roles = userDto.roles
                )
                Result.success(user)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Sesión expirada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }
}
