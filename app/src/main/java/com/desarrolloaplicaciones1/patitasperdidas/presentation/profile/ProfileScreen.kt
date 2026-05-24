package com.desarrolloaplicaciones1.patitasperdidas.presentation.profile

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Alert
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.AlertType
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.User
import com.desarrolloaplicaciones1.patitasperdidas.ui.theme.HuellitasTeal
import com.desarrolloaplicaciones1.patitasperdidas.ui.theme.ThemeState
import com.desarrolloaplicaciones1.patitasperdidas.ui.theme.Urbanist
import android.content.Context
import androidx.compose.ui.platform.LocalContext
private val CardBg = Color(0xFFF5F9F9)
private val radiusOptions = listOf(1, 3, 5, 10, 20)

@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit,
    onNavigateToAlertDetail: (alertId: String) -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showLogoutDialog  by remember { mutableStateOf(false) }
    var temaOscuro        by remember { mutableStateOf(ThemeState.isDarkMode) }
    var modoOffline       by remember { mutableStateOf(true) }
    var selectedRadius    by remember { mutableIntStateOf(3) }
    var showRadiusDialog  by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesión", fontFamily = Urbanist, fontWeight = FontWeight.Bold) },
            text = { Text("¿Seguro que querés cerrar sesión?", fontFamily = Urbanist) },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; viewModel.logout(); onLogout() }) {
                    Text("Cerrar sesión", color = Color.Red, fontFamily = Urbanist)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar", fontFamily = Urbanist)
                }
            }
        )
    }

    if (showRadiusDialog) {
        var tempRadius by remember { mutableIntStateOf(selectedRadius) }
        AlertDialog(
            onDismissRequest = { showRadiusDialog = false },
            title = { Text("Radio de alertas", fontFamily = Urbanist, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("${tempRadius} km", fontFamily = Urbanist,
                        fontWeight = FontWeight.Bold, fontSize = 18.sp,
                        color = HuellitasTeal, modifier = Modifier.padding(bottom = 8.dp))
                    Slider(
                        value = radiusOptions.indexOf(tempRadius).toFloat(),
                        onValueChange = { tempRadius = radiusOptions[it.toInt()] },
                        valueRange = 0f..(radiusOptions.size - 1).toFloat(),
                        steps = radiusOptions.size - 2,
                        colors = SliderDefaults.colors(
                            thumbColor = HuellitasTeal,
                            activeTrackColor = HuellitasTeal,
                            inactiveTrackColor = Color(0xFFDDDDDD)
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        radiusOptions.forEach { km ->
                            Text("${km}km", fontFamily = Urbanist, fontSize = 12.sp,
                                color = if (tempRadius == km) HuellitasTeal else Color(0xFF888888),
                                fontWeight = if (tempRadius == km) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { selectedRadius = tempRadius; showRadiusDialog = false },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HuellitasTeal)) {
                    Text("Guardar", fontFamily = Urbanist, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRadiusDialog = false }) {
                    Text("Cancelar", fontFamily = Urbanist, color = Color.Gray)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (temaOscuro) Color(0xFF1C1C1C) else Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header — solo flecha volver
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
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver",
                        tint = HuellitasTeal, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Perfil", fontFamily = Urbanist, fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = if (temaOscuro) Color.White else Color(0xFF1C1C1C))
        }

        // User info
        when (val state = uiState) {
            is ProfileUiState.Success -> UserSection(user = state.user, darkMode = temaOscuro)
            else -> MockUserSection(darkMode = temaOscuro)
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp),
            color = if (temaOscuro) Color(0xFF333333) else Color(0xFFEEEEEE))
        Spacer(modifier = Modifier.height(16.dp))

        // MIS REPORTES
        SectionTitle("MIS REPORTES")
        Spacer(modifier = Modifier.height(8.dp))

        val cardBgDynamic = if (temaOscuro) Color(0xFF2A2A2A) else CardBg

        when (val state = uiState) {
            is ProfileUiState.Success -> {
                if (state.userAlerts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp)).background(cardBgDynamic).padding(24.dp),
                        contentAlignment = Alignment.Center) {
                        Text("Todavía no publicaste ningún aviso",
                            fontFamily = Urbanist, fontSize = 14.sp, color = Color(0xFF888888))
                    }
                } else {
                    Surface(shape = RoundedCornerShape(12.dp), color = cardBgDynamic,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                        Column {
                            state.userAlerts.forEachIndexed { index, alert ->
                                ReporteItem(alert = alert, darkMode = temaOscuro,
                                    onClick = { onNavigateToAlertDetail(alert.id) })
                                if (index < state.userAlerts.lastIndex) {
                                    HorizontalDivider(color = if (temaOscuro) Color(0xFF333333) else Color(0xFFEEEEEE),
                                        modifier = Modifier.padding(horizontal = 12.dp))
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                Surface(shape = RoundedCornerShape(12.dp), color = cardBgDynamic,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Column {
                        MockReporteItem("Max", "Perdido · Hace 2 días", darkMode = temaOscuro, onClick = {})
                        HorizontalDivider(color = if (temaOscuro) Color(0xFF333333) else Color(0xFFEEEEEE),
                            modifier = Modifier.padding(horizontal = 12.dp))
                        MockReporteItem("Michi", "Encontrado · Hace 1 semana", darkMode = temaOscuro, onClick = {})
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp),
            color = if (temaOscuro) Color(0xFF333333) else Color(0xFFEEEEEE))
        Spacer(modifier = Modifier.height(16.dp))

        // CONFIGURACIÓN
        SectionTitle("CONFIGURACIÓN")
        Spacer(modifier = Modifier.height(8.dp))

        Surface(shape = RoundedCornerShape(12.dp), color = cardBgDynamic,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Column {
                // Radio de alertas
                Surface(onClick = { showRadiusDialog = true }, color = Color.Transparent,
                    modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null,
                            tint = if (temaOscuro) Color.White else Color(0xFF3D3D3D),
                            modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Radio de alertas", fontFamily = Urbanist, fontSize = 15.sp,
                            color = if (temaOscuro) Color.White else Color(0xFF1C1C1C),
                            modifier = Modifier.weight(1f))
                        Text("$selectedRadius km", fontFamily = Urbanist, fontSize = 14.sp,
                            color = HuellitasTeal, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ChevronRight, contentDescription = null,
                            tint = Color(0xFF888888), modifier = Modifier.size(18.dp))
                    }
                }

                HorizontalDivider(color = if (temaOscuro) Color(0xFF333333) else Color(0xFFEEEEEE),
                    modifier = Modifier.padding(horizontal = 12.dp))

                // Tema oscuro
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DarkMode, contentDescription = null,
                        tint = if (temaOscuro) Color.White else Color(0xFF3D3D3D),
                        modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Tema oscuro", fontFamily = Urbanist, fontSize = 15.sp,
                        color = if (temaOscuro) Color.White else Color(0xFF1C1C1C),
                        modifier = Modifier.weight(1f))
                    Switch(
                        checked = temaOscuro,
                        onCheckedChange = {
                            temaOscuro = it
                            ThemeState.isDarkMode = it
                            context.getSharedPreferences("huellitas_prefs", Context.MODE_PRIVATE)
                                .edit()
                                .putBoolean("dark_mode", it)
                                .apply()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = HuellitasTeal
                        )
                    )
                }

                HorizontalDivider(color = if (temaOscuro) Color(0xFF333333) else Color(0xFFEEEEEE),
                    modifier = Modifier.padding(horizontal = 12.dp))

                // Modo offline
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WifiOff, contentDescription = null,
                        tint = if (modoOffline) HuellitasTeal else Color(0xFF3D3D3D),
                        modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Modo offline", fontFamily = Urbanist, fontSize = 15.sp,
                        color = if (modoOffline) HuellitasTeal else if (temaOscuro) Color.White else Color(0xFF1C1C1C),
                        modifier = Modifier.weight(1f))
                    Switch(
                        checked = modoOffline,
                        onCheckedChange = { modoOffline = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = HuellitasTeal
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp),
            color = if (temaOscuro) Color(0xFF333333) else Color(0xFFEEEEEE))
        Spacer(modifier = Modifier.height(16.dp))

        // CUENTA
        SectionTitle("CUENTA")
        Spacer(modifier = Modifier.height(8.dp))

        Surface(shape = RoundedCornerShape(12.dp), color = cardBgDynamic,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Column {
                CuentaItem("Editar perfil", darkMode = temaOscuro, onClick = onNavigateToEditProfile)
                HorizontalDivider(color = if (temaOscuro) Color(0xFF333333) else Color(0xFFEEEEEE),
                    modifier = Modifier.padding(horizontal = 12.dp))
                CuentaItem("Cerrar sesión", isDestructive = true, darkMode = temaOscuro,
                    onClick = { showLogoutDialog = true })
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun UserSection(user: User, darkMode: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(HuellitasTeal),
            contentAlignment = Alignment.Center) {
            Text(user.name.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                fontFamily = Urbanist, fontWeight = FontWeight.Bold,
                fontSize = 26.sp, color = Color.White)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(user.name, fontFamily = Urbanist, fontWeight = FontWeight.Bold,
                fontSize = 18.sp, color = if (darkMode) Color.White else Color(0xFF1C1C1C))
            Text(user.email, fontFamily = Urbanist, fontSize = 14.sp, color = Color(0xFF888888))
        }
    }
}

@Composable
private fun MockUserSection(darkMode: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(HuellitasTeal),
            contentAlignment = Alignment.Center) {
            Text("V", fontFamily = Urbanist, fontWeight = FontWeight.Bold,
                fontSize = 26.sp, color = Color.White)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text("Valentin Arce", fontFamily = Urbanist, fontWeight = FontWeight.Bold,
                fontSize = 18.sp, color = if (darkMode) Color.White else Color(0xFF1C1C1C))
            Text("test@gmail.com", fontFamily = Urbanist, fontSize = 14.sp, color = Color(0xFF888888))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontFamily = Urbanist,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        color = if (ThemeState.isDarkMode) Color.White else HuellitasTeal,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun ReporteItem(alert: Alert, darkMode: Boolean, onClick: () -> Unit) {
    Surface(onClick = onClick, color = Color.Transparent) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFDDEEEE)),
                contentAlignment = Alignment.Center) {
                Text("🐾", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(alert.petName, fontFamily = Urbanist, fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp, color = if (darkMode) Color.White else Color(0xFF1C1C1C))
                Text("${if (alert.type == AlertType.LOST) "Perdido" else "Encontrado"} · ${timeAgo(alert.createdAt)}",
                    fontFamily = Urbanist, fontSize = 13.sp, color = Color(0xFF888888))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null,
                tint = Color(0xFF888888), modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun MockReporteItem(nombre: String, estado: String, darkMode: Boolean, onClick: () -> Unit) {
    Surface(onClick = onClick, color = Color.Transparent) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFDDEEEE)),
                contentAlignment = Alignment.Center) {
                Text("🐾", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(nombre, fontFamily = Urbanist, fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp, color = if (darkMode) Color.White else Color(0xFF1C1C1C))
                Text(estado, fontFamily = Urbanist, fontSize = 13.sp, color = Color(0xFF888888))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null,
                tint = Color(0xFF888888), modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun CuentaItem(texto: String, isDestructive: Boolean = false,
                       darkMode: Boolean = false, onClick: () -> Unit) {
    Surface(onClick = onClick, color = Color.Transparent) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(texto, fontFamily = Urbanist, fontSize = 15.sp,
                color = if (isDestructive) Color.Red else if (darkMode) Color.White else Color(0xFF1C1C1C),
                modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null,
                tint = Color(0xFF888888), modifier = Modifier.size(18.dp))
        }
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