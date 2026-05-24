package com.desarrolloaplicaciones1.patitasperdidas.presentation.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.AlertType
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.PetType
import com.desarrolloaplicaciones1.patitasperdidas.ui.theme.HuellitasTeal
import com.desarrolloaplicaciones1.patitasperdidas.ui.theme.Urbanist

@Composable
fun ExpressAlertScreen(
    onBack: () -> Unit,
    onPublished: () -> Unit = {}
) {
    var alertType   by remember { mutableStateOf(AlertType.LOST) }
    var petName     by remember { mutableStateOf("") }
    var petType     by remember { mutableStateOf(PetType.DOG) }
    var size        by remember { mutableStateOf("Chico") }
    var description by remember { mutableStateOf("") }
    var barrio      by remember { mutableStateOf("") }
    var barrioError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reporte express",
                fontFamily = Urbanist,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = Color(0xFF1C1C1C),
                modifier = Modifier.drawBehind {
                    val strokeWidth = 2.dp.toPx()
                    val y = this.size.height
                    drawLine(
                        color = HuellitasTeal,
                        start = Offset(0f, y),
                        end = Offset(this.size.width, y),
                        strokeWidth = strokeWidth
                    )
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

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Banner info
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFE8F7F6),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null,
                        tint = HuellitasTeal, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Completá lo que puedas. Cada dato ayuda.",
                        fontFamily = Urbanist,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = HuellitasTeal
                    )
                }
            }

            // ESTADO
            ExpressLabel("ESTADO")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AlertType.entries.forEach { type ->
                    val isSelected = alertType == type
                    val bgColor = when {
                        isSelected && type == AlertType.LOST  -> Color(0xFFF43F47)
                        isSelected && type == AlertType.FOUND -> Color(0xFF43A047)
                        else -> Color(0xFFF5F5F5)
                    }
                    Button(
                        onClick = { alertType = type },
                        modifier = Modifier.weight(1f).height(42.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = bgColor,
                            contentColor = if (isSelected) Color.White else Color(0xFF888888)
                        )
                    ) {
                        Text(
                            text = if (type == AlertType.LOST) "Perdido" else "Encontrado",
                            fontFamily = Urbanist,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // NOMBRE DEL ANIMAL
            ExpressLabel("NOMBRE DEL ANIMAL")
            OutlinedTextField(
                value = petName,
                onValueChange = { petName = it },
                placeholder = { Text("Ej: Buddy, Luna...", color = Color.Gray, fontFamily = Urbanist) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(3.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HuellitasTeal,
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            // ESPECIE
            ExpressLabel("ESPECIE")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ExpressChip("Perro", petType == PetType.DOG) { petType = PetType.DOG }
                ExpressChip("Gato",  petType == PetType.CAT) { petType = PetType.CAT }
            }

            // TAMAÑO
            ExpressLabel("TAMAÑO")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Chico", "Mediano", "Grande").forEach { s ->
                    ExpressChip(s, size == s) { size = s }
                }
            }

            // DESCRIPCIÓN
            ExpressLabel("DESCRIPCIÓN")
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Cualquier detalle que ayude....", color = Color.Gray, fontFamily = Urbanist) },
                singleLine = false,
                minLines = 4,
                maxLines = 6,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(3.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HuellitasTeal,
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            // BARRIO
            ExpressLabel("BARRIO")
            OutlinedTextField(
                value = barrio,
                onValueChange = { barrio = it; barrioError = null },
                placeholder = { Text("Ej: Palermo, CABA", color = Color.Gray, fontFamily = Urbanist) },
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

            Spacer(modifier = Modifier.height(8.dp))
        }

        Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
            Button(
                onClick = {
                    barrioError = if (barrio.isBlank()) "El barrio es obligatorio" else null
                    if (barrioError == null) onPublished()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding()
                    .height(52.dp),
                shape = RoundedCornerShape(3.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HuellitasTeal)
            ) {
                Text("Publicar alerta", fontFamily = Urbanist,
                    fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White)
            }
        }
    }
}

@Composable
private fun ExpressLabel(text: String) {
    Text(text = text, fontFamily = Urbanist, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, color = Color(0xFF1C1C1C))
}

@Composable
private fun ExpressChip(label: String, selected: Boolean, onClick: () -> Unit) {
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