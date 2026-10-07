package com.nodara.erp.presentation.team

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nodara.erp.domain.model.ResourceState
import com.nodara.erp.domain.model.TeamUser
import com.nodara.erp.presentation.components.*
import com.nodara.erp.presentation.theme.*

@Composable
fun TeamScreen(viewModel: TeamViewModel) {
    val teamState by viewModel.teamState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
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
                Icon(Icons.Default.PersonAdd, contentDescription = "Nuevo Miembro")
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
                    text = "Equipo y Permisos",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NodaraInk
                )
                IconButton(onClick = { viewModel.loadTeam() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = NodaraSignature)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = teamState) {
                is ResourceState.Loading -> LoadingState("Cargando usuarios del equipo...")
                is ResourceState.Error -> ErrorState(state.message, onRetry = { viewModel.loadTeam() })
                is ResourceState.Success -> {
                    if (state.data.isEmpty()) {
                        EmptyState(
                            title = "Sin usuarios registrados",
                            subtitle = "Agrega colaboradores a tu organización.",
                            actionText = "Agregar Usuario",
                            onAction = { showCreateDialog = true }
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.data) { user ->
                                TeamUserCard(
                                    user = user,
                                    onToggleActive = { viewModel.toggleActive(user.id, user.isActive) }
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
        CreateTeamUserDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, email, password, role -> viewModel.createMember(name, email, password, role) }
        )
    }
}

@Composable
private fun TeamUserCard(
    user: TeamUser,
    onToggleActive: () -> Unit
) {
    NodaraCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NodaraInk)
                Text(text = user.email, fontSize = 13.sp, color = NodaraSlate)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    user.roles.forEach { role ->
                        StatusBadge(status = role)
                    }
                }
            }
            Switch(
                checked = user.isActive,
                onCheckedChange = { onToggleActive() },
                colors = SwitchDefaults.colors(checkedThumbColor = NodaraSignature)
            )
        }
    }
}

@Composable
private fun CreateTeamUserDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, password: String, role: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("sales") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Usuario", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NodaraTextField(value = name, onValueChange = { name = it }, label = "Nombre completo")
                NodaraTextField(value = email, onValueChange = { email = it }, label = "Correo electrónico")
                NodaraTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                Text("Rol:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NodaraSlate)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(selected = role == "sales", onClick = { role = "sales" }, label = { Text("Ventas", fontSize = 11.sp) })
                    FilterChip(selected = role == "accounting", onClick = { role = "accounting" }, label = { Text("Contabilidad", fontSize = 11.sp) })
                    FilterChip(selected = role == "purchasing", onClick = { role = "purchasing" }, label = { Text("Compras", fontSize = 11.sp) })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank() && password.length >= 10) {
                        onConfirm(name, email, password, role)
                    }
                },
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
