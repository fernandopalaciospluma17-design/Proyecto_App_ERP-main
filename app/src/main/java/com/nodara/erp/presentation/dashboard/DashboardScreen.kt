package com.nodara.erp.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.nodara.erp.domain.model.DashboardSummary
import com.nodara.erp.domain.model.RecentInvoice
import com.nodara.erp.domain.model.ResourceState
import com.nodara.erp.presentation.components.*
import com.nodara.erp.presentation.theme.*

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToModule: (String) -> Unit
) {
    val summaryState by viewModel.summaryState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NodaraMist)
    ) {
        when (val state = summaryState) {
            is ResourceState.Loading -> LoadingState("Cargando indicadores de Nodara ERP...")
            is ResourceState.Error -> ErrorState(state.message, onRetry = { viewModel.loadDashboard() })
            is ResourceState.Success -> DashboardContent(state.data, onNavigateToModule, onRefresh = { viewModel.loadDashboard() })
            else -> {}
        }
    }
}

@Composable
private fun DashboardContent(
    summary: DashboardSummary,
    onNavigateToModule: (String) -> Unit,
    onRefresh: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tablero Principal",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = NodaraInk
                    )
                    Text(
                        text = "Resumen de operaciones en tiempo real",
                        fontSize = 13.sp,
                        color = NodaraSlate
                    )
                }
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = NodaraSignature)
                }
            }
        }

        // Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Ventas Hoy",
                        value = "$${String.format("%.2f", summary.salesToday)}",
                        subtitle = "${summary.invoicesToday} facturas",
                        icon = Icons.Default.TrendingUp,
                        iconTint = NodaraSuccess,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Ventas Mes",
                        value = "$${String.format("%.2f", summary.salesThisMonth)}",
                        subtitle = "${summary.invoicesThisMonth} facturas",
                        icon = Icons.Default.AttachMoney,
                        iconTint = NodaraSignature,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Por Cobrar",
                        value = "$${String.format("%.2f", summary.receivables)}",
                        subtitle = "${summary.pendingInvoices} pendientes",
                        icon = Icons.Default.AccountBalance,
                        iconTint = NodaraWarning,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Valor Inventario",
                        value = "$${String.format("%.2f", summary.inventoryValue)}",
                        subtitle = "${summary.products} productos",
                        icon = Icons.Default.Inventory2,
                        iconTint = NodaraMoss,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Module Access Chips / Shortcuts
        item {
            Text(
                text = "Acceso Rápido a Módulos",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NodaraInk
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShortcutButton("Inventario", Icons.Default.Inventory, NodaraSignature, Modifier.weight(1f)) { onNavigateToModule("inventory") }
                    ShortcutButton("Ventas", Icons.Default.PointOfSale, NodaraSuccess, Modifier.weight(1f)) { onNavigateToModule("sales") }
                    ShortcutButton("Contactos", Icons.Default.People, NodaraMoss, Modifier.weight(1f)) { onNavigateToModule("contacts") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShortcutButton("Compras", Icons.Default.ShoppingCart, NodaraWarning, Modifier.weight(1f)) { onNavigateToModule("purchases") }
                    ShortcutButton("Finanzas", Icons.Default.AccountBalanceWallet, NodaraSlate, Modifier.weight(1f)) { onNavigateToModule("finance") }
                    ShortcutButton("Proyectos", Icons.Default.Assignment, NodaraInk, Modifier.weight(1f)) { onNavigateToModule("projects") }
                }
            }
        }

        // Recent Invoices Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Facturas Recientes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NodaraInk
                )
                TextButton(onClick = { onNavigateToModule("sales") }) {
                    Text("Ver todas", color = NodaraSignature, fontSize = 13.sp)
                }
            }
        }

        if (summary.recentInvoices.isEmpty()) {
            item {
                EmptyState(
                    title = "Sin facturas registradas",
                    subtitle = "Crea la primera factura desde el módulo de Ventas."
                )
            }
        } else {
            items(summary.recentInvoices) { invoice ->
                RecentInvoiceCard(invoice)
            }
        }
    }
}

@Composable
private fun ShortcutButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = NodaraPaper),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NodaraInk)
        }
    }
}

@Composable
private fun RecentInvoiceCard(invoice: RecentInvoice) {
    NodaraCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = invoice.number,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = NodaraInk
                )
                Text(
                    text = invoice.customer,
                    fontSize = 13.sp,
                    color = NodaraSlate
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format("%.2f", invoice.total)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = NodaraInk
                )
                Spacer(modifier = Modifier.height(2.dp))
                StatusBadge(status = invoice.status)
            }
        }
    }
}
