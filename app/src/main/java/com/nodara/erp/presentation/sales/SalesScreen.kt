package com.nodara.erp.presentation.sales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nodara.erp.data.dto.CreateInvoiceItemDto
import com.nodara.erp.domain.model.Invoice
import com.nodara.erp.domain.model.ResourceState
import com.nodara.erp.presentation.components.*
import com.nodara.erp.presentation.theme.*

@Composable
fun SalesScreen(viewModel: SalesViewModel) {
    val invoicesState by viewModel.invoicesState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val products by viewModel.productsState.collectAsState()
    val contacts by viewModel.contactsState.collectAsState()

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
                Icon(Icons.Default.Add, contentDescription = "Nueva Factura")
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
                    text = "Ventas y Facturación",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NodaraInk
                )
                IconButton(onClick = { viewModel.loadInvoices() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = NodaraSignature)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            NodaraTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.loadInvoices(it.ifBlank { null })
                },
                label = "",
                placeholder = "Buscar por número o cliente...",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = invoicesState) {
                is ResourceState.Loading -> LoadingState("Cargando historial de ventas...")
                is ResourceState.Error -> ErrorState(state.message, onRetry = { viewModel.loadInvoices() })
                is ResourceState.Success -> {
                    if (state.data.isEmpty()) {
                        EmptyState(
                            title = "Sin facturas registradas",
                            subtitle = "Emite la primera factura de venta.",
                            actionText = "Crear Factura",
                            onAction = { showCreateDialog = true }
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.data) { invoice ->
                                InvoiceCard(
                                    invoice = invoice,
                                    onUpdateStatus = { status -> viewModel.updateInvoiceStatus(invoice.id, status) }
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
        CreateInvoiceDialog(
            contacts = contacts,
            products = products,
            onDismiss = { showCreateDialog = false },
            onConfirm = { number, customerId, customerName, customerTaxId, items, issuedAt ->
                viewModel.createInvoice(number, customerId, customerName, customerTaxId, items, issuedAt)
            }
        )
    }
}

@Composable
private fun InvoiceCard(
    invoice: Invoice,
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
                    Text(text = invoice.number, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NodaraInk)
                    Text(text = "Cliente: ${invoice.customerName}", fontSize = 13.sp, color = NodaraSlate)
                }
                StatusBadge(status = invoice.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${invoice.items.size} ítems | Total: $${String.format("%.2f", invoice.total)}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NodaraSuccess
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (invoice.status == "pending") {
                    TextButton(onClick = { onUpdateStatus("paid") }) {
                        Text("Marcar Cobrada", color = NodaraSuccess, fontWeight = FontWeight.Bold)
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
private fun CreateInvoiceDialog(
    contacts: List<com.nodara.erp.domain.model.Contact>,
    products: List<com.nodara.erp.domain.model.Product>,
    onDismiss: () -> Unit,
    onConfirm: (number: String, customerId: String, customerName: String, customerTaxId: String?, items: List<CreateInvoiceItemDto>, issuedAt: String) -> Unit
) {
    var number by remember { mutableStateOf("FAC-${System.currentTimeMillis().toString().takeLast(6)}") }
    var selectedContactIndex by remember { mutableStateOf(0) }
    var selectedProductIndex by remember { mutableStateOf(0) }
    var quantity by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Factura de Venta", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NodaraTextField(value = number, onValueChange = { number = it }, label = "Número de factura")

                if (contacts.isNotEmpty()) {
                    Text("Cliente:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NodaraSlate)
                    Text(
                        text = "Cliente seleccionado: ${contacts.getOrNull(selectedContactIndex)?.name ?: "N/A"}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NodaraInk
                    )
                } else {
                    Text("Sin clientes registrados. Crea un cliente primero.", color = NodaraDanger, fontSize = 12.sp)
                }

                if (products.isNotEmpty()) {
                    Text("Producto principal:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NodaraSlate)
                    val prod = products.getOrNull(selectedProductIndex)
                    Text(
                        text = "${prod?.name ?: "N/A"} — $${prod?.precio ?: 0.0}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NodaraInk
                    )
                    NodaraTextField(value = quantity, onValueChange = { quantity = it }, label = "Cantidad")
                } else {
                    Text("Sin productos disponibles en inventario.", color = NodaraDanger, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val customer = contacts.getOrNull(selectedContactIndex)
                    val product = products.getOrNull(selectedProductIndex)
                    val qty = quantity.toIntOrNull() ?: 1
                    if (customer != null && product != null) {
                        val items = listOf(
                            CreateInvoiceItemDto(
                                productId = product.id,
                                sku = product.sku,
                                quantity = qty,
                                taxRate = 0.16
                            )
                        )
                        onConfirm(
                            number,
                            customer.id,
                            customer.name,
                            customer.taxId,
                            items,
                            java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).format(java.util.Date())
                        )
                    }
                },
                enabled = contacts.isNotEmpty() && products.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = NodaraSignature)
            ) {
                Text("Emitir Factura", color = NodaraPaper)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
