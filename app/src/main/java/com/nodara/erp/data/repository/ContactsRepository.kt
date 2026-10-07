package com.nodara.erp.data.repository

import com.nodara.erp.data.api.ApiService
import com.nodara.erp.data.dto.CreateContactRequestDto
import com.nodara.erp.domain.model.Contact

class ContactsRepository(private val apiService: ApiService) {

    suspend fun getContacts(search: String? = null): Result<List<Contact>> {
        return try {
            val response = apiService.getContacts(search = search)
            if (response.isSuccessful && response.body()?.success == true) {
                val items = response.body()?.data ?: emptyList()
                val contacts = items.map { dto ->
                    Contact(
                        id = dto.realId,
                        name = dto.name ?: "Contacto sin nombre",
                        type = dto.type ?: "Cliente",
                        taxId = dto.taxId,
                        email = dto.email,
                        phone = dto.phone
                    )
                }
                Result.success(contacts)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al cargar contactos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createContact(
        name: String,
        type: String,
        taxId: String?,
        email: String?,
        phone: String?
    ): Result<Contact> {
        return try {
            val req = CreateContactRequestDto(name, type, taxId, email, phone)
            val response = apiService.createContact(req)
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.data ?: return Result.failure(Exception("Respuesta vacía"))
                Result.success(
                    Contact(
                        id = dto.realId,
                        name = dto.name ?: name,
                        type = dto.type ?: type,
                        taxId = dto.taxId ?: taxId,
                        email = dto.email ?: email,
                        phone = dto.phone ?: phone
                    )
                )
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al crear contacto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateContact(
        id: String,
        name: String,
        type: String,
        taxId: String?,
        email: String?,
        phone: String?
    ): Result<Contact> {
        return try {
            val req = CreateContactRequestDto(name, type, taxId, email, phone)
            val response = apiService.updateContact(id, req)
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.data ?: return Result.failure(Exception("Respuesta vacía"))
                Result.success(
                    Contact(
                        id = dto.realId,
                        name = dto.name ?: name,
                        type = dto.type ?: type,
                        taxId = dto.taxId ?: taxId,
                        email = dto.email ?: email,
                        phone = dto.phone ?: phone
                    )
                )
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al actualizar contacto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteContact(id: String): Result<Boolean> {
        return try {
            val response = apiService.deleteContact(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Error al eliminar contacto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
