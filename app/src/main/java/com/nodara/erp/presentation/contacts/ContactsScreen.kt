package com.nodara.erp.presentation.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nodara.erp.domain.model.Contact
import com.nodara.erp.domain.model.ResourceState
import com.nodara.erp.presentation.components.*
import com.nodara.erp.presentation.theme.*

@Composable
fun ContactsScreen(viewModel: ContactsViewModel) {
    val contactsState by viewModel.contactsState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(actionState) {
        if (actionState is ResourceState.Success) {
            snackbarHostState.showSnackbar((actionState as ResourceState.Success).data)
            viewModel.resetActionState()
            showCreateDialog = false
        } else if (actionState is ResourceState.Error) {
            snackbarHostState.showSnackbar((actionState as ResourceState.Error).message)
            viewModel.resetActionState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = NodaraSignature,
                contentColor = NodaraPaper
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Contacto")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(NodaraMist)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Contactos (Clientes y Proveedores)",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NodaraInk
                )
                IconButton(onClick = { viewModel.loadContacts() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = NodaraSignature)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            NodaraTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.loadContacts(it.ifBlank { null })
                },
                label = "",
                placeholder = "Buscar por nombre o RFC/TaxId...",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = contactsState) {
                is ResourceState.Loading -> LoadingState("Cargando contactos...")
                is ResourceState.Error -> ErrorState(state.message, onRetry = { viewModel.loadContacts() })
                is ResourceState.Success -> {
                    if (state.data.isEmpty()) {
                        EmptyState(
                            title = "Sin contactos registrados",
                            subtitle = "Agrega tu primer cliente o proveedor.",
                            actionText = "Crear Contacto",
                            onAction = { showCreateDialog = true }
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.data) { contact ->
                                ContactCard(
                                    contact = contact,
                                    onDelete = { viewModel.deleteContact(contact.id) }
                                )
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }

    if (showCreateDialog) {
        CreateContactDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, type, taxId, email, phone ->
                viewModel.createContact(name, type, taxId, email, phone)
            }
        )
    }
}

@Composable
private fun ContactCard(
    contact: Contact,
    onDelete: () -> Unit
) {
    NodaraCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = contact.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NodaraInk)
                Text(text = "Tipo: ${contact.type} | RFC/Tax: ${contact.taxId ?: "N/A"}", fontSize = 12.sp, color = NodaraSlate)
                if (!contact.email.isNull_or_blank() || !contact.phone.isNull_or_blank()) {
                    Text(text = "Email: ${contact.email ?: "-"} | Tel: ${contact.phone ?: "-"}", fontSize = 12.sp, color = NodaraMoss)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = NodaraDanger)
            }
        }
    }
}

@Composable
private fun CreateContactDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: String, taxId: String?, email: String?, phone: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Cliente") }
    var taxId by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Contacto", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NodaraTextField(value = name, onValueChange = { name = it }, label = "Nombre o Razón Social")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = type == "Cliente", onClick = { type = "Cliente" }, label = { Text("Cliente") })
                    FilterChip(selected = type == "Proveedor", onClick = { type = "Proveedor" }, label = { Text("Proveedor") })
                }
                NodaraTextField(value = taxId, onValueChange = { taxId = it }, label = "RFC / Tax ID")
                NodaraTextField(value = email, onValueChange = { email = it }, label = "Correo electrónico")
                NodaraTextField(value = phone, onValueChange = { phone = it }, label = "Teléfono")
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, type, taxId.ifBlank { null }, email.ifBlank { null }, phone.ifBlank { null }) },
                colors = ButtonDefaults.buttonColors(containerColor = NodaraSignature)
            ) {
                Text("Guardar", color = NodaraPaper)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

private fun String?.isNull_or_blank(): Boolean = this == null || this.trim().isEmpty()
