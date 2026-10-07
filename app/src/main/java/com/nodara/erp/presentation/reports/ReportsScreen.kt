package com.nodara.erp.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DataThresholding
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nodara.erp.domain.model.ResourceState
import com.nodara.erp.presentation.components.NodaraButton
import com.nodara.erp.presentation.components.NodaraCard
import com.nodara.erp.presentation.theme.*

@Composable
fun ReportsScreen(viewModel: ReportsViewModel) {
    val seedState by viewModel.seedState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(seedState) {
        if (seedState is ResourceState.Success) {
            snackbarHostState.showSnackbar((seedState as ResourceState.Success).data)
            viewModel.resetState()
        } else if (seedState is ResourceState.Error) {
            snackbarHostState.showSnackbar((seedState as ResourceState.Error).message)
            viewModel.resetState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(NodaraMist)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Datos y Exportaciones",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = NodaraInk
            )
            Text(
                text = "Generación de reportes y carga de datos de muestra",
                fontSize = 13.sp,
                color = NodaraSlate
            )

            Spacer(modifier = Modifier.height(20.dp))

            NodaraCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Reportes en PDF y Excel", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NodaraInk)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Los enlaces firmados se obtienen del servidor con una vigencia de 5 minutos.",
                        fontSize = 12.sp,
                        color = NodaraSlate
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = NodaraMoss),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Exportar Catálogo de Productos (PDF)")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = NodaraSlate),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Exportar Facturación y Ventas (Excel)")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            NodaraCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Cargar Datos Iniciales de Muestra", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NodaraInk)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Si tu organización es nueva, puedes sembrar automáticamente 1,000 productos, 1,000 contactos y facturas de demostración en la base de datos.",
                        fontSize = 12.sp,
                        color = NodaraSlate
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    NodaraButton(
                        text = "Cargar Datos de Muestra",
                        onClick = { viewModel.seedSampleData() },
                        isLoading = seedState is ResourceState.Loading,
                        icon = Icons.Default.CloudDownload,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
