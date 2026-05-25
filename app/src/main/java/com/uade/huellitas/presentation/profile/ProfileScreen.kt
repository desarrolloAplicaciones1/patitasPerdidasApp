package com.uade.huellitas.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uade.huellitas.domain.model.Alert
import com.uade.huellitas.domain.model.AlertType
import com.uade.huellitas.domain.model.User
import com.uade.huellitas.ui.theme.HuellitasTeal
import com.uade.huellitas.ui.theme.ThemeState
import com.uade.huellitas.ui.theme.Urbanist

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
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showRadiusDialog by remember { mutableStateOf(false) }
    var tempRadius by remember { mutableIntStateOf(settings.alertRadiusKm) }
    val isDark = settings.darkModeEnabled

    LaunchedEffect(settings.darkModeEnabled) {
        ThemeState.isDarkMode = settings.darkModeEnabled
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesion", fontFamily = Urbanist, fontWeight = FontWeight.Bold) },
            text = { Text("Seguro que queres cerrar sesion?", fontFamily = Urbanist) },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; viewModel.logout(); onLogout() }) {
                    Text("Cerrar sesion", color = Color.Red, fontFamily = Urbanist)
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
        AlertDialog(
            onDismissRequest = { showRadiusDialog = false },
            title = { Text("Radio de alertas", fontFamily = Urbanist, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "${tempRadius} km",
                        fontFamily = Urbanist,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = HuellitasTeal,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
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
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        radiusOptions.forEach { km ->
                            Text(
                                "${km}km",
                                fontFamily = Urbanist,
                                fontSize = 12.sp,
                                color = if (tempRadius == km) HuellitasTeal else Color(0xFF888888),
                                fontWeight = if (tempRadius == km) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setAlertRadius(tempRadius)
                        showRadiusDialog = false
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HuellitasTeal)
                ) {
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
            .background(if (isDark) Color(0xFF1C1C1C) else Color.White)
            .verticalScroll(rememberScrollState())
    ) {
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
            Text("Perfil", fontFamily = Urbanist, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = if (isDark) Color.White else Color(0xFF1C1C1C))
        }

        when (val state = uiState) {
            is ProfileUiState.Success -> UserSection(user = state.user, darkMode = isDark)
            is ProfileUiState.Error -> ProfileStatusSection(
                title = "Perfil no disponible",
                subtitle = state.message,
                darkMode = isDark
            )
            ProfileUiState.Loading -> ProfileStatusSection(
                title = "Cargando perfil",
                subtitle = "Estamos recuperando tus datos.",
                darkMode = isDark
            )
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = if (isDark) Color(0xFF333333) else Color(0xFFEEEEEE))
        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("MIS REPORTES", darkMode = isDark)
        Spacer(modifier = Modifier.height(8.dp))

        val cardBgDynamic = if (isDark) Color(0xFF2A2A2A) else CardBg

        when (val state = uiState) {
            is ProfileUiState.Success -> {
                if (state.userAlerts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(cardBgDynamic)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Todavia no publicaste ningun aviso", fontFamily = Urbanist, fontSize = 14.sp, color = Color(0xFF888888))
                    }
                } else {
                    Surface(shape = RoundedCornerShape(12.dp), color = cardBgDynamic, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                        Column {
                            state.userAlerts.forEachIndexed { index, alert ->
                                ReporteItem(alert = alert, darkMode = isDark, onClick = { onNavigateToAlertDetail(alert.id) })
                                if (index < state.userAlerts.lastIndex) {
                                    HorizontalDivider(color = if (isDark) Color(0xFF333333) else Color(0xFFEEEEEE), modifier = Modifier.padding(horizontal = 12.dp))
                                }
                            }
                        }
                    }
                }
            }
            is ProfileUiState.Error -> {
                ProfileMessageCard(
                    message = state.message,
                    darkMode = isDark,
                    backgroundColor = cardBgDynamic
                )
            }
            ProfileUiState.Loading -> {
                ProfileMessageCard(
                    message = "Cargando tus reportes...",
                    darkMode = isDark,
                    backgroundColor = cardBgDynamic
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = if (isDark) Color(0xFF333333) else Color(0xFFEEEEEE))
        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("CONFIGURACION", darkMode = isDark)
        Spacer(modifier = Modifier.height(8.dp))

        Surface(shape = RoundedCornerShape(12.dp), color = cardBgDynamic, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Column {
                Surface(onClick = {
                    tempRadius = settings.alertRadiusKm
                    showRadiusDialog = true
                }, color = Color.Transparent, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = if (isDark) Color.White else Color(0xFF3D3D3D), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Radio de alertas", fontFamily = Urbanist, fontSize = 15.sp, color = if (isDark) Color.White else Color(0xFF1C1C1C), modifier = Modifier.weight(1f))
                        Text("${settings.alertRadiusKm} km", fontFamily = Urbanist, fontSize = 14.sp, color = HuellitasTeal, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF888888), modifier = Modifier.size(18.dp))
                    }
                }

                HorizontalDivider(color = if (isDark) Color(0xFF333333) else Color(0xFFEEEEEE), modifier = Modifier.padding(horizontal = 12.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DarkMode, contentDescription = null, tint = if (isDark) Color.White else Color(0xFF3D3D3D), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Tema oscuro", fontFamily = Urbanist, fontSize = 15.sp, color = if (isDark) Color.White else Color(0xFF1C1C1C), modifier = Modifier.weight(1f))
                    Switch(
                        checked = settings.darkModeEnabled,
                        onCheckedChange = { viewModel.setDarkMode(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = HuellitasTeal)
                    )
                }

                HorizontalDivider(color = if (isDark) Color(0xFF333333) else Color(0xFFEEEEEE), modifier = Modifier.padding(horizontal = 12.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WifiOff, contentDescription = null, tint = if (settings.offlineModeEnabled) HuellitasTeal else Color(0xFF3D3D3D), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Modo offline",
                        fontFamily = Urbanist,
                        fontSize = 15.sp,
                        color = if (settings.offlineModeEnabled) HuellitasTeal else if (isDark) Color.White else Color(0xFF1C1C1C),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = settings.offlineModeEnabled,
                        onCheckedChange = { viewModel.setOfflineMode(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = HuellitasTeal)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = if (isDark) Color(0xFF333333) else Color(0xFFEEEEEE))
        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("CUENTA", darkMode = isDark)
        Spacer(modifier = Modifier.height(8.dp))

        Surface(shape = RoundedCornerShape(12.dp), color = cardBgDynamic, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Column {
                if (uiState is ProfileUiState.Success) {
                    CuentaItem("Editar perfil", darkMode = isDark, onClick = onNavigateToEditProfile)
                    HorizontalDivider(color = if (isDark) Color(0xFF333333) else Color(0xFFEEEEEE), modifier = Modifier.padding(horizontal = 12.dp))
                }
                CuentaItem("Cerrar sesion", isDestructive = true, darkMode = isDark, onClick = { showLogoutDialog = true })
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun UserSection(user: User, darkMode: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(HuellitasTeal), contentAlignment = Alignment.Center) {
            Text(user.name.firstOrNull()?.uppercaseChar()?.toString() ?: "U", fontFamily = Urbanist, fontWeight = FontWeight.Bold, fontSize = 26.sp, color = Color.White)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(user.name, fontFamily = Urbanist, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (darkMode) Color.White else Color(0xFF1C1C1C))
            Text(user.email, fontFamily = Urbanist, fontSize = 14.sp, color = Color(0xFF888888))
            if (!user.location.isNullOrBlank()) {
                Text(user.location, fontFamily = Urbanist, fontSize = 13.sp, color = Color(0xFF888888))
            }
        }
    }
}

@Composable
private fun ProfileStatusSection(title: String, subtitle: String, darkMode: Boolean) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
            text = title,
            fontFamily = Urbanist,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = if (darkMode) Color.White else Color(0xFF1C1C1C)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            fontFamily = Urbanist,
            fontSize = 14.sp,
            color = Color(0xFF888888)
        )
    }
}

@Composable
private fun SectionTitle(text: String, darkMode: Boolean) {
    Text(
        text = text,
        fontFamily = Urbanist,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        color = if (darkMode) Color.White else HuellitasTeal,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun ReporteItem(alert: Alert, darkMode: Boolean, onClick: () -> Unit) {
    Surface(onClick = onClick, color = Color.Transparent) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFDDEEEE)), contentAlignment = Alignment.Center) {
                Text("Pet", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(alert.petName, fontFamily = Urbanist, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = if (darkMode) Color.White else Color(0xFF1C1C1C))
                Text("${if (alert.type == AlertType.LOST) "Perdido" else "Encontrado"} - ${timeAgo(alert.createdAt)}", fontFamily = Urbanist, fontSize = 13.sp, color = Color(0xFF888888))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF888888), modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun ProfileMessageCard(message: String, darkMode: Boolean, backgroundColor: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                fontFamily = Urbanist,
                fontSize = 14.sp,
                color = if (darkMode) Color(0xFFBDBDBD) else Color(0xFF666666)
            )
        }
    }
}

@Composable
private fun CuentaItem(texto: String, isDestructive: Boolean = false, darkMode: Boolean = false, onClick: () -> Unit) {
    Surface(onClick = onClick, color = Color.Transparent) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(texto, fontFamily = Urbanist, fontSize = 15.sp, color = if (isDestructive) Color.Red else if (darkMode) Color.White else Color(0xFF1C1C1C), modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF888888), modifier = Modifier.size(18.dp))
        }
    }
}

private fun timeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val mins = diff / 60_000
    val hours = diff / 3_600_000
    val days = diff / 86_400_000
    return when {
        mins < 60 -> "Hace ${mins}min"
        hours < 24 -> "Hace ${hours}h"
        else -> "Hace ${days}d"
    }
}

