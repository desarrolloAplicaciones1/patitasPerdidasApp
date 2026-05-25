package com.uade.huellitas.presentation.alert.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.uade.huellitas.domain.model.Alert
import com.uade.huellitas.domain.model.AlertType
import com.uade.huellitas.domain.model.PetType
import com.uade.huellitas.ui.theme.HuellitasTeal
import com.uade.huellitas.ui.theme.Urbanist
import java.net.URLEncoder

@Composable
fun AlertDetailScreen(
    alertId: String,
    onBack: () -> Unit,
    viewModel: AlertDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(alertId) { viewModel.loadAlert(alertId) }
    LaunchedEffect(uiState) {
        if (uiState is AlertDetailUiState.Deleted || uiState is AlertDetailUiState.Resolved) {
            onBack()
        }
    }

    when (val state = uiState) {
        is AlertDetailUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = HuellitasTeal)
            }
        }
        is AlertDetailUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onBack) { Text("Volver") }
                }
            }
        }
        is AlertDetailUiState.Success -> {
            AlertDetailContent(
                alert = state.alert,
                onBack = onBack,
                onUpdate = viewModel::updateAlert,
                onResolve = viewModel::resolveAlert,
                onDelete = viewModel::deleteAlert
            )
        }
        else -> Unit
    }
}

@Composable
private fun AlertDetailContent(
    alert: Alert,
    onBack: () -> Unit,
    onUpdate: (String, String, String) -> Unit,
    onResolve: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var isEditing         by remember { mutableStateOf(false) }
    var editName          by remember { mutableStateOf(alert.petName) }
    var editDescription   by remember { mutableStateOf(alert.description) }
    var editColor         by remember { mutableStateOf(alert.color ?: "") }
    var showResolveDialog by remember { mutableStateOf(false) }
    var showDeleteDialog  by remember { mutableStateOf(false) }

    if (showResolveDialog) {
        AlertDialog(
            onDismissRequest = { showResolveDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Â¿Marcar como resuelto?", fontFamily = Urbanist,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground) },
            text = { Text("El aviso dejarÃ¡ de aparecer en el feed.", fontFamily = Urbanist,
                color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = { showResolveDialog = false; onResolve() }) {
                    Text("Confirmar", color = HuellitasTeal, fontFamily = Urbanist)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResolveDialog = false }) {
                    Text("Cancelar", fontFamily = Urbanist,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Â¿Eliminar aviso?", fontFamily = Urbanist,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground) },
            text = { Text("Esta acciÃ³n no se puede deshacer.", fontFamily = Urbanist,
                color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; onDelete() }) {
                    Text("Eliminar", color = Color.Red, fontFamily = Urbanist)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar", fontFamily = Urbanist,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Hero image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color(0xFFB2EDED))
        ) {
            val photoUrl = if (alert.photoUrls.isNotEmpty()) alert.photoUrls.first()
            else "https://images.dog.ceo/breeds/retriever-golden/n02099601_3004.jpg"

            AsyncImage(
                model = photoUrl,
                contentDescription = "Foto de ${alert.petName}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Back button
            Box(
                modifier = Modifier
                    .padding(16.dp).statusBarsPadding().size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
                    .align(Alignment.TopStart),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver",
                        tint = Color(0xFF1C1C1C), modifier = Modifier.size(20.dp))
                }
            }

            // Badge PERDIDO/ENCONTRADO
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart).padding(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (alert.type == AlertType.LOST) Color(0xFFF43F47) else Color(0xFF43A047)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    if (alert.type == AlertType.LOST) "PERDIDO" else "ENCONTRADO",
                    color = Color.White, fontFamily = Urbanist,
                    fontWeight = FontWeight.Bold, fontSize = 11.sp
                )
            }

            // Distancia
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd).padding(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White.copy(alpha = 0.85f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("300M", fontFamily = Urbanist,
                    fontWeight = FontWeight.SemiBold, fontSize = 12.sp,
                    color = Color(0xFF1C1C1C))
            }
        }

        // Contenido scrolleable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nombre + lÃ¡piz
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isEditing) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        textStyle = LocalTextStyle.current.copy(
                            fontFamily = Urbanist,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HuellitasTeal,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                } else {
                    Text(
                        text = editName,
                        fontFamily = Urbanist,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(36.dp).clip(CircleShape)
                        .background(
                            if (isEditing) Color(0xFFFFEEEE) else Color(0xFFE8F7F6)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {
                        if (isEditing) {
                            editName = alert.petName
                            editDescription = alert.description
                            editColor = alert.color ?: ""
                            isEditing = false
                        } else {
                            isEditing = true
                        }
                    }) {
                        Icon(
                            if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Cancelar" else "Editar",
                            tint = if (isEditing) Color.Red else HuellitasTeal,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Raza
            Text(
                text = buildString {
                    append(when (alert.petType) {
                        PetType.DOG -> "Perro"; PetType.CAT -> "Gato"; else -> "Animal"
                    })
                    if (!alert.breed.isNullOrBlank()) append(" Â· ${alert.breed}")
                },
                fontFamily = Urbanist, fontWeight = FontWeight.Normal,
                fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // UbicaciÃ³n
            if (!alert.location.address.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${alert.location.address} Â· ${timeAgo(alert.createdAt)}",
                        fontFamily = Urbanist, fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Grid de datos â€” usa colores del tema
            val gridBg = if (MaterialTheme.colorScheme.background == Color(0xFF1C1C1C) ||
                MaterialTheme.colorScheme.background.red < 0.2f)
                Color(0xFF1A3333) else Color(0xFFE8F7F6)

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = gridBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // COLOR editable
                        Column(modifier = Modifier.weight(1f)) {
                            Text("COLOR", fontFamily = Urbanist, fontWeight = FontWeight.Bold,
                                fontSize = 11.sp, color = HuellitasTeal, letterSpacing = 0.8.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            if (isEditing) {
                                BasicInlineField(
                                    value = editColor,
                                    onValueChange = { editColor = it },
                                    placeholder = "Ej. Dorado"
                                )
                            } else {
                                Text(
                                    editColor.ifBlank { "â€”" }, fontFamily = Urbanist,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                        DetailCell("COLLAR", "â€”", modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        DetailCell(
                            "ESPECIE",
                            when (alert.petType) {
                                PetType.DOG -> "Perro"; PetType.CAT -> "Gato"; else -> "Otro"
                            },
                            modifier = Modifier.weight(1f)
                        )
                        DetailCell(
                            "ESTADO",
                            if (alert.type == AlertType.LOST) "Perdido" else "Encontrado",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // DescripciÃ³n editable inline
            Column {
                Text(
                    "DESCRIPCIÃ“N",
                    fontFamily = Urbanist, fontWeight = FontWeight.Bold, fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.drawBehind {
                        val sw = 2.dp.toPx()
                        drawLine(HuellitasTeal, Offset(0f, this.size.height),
                            Offset(this.size.width, this.size.height), sw)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (isEditing) {
                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        placeholder = {
                            Text("Cualquier detalle que ayude...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = Urbanist)
                        },
                        singleLine = false,
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HuellitasTeal,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                } else {
                    Text(
                        text = editDescription,
                        fontFamily = Urbanist, fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // BotÃ³n guardar â€” solo en modo ediciÃ³n
            if (isEditing) {
                Button(
                    onClick = {
                        onUpdate(editName, editDescription, editColor)
                        isEditing = false
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HuellitasTeal)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar cambios", fontFamily = Urbanist,
                        fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White)
                }
            }

            // Botones normales â€” ocultos en modo ediciÃ³n
            if (!isEditing) {
                // Contactar por WhatsApp
                Button(
                    onClick = {
                        val phone = alert.contactPhone?.replace(Regex("[^0-9]"), "").orEmpty()
                        val msg = "Hola! Vi tu aviso en Huellitas sobre ${alert.petName}. Quiero mas info."
                        val url = "https://wa.me/$phone?text=${URLEncoder.encode(msg, "UTF-8")}"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = !alert.contactPhone.isNullOrBlank(),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HuellitasTeal)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null,
                        modifier = Modifier.size(18.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (alert.contactPhone.isNullOrBlank()) "Sin telefono de contacto" else "Contactar por WhatsApp", fontFamily = Urbanist,
                        fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.White)
                }

                // Resuelto / Eliminar
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { showResolveDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, HuellitasTeal)
                    ) {
                        Text("Resuelto âœ“", fontFamily = Urbanist,
                            fontWeight = FontWeight.Normal, color = HuellitasTeal)
                    }
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                    ) {
                        Text("Eliminar", fontFamily = Urbanist,
                            fontWeight = FontWeight.Normal, color = Color.Red)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BasicInlineField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, HuellitasTeal.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        if (value.isEmpty()) {
            Text(placeholder, fontFamily = Urbanist, fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        androidx.compose.foundation.text.BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = Urbanist, fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DetailCell(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, fontFamily = Urbanist, fontWeight = FontWeight.Bold,
            fontSize = 11.sp, color = HuellitasTeal, letterSpacing = 0.8.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontFamily = Urbanist, fontWeight = FontWeight.Normal,
            fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground)
    }
}

private fun timeAgo(timestamp: Long): String {
    val diff  = System.currentTimeMillis() - timestamp
    val mins  = diff / 60_000
    val hours = diff / 3_600_000
    val days  = diff / 86_400_000
    return when {
        mins  < 60 -> "Hace ${mins}min"
        hours < 24 -> "Hace ${hours}h"
        else       -> "Hace ${days}d"
    }
}
