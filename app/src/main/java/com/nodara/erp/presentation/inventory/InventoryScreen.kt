package com.nodara.erp.presentation.inventory

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nodara.erp.domain.model.Product
import com.nodara.erp.domain.model.ResourceState
import com.nodara.erp.presentation.components.*
import com.nodara.erp.presentation.theme.*

@Composable
fun InventoryScreen(viewModel: InventoryViewModel) {
    val productsState by viewModel.productsState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedProductForEdit by remember { mutableStateOf<Product?>(null) }
    var selectedProductForStock by remember { mutableStateOf<Product?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(actionState) {
        if (actionState is ResourceState.Success) {
            snackbarHostState.showSnackbar((actionState as ResourceState.Success).data)
            viewModel.resetActionState()
            showCreateDialog = false
            selectedProductForEdit = null
            selectedProductForStock = null
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
                Icon(Icons.Default.Add, contentDescription = "Nuevo Producto")
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
            // Title & Search
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inventario de Productos",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NodaraInk
                )
                IconButton(onClick = { viewModel.loadProducts() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = NodaraSignature)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            NodaraTextField(
                value = searchQuery,
                onValueChange = {
                    viewModel.searchQuery.value = it
                    viewModel.loadProducts(it)
                },
                label = "",
                placeholder = "Buscar por nombre, SKU o código...",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = productsState) {
                is ResourceState.Loading -> LoadingState("Cargando catálogo de productos...")
                is ResourceState.Error -> ErrorState(state.message, onRetry = { viewModel.loadProducts() })
                is ResourceState.Success -> {
                    if (state.data.isEmpty()) {
                        EmptyState(
                            title = "Sin productos encontrados",
                            subtitle = "Agrega nuevos productos o borra la búsqueda.",
                            actionText = "Crear Producto",
                            onAction = { showCreateDialog = true }
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.data) { product ->
                                ProductCard(
                                    product = product,
                                    onEdit = { selectedProductForEdit = product },
                                    onStockAdjust = { selectedProductForStock = product },
                                    onDelete = { viewModel.deleteProduct(product.id) }
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
        CreateOrEditProductDialog(
            product = null,
            onDismiss = { showCreateDialog = false },
            onConfirm = { sku, barcode, name, img, costo, precio, minStock, initStock ->
                viewModel.createProduct(sku, barcode, name, img, costo, precio, minStock, initStock)
            }
        )
    }

    if (selectedProductForEdit != null) {
        CreateOrEditProductDialog(
            product = selectedProductForEdit,
            onDismiss = { selectedProductForEdit = null },
            onConfirm = { sku, barcode, name, img, costo, precio, minStock, _ ->
                viewModel.updateProduct(selectedProductForEdit!!.id, sku, barcode, name, img, costo, precio, minStock)
            }
        )
    }

    if (selectedProductForStock != null) {
        StockAdjustDialog(
            product = selectedProductForStock!!,
            onDismiss = { selectedProductForStock = null },
            onConfirm = { qty, reason ->
                viewModel.adjustStock(selectedProductForStock!!.id, qty, reason)
            }
        )
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onEdit: () -> Unit,
    onStockAdjust: () -> Unit,
    onDelete: () -> Unit
) {
    NodaraCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image or Placeholder
            if (!product.imageUrl.isNull_or_blank()) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NodaraSignature.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Inventory2, contentDescription = null, tint = NodaraSignature)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = NodaraInk
                )
                Text(
                    text = "SKU: ${product.sku} | Barcode: ${product.barcode}",
                    fontSize = 12.sp,
                    color = NodaraSlate
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Precio: $${String.format("%.2f", product.precio)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = NodaraSuccess
                    )
                    Text(
                        text = "Costo: $${String.format("%.2f", product.costo)}",
                        fontSize = 12.sp,
                        color = NodaraMoss
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                val isLowStock = product.currentStock <= product.stockMinimo
                val stockBg = if (isLowStock) NodaraDanger.copy(alpha = 0.15f) else NodaraSuccess.copy(alpha = 0.15f)
                val stockColor = if (isLowStock) NodaraDanger else NodaraSuccess

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(stockBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Stock: ${product.currentStock}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = stockColor
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    IconButton(onClick = onStockAdjust, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.SwapVert, contentDescription = "Ajustar Stock", tint = NodaraSignature)
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = NodaraMoss)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = NodaraDanger)
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateOrEditProductDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onConfirm: (sku: String, barcode: String, name: String, img: String?, costo: Double, precio: Double, minStock: Int, initStock: Int) -> Unit
) {
    var sku by remember { mutableStateOf(product?.sku ?: "PROD-${System.currentTimeMillis().toString().takeLast(6)}") }
    var barcode by remember { mutableStateOf(product?.barcode ?: "750${System.currentTimeMillis().toString().takeLast(9)}") }
    var name by remember { mutableStateOf(product?.name ?: "") }
    var imageUrl by remember { mutableStateOf(product?.imageUrl ?: "") }
    var costo by remember { mutableStateOf(product?.costo?.toString() ?: "100.00") }
    var precio by remember { mutableStateOf(product?.precio?.toString() ?: "150.00") }
    var stockMinimo by remember { mutableStateOf(product?.stockMinimo?.toString() ?: "5") }
    var initialStock by remember { mutableStateOf("20") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product == null) "Nuevo Producto" else "Editar Producto", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NodaraTextField(value = name, onValueChange = { name = it }, label = "Nombre del producto")
                NodaraTextField(value = sku, onValueChange = { sku = it }, label = "SKU")
                NodaraTextField(value = barcode, onValueChange = { barcode = it }, label = "Código de barras")
                NodaraTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = "URL Imagen (opcional)")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NodaraTextField(value = costo, onValueChange = { costo = it }, label = "Costo ($)", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    NodaraTextField(value = precio, onValueChange = { precio = it }, label = "Precio ($)", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NodaraTextField(value = stockMinimo, onValueChange = { stockMinimo = it }, label = "Stock Mínimo", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    if (product == null) {
                        NodaraTextField(value = initialStock, onValueChange = { initialStock = it }, label = "Stock Inicial", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        sku, barcode, name,
                        if (imageUrl.isBlank()) null else imageUrl,
                        costo.toDoubleOrNull() ?: 0.0,
                        precio.toDoubleOrNull() ?: 0.0,
                        stockMinimo.toIntOrNull() ?: 0,
                        initialStock.toIntOrNull() ?: 0
                    )
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

@Composable
private fun StockAdjustDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: (quantity: Int, reason: String) -> Unit
) {
    var quantity by remember { mutableStateOf("5") }
    var reason by remember { mutableStateOf("Ajuste manual desde App Móvil") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajustar Stock — ${product.name}", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Stock actual: ${product.currentStock} unidades", fontSize = 14.sp, color = NodaraSlate)
                NodaraTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = "Cantidad de ajuste (Ej. 10 para entrada, -5 para salida)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                NodaraTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = "Motivo del ajuste"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantity.toIntOrNull() ?: 0
                    if (qty != 0) onConfirm(qty, reason)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NodaraSignature)
            ) {
                Text("Aplicar Ajuste", color = NodaraPaper)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

private fun String?.isNull_or_blank(): Boolean = this == null || this.trim().isEmpty()
