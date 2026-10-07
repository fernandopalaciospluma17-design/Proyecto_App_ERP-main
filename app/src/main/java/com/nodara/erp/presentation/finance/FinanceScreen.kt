package com.nodara.erp.presentation.finance

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
import com.nodara.erp.domain.model.CashMovement
import com.nodara.erp.domain.model.FinanceSummary
import com.nodara.erp.domain.model.ResourceState
import com.nodara.erp.presentation.components.*
import com.nodara.erp.presentation.theme.*

@Composable
fun FinanceScreen(viewModel: FinanceViewModel) {
    val financeState by viewModel.financeState.collectAsState()
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
                Icon(Icons.Default.Add, contentDescription = "Nuevo Movimiento")
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
                    text = "Movimientos Financieros",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NodaraInk
                )
                IconButton(onClick = { viewModel.loadFinanceData() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = NodaraSignature)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = financeState) {
                is ResourceState.Loading -> LoadingState("Cargando flujo de caja...")
                is ResourceState.Error -> ErrorState(state.message, onRetry = { viewModel.loadFinanceData() })
                is ResourceState.Success -> {
                    val (movements, summary) = state.data
                    FinanceContent(
                        movements = movements,
                        summary = summary,
                        onCreateClick = { showCreateDialog = true }
                    )
                }
                else -> {}
            }
        }
    }

    if (showCreateDialog) {
        CreateMovementDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { type, category, concept, amount ->
                val isoDate = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).format(java.util.Date())
                viewModel.createCashMovement(type, category, concept, amount, isoDate)
            }
        )
    }
}

@Composable
private fun FinanceContent(
    movements: List<CashMovement>,
    summary: FinanceSummary,
    onCreateClick: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    title = "Ingresos",
                    value = "$${String.format("%.2f", summary.income)}",
                    icon = Icons.Default.TrendingUp,
                    iconTint = NodaraSuccess,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Gastos",
                    value = "$${String.format("%.2f", summary.expense)}",
                    icon = Icons.Default.TrendingDown,
                    iconTint = NodaraDanger,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            StatCard(
                title = "Balance Neto",
                value = "$${String.format("%.2f", summary.balance)}",
                subtitle = if (summary.balance >= 0) "Flujo de caja positivo" else "Alerta de déficit",
                icon = Icons.Default.AccountBalanceWallet,
                iconTint = if (summary.balance >= 0) NodaraSuccess else NodaraDanger,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text(
                text = "Historial de Transacciones",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NodaraInk
            )
        }

        if (movements.isEmpty()) {
            item {
                EmptyState(
                    title = "Sin movimientos registrados",
                    subtitle = "Registra un ingreso o gasto.",
                    actionText = "Registrar Movimiento",
                    onAction = onCreateClick
                )
            }
        } else {
            items(movements) { movement ->
                MovementCard(movement)
            }
        }
    }
}

@Composable
private fun MovementCard(movement: CashMovement) {
    val isIncome = movement.type.lowercase() == "income"
    val color = if (isIncome) NodaraSuccess else NodaraDanger
    val sign = if (isIncome) "+" else "-"

    NodaraCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = movement.concept, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NodaraInk)
                Text(text = "Categoría: ${movement.category}", fontSize = 12.sp, color = NodaraSlate)
            }
            Text(
                text = "$sign$${String.format("%.2f", movement.amount)}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = color
            )
        }
    }
}

@Composable
private fun CreateMovementDialog(
    onDismiss: () -> Unit,
    onConfirm: (type: String, category: String, concept: String, amount: Double) -> Unit
) {
    var type by remember { mutableStateOf("income") }
    var category by remember { mutableStateOf("Ventas") }
    var concept by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Movimiento", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = type == "income", onClick = { type = "income"; category = "Ventas" }, label = { Text("Ingreso") })
                    FilterChip(selected = type == "expense", onClick = { type = "expense"; category = "Operación" }, label = { Text("Gasto") })
                }
                NodaraTextField(value = concept, onValueChange = { concept = it }, label = "Concepto / Glosa")
                NodaraTextField(value = category, onValueChange = { category = it }, label = "Categoría")
                NodaraTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = "Monto ($)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    if (concept.isNotBlank() && amt > 0) onConfirm(type, category, concept, amt)
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
