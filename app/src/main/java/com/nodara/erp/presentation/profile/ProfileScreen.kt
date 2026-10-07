package com.nodara.erp.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nodara.erp.BuildConfig
import com.nodara.erp.domain.model.ResourceState
import com.nodara.erp.presentation.components.NodaraButton
import com.nodara.erp.presentation.components.NodaraCard
import com.nodara.erp.presentation.components.StatusBadge
import com.nodara.erp.presentation.theme.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit
) {
    val userState by viewModel.userState.collectAsState()
    val healthState by viewModel.healthState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NodaraMist)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Perfil y Configuración",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = NodaraInk
        )
        Text(
            text = "Información de cuenta y conexión al servidor",
            fontSize = 13.sp,
            color = NodaraSlate
        )

        Spacer(modifier = Modifier.height(20.dp))

        when (val state = userState) {
            is ResourceState.Success -> {
                val user = state.data
                NodaraCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = NodaraSignature, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NodaraInk)
                                Text(text = user.email, fontSize = 13.sp, color = NodaraSlate)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = NodaraLine)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = "Tenant ID:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NodaraSlate)
                        Text(text = user.tenantId, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NodaraInk)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = "Roles asignados:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NodaraSlate)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            user.roles.forEach { role ->
                                StatusBadge(status = role)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                NodaraCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Servidor y API REST", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NodaraInk)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Base URL: ${BuildConfig.API_BASE_URL}", fontSize = 12.sp, color = NodaraSlate)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Estado del servidor: ", fontSize = 12.sp, color = NodaraSlate)
                            StatusBadge(status = if (healthState == true) "En Línea (Render)" else "Comprobando...")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                NodaraButton(
                    text = "Cerrar Sesión",
                    onClick = { viewModel.logout(onLogout) },
                    icon = Icons.Default.ExitToApp,
                    containerColor = NodaraDanger,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            else -> {}
        }
    }
}
