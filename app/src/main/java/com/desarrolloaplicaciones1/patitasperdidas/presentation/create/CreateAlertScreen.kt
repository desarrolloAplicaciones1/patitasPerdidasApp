package com.desarrolloaplicaciones1.patitasperdidas.presentation.create

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.AlertType
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.PetType
import com.desarrolloaplicaciones1.patitasperdidas.ui.theme.HuellitasTeal
import com.desarrolloaplicaciones1.patitasperdidas.ui.theme.StatusFound
import com.desarrolloaplicaciones1.patitasperdidas.ui.theme.Urbanist

@Composable
fun CreateAlertScreen(
    onBack: () -> Unit,
    onAlertCreated: () -> Unit,
    viewModel: CreateAlertViewModel = viewModel()
) {
    var hasCollar        by remember { mutableStateOf(false) }
    val uiState          by viewModel.uiState.collectAsStateWithLifecycle()
    val formState        by viewModel.formState.collectAsStateWithLifecycle()
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isCastrated      by remember { mutableStateOf(false) }
    var size             by remember { mutableStateOf("Chico") }
    var nameError        by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var barrioError      by remember { mutableStateOf<String?>(null) }
    var phoneError       by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    LaunchedEffect(uiState) {
        if (uiState is CreateAlertUiState.Success) {
            viewModel.resetState()
            onAlertCreated()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reportar mascota",
                fontFamily = Urbanist,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .drawBehind {
                        val strokeWidth = 2.dp.toPx()
                        val y = this.size.height
                        drawLine(HuellitasTeal, Offset(0f, y),
                            Offset(this.size.width, y), strokeWidth)
                    }
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFE8F7F6))
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar",
                    tint = HuellitasTeal, modifier = Modifier.size(16.dp))
            }
        }

        // Contenido scrolleable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState is CreateAlertUiState.Error) {
                Surface(color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp)) {
                    Text(text = (uiState as CreateAlertUiState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp))
                }
            }

            // FOTO
            SectionLabel("FOTO")
            PhotoPickerBox(imageUri = selectedImageUri,
                onClick = { imagePickerLauncher.launch("image/*") })

            // ESTADO
            SectionLabel("ESTADO")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AlertType.entries.forEach { type ->
                    val isSelected = formState.alertType == type
                    val bgColor = when {
                        isSelected && type == AlertType.LOST  -> Color(0xFFF43F47)
                        isSelected && type == AlertType.FOUND -> StatusFound
                        else -> Color(0xFFF5F5F5)
                    }
                    Button(
                        onClick = { viewModel.onAlertTypeChange(type) },
                        modifier = Modifier.weight(1f).height(42.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = bgColor,
                            contentColor = if (isSelected) Color.White else Color(0xFF888888)
                        )
                    ) {
                        Text(
                            text = if (type == AlertType.LOST) "Perdido" else "Encontrado",
                            fontFamily = Urbanist, fontWeight = FontWeight.Normal, fontSize = 14.sp
                        )
                    }
                }
            }

            // NOMBRE *
            SectionLabel("NOMBRE DEL ANIMAL *")
            OutlinedTextField(
                value = formState.petName,
                onValueChange = { viewModel.onPetNameChange(it); nameError = null },
                placeholder = { Text("Ej. Buddy, Luna...", color = Color.Gray, fontFamily = Urbanist) },
                singleLine = true,
                isError = nameError != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(3.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HuellitasTeal,
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )
            if (nameError != null) {
                Text(nameError!!, color = Color.Red, fontFamily = Urbanist, fontSize = 12.sp)
            }

            // ESPECIE y CASTRADO
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    SectionLabel("ESPECIE")
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        SmallChip("Perro", formState.petType == PetType.DOG) {
                            viewModel.onPetTypeChange(PetType.DOG)
                        }
                        SmallChip("Gato", formState.petType == PetType.CAT) {
                            viewModel.onPetTypeChange(PetType.CAT)
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    SectionLabel("CASTRADO")
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        SmallChip("No", !isCastrated) { isCastrated = false }
                        SmallChip("Sí", isCastrated)  { isCastrated = true }
                    }
                }
            }

            // TAMAÑO
            SectionLabel("TAMAÑO")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Chico", "Mediano", "Grande").forEach { s ->
                    SmallChip(s, size == s) { size = s }
                }
            }

            // COLLAR
            SectionLabel("COLLAR")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallChip("No", !hasCollar) { hasCollar = false }
                SmallChip("Sí", hasCollar)  { hasCollar = true }
            }

            // DESCRIPCIÓN *
            SectionLabel("DESCRIPCIÓN *")
            OutlinedTextField(
                value = formState.description,
                onValueChange = { viewModel.onDescriptionChange(it); descriptionError = null },
                placeholder = { Text("Cualquier detalle que ayude...", color = Color.Gray, fontFamily = Urbanist) },
                singleLine = false,
                minLines = 4,
                maxLines = 6,
                isError = descriptionError != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(3.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HuellitasTeal,
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )
            if (descriptionError != null) {
                Text(descriptionError!!, color = Color.Red, fontFamily = Urbanist, fontSize = 12.sp)
            }

            // BARRIO *
            SectionLabel("BARRIO *")
            OutlinedTextField(
                value = formState.address,
                onValueChange = { viewModel.onLocationChange(0.0, 0.0, it); barrioError = null },
                placeholder = { Text("Ej. Palermo, CABA", color = Color.Gray, fontFamily = Urbanist) },
                singleLine = true,
                isError = barrioError != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(3.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HuellitasTeal,
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )
            if (barrioError != null) {
                Text(barrioError!!, color = Color.Red, fontFamily = Urbanist, fontSize = 12.sp)
            }

            // COLOR
            SectionLabel("COLOR")
            OutlinedTextField(
                value = formState.color ?: "",
                onValueChange = viewModel::onColorChange,
                placeholder = { Text("Ej. Tricolor", color = Color.Gray, fontFamily = Urbanist) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(3.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HuellitasTeal,
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            // TELÉFONO DE CONTACTO *
            SectionLabel("TELÉFONO DE CONTACTO *")
            OutlinedTextField(
                value = formState.contactPhone ?: "",
                onValueChange = { viewModel.onContactPhoneChange(it); phoneError = null },
                placeholder = { Text("Ej. +54 11 1234-5678", color = Color.Gray, fontFamily = Urbanist) },
                singleLine = true,
                isError = phoneError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(3.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HuellitasTeal,
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )
            if (phoneError != null) {
                Text(phoneError!!, color = Color.Red, fontFamily = Urbanist, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Botón publicar fijo abajo
        Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
            Button(
                onClick = {
                    nameError        = if (formState.petName.isBlank()) "El nombre es obligatorio" else null
                    descriptionError = if (formState.description.isBlank()) "La descripción es obligatoria" else null
                    barrioError      = if (formState.address.isBlank()) "El barrio es obligatorio" else null
                    phoneError       = if ((formState.contactPhone ?: "").isBlank()) "El teléfono es obligatorio" else null
                    if (nameError == null && descriptionError == null && barrioError == null && phoneError == null) {
                        viewModel.submitAlert()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding()
                    .height(52.dp),
                enabled = uiState !is CreateAlertUiState.Loading,
                shape = RoundedCornerShape(3.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HuellitasTeal)
            ) {
                if (uiState is CreateAlertUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp),
                        color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Publicar", fontFamily = Urbanist,
                        fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text = text, fontFamily = Urbanist, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
}

@Composable
private fun SmallChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) HuellitasTeal else Color(0xFFF0F0F0))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(text = label, fontFamily = Urbanist,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 13.sp, color = if (selected) Color.White else Color(0xFF3D3D3D))
    }
}

@Composable
private fun PhotoPickerBox(imageUri: Uri?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF9F9F9))
            .border(width = 1.dp, color = Color(0xFFDDDDDD), shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            AsyncImage(model = imageUri, contentDescription = "Foto seleccionada",
                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CameraAlt, contentDescription = null,
                    tint = HuellitasTeal, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.height(6.dp))
                Text("Tocá para sacar una foto", fontFamily = Urbanist,
                    fontSize = 14.sp, color = Color(0xFF3D3D3D))
                Text("o elegir desde galería", fontFamily = Urbanist,
                    fontSize = 12.sp, color = Color(0xFF888888))
            }
        }
    }
}