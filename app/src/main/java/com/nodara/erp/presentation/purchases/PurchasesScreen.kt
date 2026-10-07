package com.nodara.erp.presentation.purchases

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
import com.nodara.erp.data.dto.CreatePurchaseItemDto
import com.nodara.erp.domain.model.Purchase
import com.nodara.erp.domain.model.ResourceState
import com.nodara.erp.presentation.components.*
import com.nodara.erp.presentation.theme.*

@Composable
fun PurchasesScreen(viewModel: PurchasesViewModel) {
    val purchasesState by viewModel.purchasesState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val suppliers by viewModel.suppliersState.collectAsState()
    val products by viewModel.productsState.collectAsState()

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
                Icon(Icons.Default.Add, contentDescription = "Nueva Orden de Compra")
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
                    text = "Órdenes de Compra",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NodaraInk
                )
                IconButton(onClick = { viewModel.loadPurchases() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = NodaraSignature)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = purchasesState) {
                is ResourceState.Loading -> LoadingState("Cargando órdenes de compra...")
                is ResourceState.Error -> ErrorState(state.message, onRetry = { viewModel.loadPurchases() })
                is ResourceState.Success -> {
                    if (state.data.isEmpty()) {
                        EmptyState(
                            title = "Sin órdenes de compra",
                            subtitle = "Crea la primera orden para abastecer inventario.",
                            actionText = "Crear Orden de Compra",
                            onAction = { showCreateDialog = true }
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.data) { purchase ->
                                PurchaseCard(
                                    purchase = purchase,
                                    onUpdateStatus = { status -> viewModel.updateStatus(purchase.id, status) }
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
        CreatePurchaseDialog(
            suppliers = suppliers,
            products = products,
            onDismiss = { showCreateDialog = false },
            onConfirm = { supplierId, items -> viewModel.createPurchase(supplierId, items) }
        )
    }
}

@Composable
private fun PurchaseCard(
    purchase: Purchase,
    onUpdateStatus: (String) -> Unit
) {
    NodaraCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = purchase.number, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NodaraInk)
                    Text(text = "Proveedor: ${purchase.supplierName}", fontSize = 13.sp, color = NodaraSlate)
                }
                StatusBadge(status = purchase.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${purchase.items.size} productos | Total: $${String.format("%.2f", purchase.total)}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NodaraInk
            )

            if (purchase.status == "ordered") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { onUpdateStatus("received") }) {
                        Text("Recibir Mercancía", color = NodaraSuccess, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { onUpdateStatus("cancelled") }) {
                        Text("Cancelar", color = NodaraDanger)
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatePurchaseDialog(
    suppliers: List<com.nodara.erp.domain.model.Contact>,
    products: List<com.nodara.erp.domain.model.Product>,
    onDismiss: () -> Unit,
    onConfirm: (supplierId: String, items: List<CreatePurchaseItemDto>) -> Unit
) {
    var quantity by remember { mutableStateOf("10") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Orden de Compra", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (suppliers.isNotEmpty() && products.isNotEmpty()) {
                    Text("Proveedor: ${suppliers.first().name}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Producto: ${products.first().name}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    NodaraTextField(value = quantity, onValueChange = { quantity = it }, label = "Cantidad a pedir")
                } else {
                    Text("Asegúrate de registrar al menos un Proveedor y un Producto primero.", color = NodaraDanger, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val supplier = suppliers.firstOrNull()
                    val product = products.firstOrNull()
                    val qty = quantity.toIntOrNull() ?: 10
                    if (supplier != null && product != null) {
                        onConfirm(
                            supplier.id,
                            listOf(CreatePurchaseItemDto(product.id, qty, product.costo))
                        )
                    }
                },
                enabled = suppliers.isNotEmpty() && products.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = NodaraSignature)
            ) {
                Text("Generar Orden", color = NodaraPaper)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
