package com.desarrolloaplicaciones1.patitasperdidas.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.desarrolloaplicaciones1.patitasperdidas.ui.theme.HuellitasTeal
import com.desarrolloaplicaciones1.patitasperdidas.ui.theme.Urbanist

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val currentName = when (val s = uiState) {
        is ProfileUiState.Success -> s.user.name
        else -> "Valentin Arce"
    }
    val currentEmail = when (val s = uiState) {
        is ProfileUiState.Success -> s.user.email
        else -> "test@gmail.com"
    }

    var nombre      by remember(currentName) { mutableStateOf(currentName) }
    var ubicacion   by remember { mutableStateOf("Palermo, CABA") }
    var password    by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false; onBack() },
            title = { Text("¡Perfil actualizado!", fontFamily = Urbanist, fontWeight = FontWeight.Bold) },
            text = { Text("Tus datos fueron guardados correctamente.", fontFamily = Urbanist) },
            confirmButton = {
                TextButton(onClick = { showSuccess = false; onBack() }) {
                    Text("OK", color = HuellitasTeal, fontFamily = Urbanist)
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Top bar con mismo estilo que Profile
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape)
                    .background(Color(0xFFE8F7F6)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver",
                        tint = HuellitasTeal, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Editar perfil", fontFamily = Urbanist, fontWeight = FontWeight.Bold,
                fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Avatar con inicial del nombre real
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape)
                    .background(HuellitasTeal).align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentName.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                    fontFamily = Urbanist, fontWeight = FontWeight.Bold,
                    fontSize = 32.sp, color = Color.White
                )
            }

            // Email (solo lectura)
            Text("Email", fontFamily = Urbanist, fontWeight = FontWeight.Medium,
                fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            OutlinedTextField(
                value = currentEmail,
                onValueChange = {},
                enabled = false,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(3.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color(0xFFDDDDDD),
                    disabledTextColor = Color(0xFF888888),
                    disabledContainerColor = Color(0xFFF5F5F5)
                )
            )
            Text("El email no se puede modificar", fontFamily = Urbanist,
                fontSize = 11.sp, color = Color(0xFF888888))

            // Nombre
            Text("Nombre", fontFamily = Urbanist, fontWeight = FontWeight.Medium,
                fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(3.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HuellitasTeal,
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            // Ubicación
            Text("Ubicación", fontFamily = Urbanist, fontWeight = FontWeight.Medium,
                fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                placeholder = { Text("Ej. Palermo, CABA", color = Color.Gray, fontFamily = Urbanist) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(3.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HuellitasTeal,
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            HorizontalDivider(color = Color(0xFFEEEEEE))

            // Nueva contraseña
            Text("Nueva contraseña", fontFamily = Urbanist, fontWeight = FontWeight.Medium,
                fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Mínimo 6 caracteres", color = Color.Gray, fontFamily = Urbanist) },
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passVisible = !passVisible }) {
                        Icon(if (passVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null, tint = Color.Gray)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(3.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HuellitasTeal,
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            // Confirmar contraseña
            Text("Confirmar contraseña", fontFamily = Urbanist, fontWeight = FontWeight.Medium,
                fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            OutlinedTextField(
                value = confirmPass,
                onValueChange = { confirmPass = it },
                placeholder = { Text("Repetí la contraseña", color = Color.Gray, fontFamily = Urbanist) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(3.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HuellitasTeal,
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
            Button(
                onClick = { showSuccess = true },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .navigationBarsPadding().height(52.dp),
                shape = RoundedCornerShape(3.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HuellitasTeal)
            ) {
                Text("Guardar cambios", fontFamily = Urbanist,
                    fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White)
            }
        }
    }
}