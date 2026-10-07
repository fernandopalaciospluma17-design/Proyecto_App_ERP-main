package com.nodara.erp.presentation.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nodara.erp.domain.model.Project
import com.nodara.erp.domain.model.ResourceState
import com.nodara.erp.presentation.components.*
import com.nodara.erp.presentation.theme.*

@Composable
fun ProjectsScreen(viewModel: ProjectsViewModel) {
    val projectsState by viewModel.projectsState.collectAsState()
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
                Icon(Icons.Default.Add, contentDescription = "Nuevo Proyecto")
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
                    text = "Proyectos y Obras",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NodaraInk
                )
                IconButton(onClick = { viewModel.loadProjects() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = NodaraSignature)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = projectsState) {
                is ResourceState.Loading -> LoadingState("Cargando proyectos...")
                is ResourceState.Error -> ErrorState(state.message, onRetry = { viewModel.loadProjects() })
                is ResourceState.Success -> {
                    if (state.data.isEmpty()) {
                        EmptyState(
                            title = "Sin proyectos registrados",
                            subtitle = "Crea tu primer proyecto u obra.",
                            actionText = "Crear Proyecto",
                            onAction = { showCreateDialog = true }
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.data) { project ->
                                ProjectCard(project)
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }

    if (showCreateDialog) {
        CreateProjectDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, client, desc, budget, progress ->
                viewModel.createProject(name, client, desc, "active", budget, progress)
            }
        )
    }
}

@Composable
private fun ProjectCard(project: Project) {
    NodaraCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = project.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NodaraInk)
                    if (project.client.isNotBlank()) {
                        Text(text = "Cliente: ${project.client}", fontSize = 12.sp, color = NodaraSlate)
                    }
                }
                StatusBadge(status = project.status)
            }

            if (project.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = project.description, fontSize = 12.sp, color = NodaraMoss)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Avance: ${project.progress}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NodaraInk)
                Text(text = "Presupuesto: $${String.format("%.2f", project.budget)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NodaraSuccess)
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { project.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = NodaraSignature,
                trackColor = NodaraLine
            )
        }
    }
}

@Composable
private fun CreateProjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, client: String, desc: String, budget: Double, progress: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var client by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("50000.00") }
    var progress by remember { mutableStateOf("10") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Proyecto", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NodaraTextField(value = name, onValueChange = { name = it }, label = "Nombre del proyecto")
                NodaraTextField(value = client, onValueChange = { client = it }, label = "Cliente / Entidad")
                NodaraTextField(value = desc, onValueChange = { desc = it }, label = "Descripción")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NodaraTextField(value = budget, onValueChange = { budget = it }, label = "Presupuesto ($)", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    NodaraTextField(value = progress, onValueChange = { progress = it }, label = "Avance (%)", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, client, desc, budget.toDoubleOrNull() ?: 0.0, progress.toIntOrNull() ?: 0)
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
