package com.uade.huellitas.presentation.profile.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.uade.huellitas.ui.theme.HuellitasTeal
import com.uade.huellitas.ui.theme.Urbanist

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: EditProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    var passVisible by remember { mutableStateOf(false) }

    if (uiState is EditProfileUiState.Success) {
        AlertDialog(
            onDismissRequest = {
                viewModel.consumeSuccess()
                onBack()
            },
            title = { Text("Perfil actualizado", fontFamily = Urbanist, fontWeight = FontWeight.Bold) },
            text = { Text("Tus datos fueron guardados correctamente.", fontFamily = Urbanist) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.consumeSuccess()
                    onBack()
                }) {
                    Text("OK", color = HuellitasTeal, fontFamily = Urbanist)
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F7F6)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = HuellitasTeal, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Editar perfil", fontFamily = Urbanist, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(HuellitasTeal)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formState.name.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                    fontFamily = Urbanist,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = Color.White
                )
            }

            if (uiState is EditProfileUiState.Error) {
                Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = (uiState as EditProfileUiState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Text("Email", fontFamily = Urbanist, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            OutlinedTextField(
                value = formState.email,
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
            Text("El email no se puede modificar", fontFamily = Urbanist, fontSize = 11.sp, color = Color(0xFF888888))

            Text("Nombre", fontFamily = Urbanist, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            OutlinedTextField(
                value = formState.name,
                onValueChange = viewModel::onNameChange,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(3.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HuellitasTeal,
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            Text("Ubicacion", fontFamily = Urbanist, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            OutlinedTextField(
                value = formState.location,
                onValueChange = viewModel::onLocationChange,
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

            Text("Nueva contrasena", fontFamily = Urbanist, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            OutlinedTextField(
                value = formState.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = { Text("Minimo 6 caracteres", color = Color.Gray, fontFamily = Urbanist) },
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passVisible = !passVisible }) {
                        Icon(if (passVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = Color.Gray)
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

            Text("Confirmar contrasena", fontFamily = Urbanist, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            OutlinedTextField(
                value = formState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                placeholder = { Text("Repeti la contrasena", color = Color.Gray, fontFamily = Urbanist) },
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
                onClick = viewModel::saveProfile,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .navigationBarsPadding()
                    .height(52.dp),
                enabled = uiState !is EditProfileUiState.Loading,
                shape = RoundedCornerShape(3.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HuellitasTeal)
            ) {
                if (uiState is EditProfileUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Guardar cambios", fontFamily = Urbanist, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}
