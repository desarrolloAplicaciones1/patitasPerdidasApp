package com.desarrolloaplicaciones1.patitasperdidas.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.desarrolloaplicaciones1.patitasperdidas.R
import com.desarrolloaplicaciones1.patitasperdidas.ui.theme.HuellitasTeal
import com.desarrolloaplicaciones1.patitasperdidas.ui.theme.Urbanist

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            viewModel.resetState()
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        Image(
            painter = painterResource(id = R.drawable.logo_register),
            contentDescription = "Crear cuenta",
            modifier = Modifier.width(240.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Nombre
        Text("Nombre", fontFamily = Urbanist, fontWeight = FontWeight.Medium,
            fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = name, onValueChange = { name = it; nameError = null },
            placeholder = { Text("Ej. Valentina Arce", color = Color.Gray, fontFamily = Urbanist) },
            isError = nameError != null, singleLine = true, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(3.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HuellitasTeal, unfocusedBorderColor = Color(0xFFDDDDDD))
        )
        if (nameError != null) Text(nameError!!, color = Color.Red, fontFamily = Urbanist,
            fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))

        Spacer(modifier = Modifier.height(16.dp))

        // Email
        Text("Email", fontFamily = Urbanist, fontWeight = FontWeight.Medium,
            fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = email, onValueChange = { email = it; emailError = null },
            placeholder = { Text("tu@email.com", color = Color.Gray, fontFamily = Urbanist) },
            trailingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray) },
            isError = emailError != null, singleLine = true, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(3.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HuellitasTeal, unfocusedBorderColor = Color(0xFFDDDDDD))
        )
        if (emailError != null) Text(emailError!!, color = Color.Red, fontFamily = Urbanist,
            fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))

        Spacer(modifier = Modifier.height(16.dp))

        // Contraseña
        Text("Contraseña", fontFamily = Urbanist, fontWeight = FontWeight.Medium,
            fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it; passwordError = null },
            placeholder = { Text("Mínimo 6 caracteres", color = Color.Gray, fontFamily = Urbanist) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null, tint = Color.Gray)
                }
            },
            isError = passwordError != null, singleLine = true, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(3.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HuellitasTeal, unfocusedBorderColor = Color(0xFFDDDDDD))
        )
        if (passwordError != null) Text(passwordError!!, color = Color.Red, fontFamily = Urbanist,
            fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))

        Spacer(modifier = Modifier.height(16.dp))

        // Confirmar contraseña
        Text("Confirmar Contraseña", fontFamily = Urbanist, fontWeight = FontWeight.Medium,
            fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = confirmPassword, onValueChange = { confirmPassword = it; confirmError = null },
            placeholder = { Text("Mínimo 6 caracteres", color = Color.Gray, fontFamily = Urbanist) },
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = { Icon(Icons.Default.Visibility, contentDescription = null, tint = Color.Gray) },
            isError = confirmError != null, singleLine = true, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(3.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HuellitasTeal, unfocusedBorderColor = Color(0xFFDDDDDD))
        )
        if (confirmError != null) Text(confirmError!!, color = Color.Red, fontFamily = Urbanist,
            fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))

        if (uiState is AuthUiState.Error) {
            Text((uiState as AuthUiState.Error).message, color = Color.Red, fontFamily = Urbanist,
                fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                nameError     = if (name.isBlank()) "El nombre es requerido" else null
                emailError    = if (email.isBlank()) "El email es requerido" else null
                passwordError = if (password.length < 6) "Mínimo 6 caracteres" else null
                confirmError  = if (password != confirmPassword) "Las contraseñas no coinciden" else null
                if (nameError == null && emailError == null && passwordError == null && confirmError == null) {
                    viewModel.register(name.trim(), email.trim(), password)
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = uiState !is AuthUiState.Loading,
            shape = RoundedCornerShape(3.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HuellitasTeal)
        ) {
            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Crear Cuenta", fontFamily = Urbanist, fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("¿Ya tenés cuenta? ", fontFamily = Urbanist,
                color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
            TextButton(onClick = onNavigateToLogin, contentPadding = PaddingValues(0.dp)) {
                Text("Iniciá Sesión", fontFamily = Urbanist, color = HuellitasTeal,
                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
