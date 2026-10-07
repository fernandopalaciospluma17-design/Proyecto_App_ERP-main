package com.nodara.erp.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import com.nodara.erp.data.dto.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SecureSessionManager(context: Context) {

    private val gson = Gson()
    private val prefs: SharedPreferences = try {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            "nodara_secure_session",
            masterKeyAlias,
            context,
            PrefKeyEncryptionScheme.valueOf("AES256_SKEY"),
            PrefValueEncryptionScheme.valueOf("AES256_GCM")
        )
    } catch (t: Throwable) {
        context.getSharedPreferences("nodara_session_fallback", Context.MODE_PRIVATE)
    }

    private val _sessionState = MutableStateFlow<UserDto?>(getUser())
    val sessionState: StateFlow<UserDto?> = _sessionState.asStateFlow()

    fun saveSession(token: String, user: UserDto) {
        try {
            prefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_TENANT_ID, user.tenantId)
                .putString(KEY_USER_JSON, gson.toJson(user))
                .apply()
            _sessionState.value = user
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getToken(): String? {
        return try {
            prefs.getString(KEY_TOKEN, null)
        } catch (e: Exception) {
            null
        }
    }

    fun getTenantId(): String? {
        return try {
            prefs.getString(KEY_TENANT_ID, null)
        } catch (e: Exception) {
            null
        }
    }

    fun getUser(): UserDto? {
        return try {
            val json = prefs.getString(KEY_USER_JSON, null) ?: return null
            gson.fromJson(json, UserDto::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun clearSession() {
        try {
            prefs.edit().clear().apply()
            _sessionState.value = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_TENANT_ID = "tenant_id"
        private const val KEY_USER_JSON = "user_json"
    }
}
