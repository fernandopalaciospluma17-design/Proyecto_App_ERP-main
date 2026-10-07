package com.nodara.erp.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nodara.erp.domain.model.ResourceState
import com.nodara.erp.presentation.components.NodaraButton
import com.nodara.erp.presentation.components.NodaraOutlinedButton
import com.nodara.erp.presentation.components.NodaraTextField
import com.nodara.erp.presentation.theme.*

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit
) {
    val registerState by viewModel.registerState.collectAsState()
    val focusManager = LocalFocusManager.current

    var name by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NodaraInk),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crear Cuenta ERP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = NodaraPaper
            )
            Text(
                text = "Registra tu empresa en Nodara",
                fontSize = 14.sp,
                color = NodaraMist.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NodaraPaper),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    NodaraTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nombre completo",
                        placeholder = "Ej. Carlos Mendoza",
                        leadingIcon = Icons.Default.Person,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    NodaraTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = "Nombre de la Empresa",
                        placeholder = "Ej. Comercializadora del Norte",
                        leadingIcon = Icons.Default.Business,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    NodaraTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Correo electrónico",
                        placeholder = "admin@empresa.com",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    NodaraTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Contraseña (min. 10 caracteres)",
                        placeholder = "••••••••••••",
                        leadingIcon = Icons.Default.Lock,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            viewModel.register(name, companyName, email, password)
                        })
                    )

                    if (registerState is ResourceState.Error) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = (registerState as ResourceState.Error).message,
                            color = NodaraDanger,
                            fontSize = 13.sp
                        )
                    }

                    if (registerState is ResourceState.Success) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = (registerState as ResourceState.Success).data,
                            color = NodaraSuccess,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    NodaraButton(
                        text = "Registrar Empresa",
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.register(name, companyName, email, password)
                        },
                        isLoading = registerState is ResourceState.Loading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    NodaraOutlinedButton(
                        text = "Ya tengo cuenta (Iniciar Sesión)",
                        onClick = onNavigateToLogin,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
