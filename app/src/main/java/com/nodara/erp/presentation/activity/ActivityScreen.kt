package com.nodara.erp.presentation.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nodara.erp.domain.model.AuditLog
import com.nodara.erp.domain.model.ResourceState
import com.nodara.erp.presentation.components.*
import com.nodara.erp.presentation.theme.*

@Composable
fun ActivityScreen(viewModel: ActivityViewModel) {
    val activityState by viewModel.activityState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NodaraMist)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Registro de Actividad",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = NodaraInk
            )
            IconButton(onClick = { viewModel.loadActivity() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = NodaraSignature)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = activityState) {
            is ResourceState.Loading -> LoadingState("Cargando logs de auditoría...")
            is ResourceState.Error -> ErrorState(state.message, onRetry = { viewModel.loadActivity() })
            is ResourceState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyState(
                        title = "Sin actividad registrada",
                        subtitle = "Las acciones del sistema aparecerán aquí."
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.data) { log ->
                            AuditLogCard(log)
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun AuditLogCard(log: AuditLog) {
    NodaraCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${log.action} — ${log.resource}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NodaraInk)
                Text(text = "Usuario: ${log.userName}", fontSize = 12.sp, color = NodaraSlate)
                Text(text = log.timestamp, fontSize = 11.sp, color = NodaraMoss)
            }
            StatusBadge(status = if (log.statusCode in 200..299) "OK" else "Error")
        }
    }
}
