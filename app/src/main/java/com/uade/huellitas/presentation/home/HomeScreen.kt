package com.uade.huellitas.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.uade.huellitas.domain.model.Alert
import com.uade.huellitas.domain.model.AlertType
import com.uade.huellitas.domain.model.PetType
import com.uade.huellitas.presentation.location.RequestLocationPermissionEffect
import com.uade.huellitas.ui.theme.HuellitasTeal
import com.uade.huellitas.ui.theme.HuellitasTealSurface
import com.uade.huellitas.ui.theme.StatusFound
import com.uade.huellitas.ui.theme.StatusLost
import com.uade.huellitas.ui.theme.ThemeState
import com.uade.huellitas.ui.theme.Urbanist

private val FooterColor = Color(0xFFE8F7F6)
private val radiusOptions = listOf(1, 3, 5, 10, 20)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (alertId: String) -> Unit,
    onLogout: () -> Unit,
    onNavigateToCreateAlert: () -> Unit = {},
    onNavigateToExpressAlert: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState     by viewModel.uiState.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val currentUserName = (uiState as? HomeUiState.Success)?.currentUserName
    var showFilterDialog by remember { mutableStateOf(false) }
    var showReportSheet  by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    RequestLocationPermissionEffect(
        onPermissionGranted = viewModel::refreshReferenceLocation
    )

    if (showFilterDialog) {
        FilterDialog(
            currentPetType   = filterState.petType,
            currentAlertType = filterState.alertType,
            currentRadiusKm  = filterState.radiusKm,
            onApply = { petType, alertType, radiusKm ->
                viewModel.setFilter(petType = petType, alertType = alertType, radiusKm = radiusKm)
                showFilterDialog = false
            },
            onClearFilter = { viewModel.clearFilter(); showFilterDialog = false },
            onDismiss = { showFilterDialog = false }
        )
    }

    if (showReportSheet) {
        ModalBottomSheet(
            onDismissRequest = { showReportSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .navigationBarsPadding()
            ) {
                Text("¿Qué querés reportar?", fontFamily = Urbanist, fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        onClick = { showReportSheet = false; onNavigateToCreateAlert() },
                        modifier = Modifier.weight(1f).height(140.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFE8F7F6),
                        border = BorderStroke(1.5.dp, HuellitasTeal)
                    ) {
                        Column(modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null,
                                tint = HuellitasTeal, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Me perdí / Encontré", fontFamily = Urbanist,
                                fontWeight = FontWeight.Bold, fontSize = 14.sp,
                                color = Color(0xFF1C1C1C), textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Para reportes detallados", fontFamily = Urbanist,
                                fontSize = 12.sp, color = Color(0xFF888888), textAlign = TextAlign.Center)
                        }
                    }
                    Surface(
                        onClick = { showReportSheet = false; onNavigateToExpressAlert() },
                        modifier = Modifier.weight(1f).height(140.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFE8F7F6),
                        border = BorderStroke(1.5.dp, HuellitasTeal)
                    ) {
                        Column(modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.FlashOn, contentDescription = null,
                                tint = HuellitasTeal, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Vi algo rápido", fontFamily = Urbanist,
                                fontWeight = FontWeight.Bold, fontSize = 14.sp,
                                color = Color(0xFF1C1C1C), textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Sin foto, solo zona", fontFamily = Urbanist,
                                fontSize = 12.sp, color = Color(0xFF888888), textAlign = TextAlign.Center)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().padding(bottom = 72.dp)) {
            HomeHeader(
                userName = currentUserName,
                onFilterClick = { showFilterDialog = true }
            )
            CreateAlertBanner(onClick = { showReportSheet = true })

            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = HuellitasTeal)
                    }
                }
                is HomeUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is HomeUiState.Success -> {
                    if (state.alerts.isEmpty()) {
                        EmptyAlertsState(modifier = Modifier.fillMaxSize())
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.alerts, key = { it.id }) { alert ->
                                AlertCard(alert = alert, onClick = { onNavigateToDetail(alert.id) })
                            }
                        }
                    }
                }
            }
        }

        HomeBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            onHome = {},
            onAdd = { showReportSheet = true },
            onMap = onNavigateToMap,
            onProfile = onNavigateToProfile
        )
    }
}

@Composable
private fun HomeHeader(userName: String?, onFilterClick: () -> Unit) {
    val greeting = userName
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?.let { "Hola, $it!" }
        ?: "Hola!"

    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 72.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(greeting, fontFamily = Urbanist, fontWeight = FontWeight.Normal,
                fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("¿Todo listo para ayudar?", fontFamily = Urbanist, fontWeight = FontWeight.Bold,
                fontSize = 22.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        IconButton(onClick = onFilterClick) {
            Icon(Icons.Default.FilterList, contentDescription = "Filtrar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(4.dp))
        Box(modifier = Modifier.size(40.dp).clip(CircleShape)
            .background(FooterColor), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones",
                tint = Color.Black, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun FilterDialog(
    currentPetType: PetType?,
    currentAlertType: AlertType?,
    currentRadiusKm: Int,
    onApply: (PetType?, AlertType?, Int) -> Unit,
    onClearFilter: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedPetType   by remember { mutableStateOf(currentPetType) }
    var selectedAlertType by remember { mutableStateOf(currentAlertType) }
    var selectedRadius    by remember { mutableIntStateOf(currentRadiusKm) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Filtrar avisos", fontFamily = Urbanist,
                    fontWeight = FontWeight.Bold, fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Tipo de aviso", fontFamily = Urbanist, fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChipOption("Perdidos", selectedAlertType == AlertType.LOST) {
                        selectedAlertType = if (selectedAlertType == AlertType.LOST) null else AlertType.LOST
                    }
                    FilterChipOption("Encontrados", selectedAlertType == AlertType.FOUND) {
                        selectedAlertType = if (selectedAlertType == AlertType.FOUND) null else AlertType.FOUND
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Especie", fontFamily = Urbanist, fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChipOption("Perros", selectedPetType == PetType.DOG) {
                        selectedPetType = if (selectedPetType == PetType.DOG) null else PetType.DOG
                    }
                    FilterChipOption("Gatos", selectedPetType == PetType.CAT) {
                        selectedPetType = if (selectedPetType == PetType.CAT) null else PetType.CAT
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Rango de cercanía", fontFamily = Urbanist, fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))
                val sliderIndex = radiusOptions.indexOf(selectedRadius).takeIf { it >= 0 } ?: 2
                Slider(
                    value = sliderIndex.toFloat(),
                    onValueChange = { selectedRadius = radiusOptions[it.toInt()] },
                    valueRange = 0f..(radiusOptions.size - 1).toFloat(),
                    steps = radiusOptions.size - 2,
                    colors = SliderDefaults.colors(thumbColor = HuellitasTeal,
                        activeTrackColor = HuellitasTeal, inactiveTrackColor = Color(0xFFDDDDDD)),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    radiusOptions.forEach { km ->
                        Box(modifier = Modifier.clip(RoundedCornerShape(20.dp))
                            .background(if (selectedRadius == km) HuellitasTeal else Color.Transparent)
                            .padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text("${km}km", fontFamily = Urbanist, fontSize = 12.sp,
                                fontWeight = if (selectedRadius == km) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedRadius == km) Color.White else Color(0xFF888888))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onClearFilter, modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)) {
                        Text("Limpiar", fontFamily = Urbanist, color = Color.Gray)
                    }
                    Button(onClick = { onApply(selectedPetType, selectedAlertType, selectedRadius) },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HuellitasTeal)) {
                        Text("Aplicar", fontFamily = Urbanist, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp))
        .background(if (selected) HuellitasTeal else Color(0xFFF0F0F0))
        .clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(label, fontFamily = Urbanist, fontWeight = FontWeight.Medium, fontSize = 13.sp,
            color = if (selected) Color.White else Color(0xFF3D3D3D))
    }
}

@Composable
private fun CreateAlertBanner(onClick: () -> Unit) {
    val isDark = ThemeState.isDarkMode
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isDark) HuellitasTeal else HuellitasTealSurface,
        contentColor = if (isDark) Color.White else HuellitasTeal
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(6.dp))
                    .background(if (isDark) Color.White.copy(alpha = 0.2f) else HuellitasTeal),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.FlashOn, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("Reportar mascota", fontFamily = Urbanist,
                    fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
            Icon(Icons.Default.ArrowForward, contentDescription = null,
                modifier = Modifier.size(18.dp))
        }
    }
}
@Composable
private fun AlertCard(alert: Alert, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center) {
                if (alert.photoUrls.isNotEmpty()) {
                    AsyncImage(model = alert.photoUrls.first(),
                        contentDescription = "Foto de ${alert.petName}",
                        contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                } else {
                    AsyncImage(
                        model = "https://images.dog.ceo/breeds/retriever-golden/n02099601_3004.jpg",
                        contentDescription = "Sin foto",
                        contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                }
            }
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp))
                    .background(if (alert.type == AlertType.LOST) StatusLost else StatusFound)
                    .padding(horizontal = 8.dp, vertical = 3.dp)) {
                    Text(text = if (alert.type == AlertType.LOST) "PERDIDO" else "ENCONTRADO",
                        color = Color.White, fontFamily = Urbanist, fontWeight = FontWeight.Bold,
                        fontSize = 10.sp, letterSpacing = 0.5.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = alert.petName, fontFamily = Urbanist, fontWeight = FontWeight.Bold,
                    fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
                if (!alert.color.isNullOrBlank()) {
                    Text(text = alert.color!!, fontFamily = Urbanist,
                        fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!alert.location.address.isNullOrBlank()) {
                        Icon(Icons.Default.LocationOn, contentDescription = null,
                            tint = HuellitasTeal, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = alert.location.address!!, fontFamily = Urbanist,
                            fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(" · ", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                    Text(text = timeAgo(alert.createdAt), fontFamily = Urbanist,
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun EmptyAlertsState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text("🔍", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("No encontramos avisos cercanos", fontFamily = Urbanist,
                fontWeight = FontWeight.SemiBold, fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun HomeBottomBar(
    modifier: Modifier = Modifier,
    onHome: () -> Unit,
    onAdd: () -> Unit,
    onMap: () -> Unit,
    onProfile: () -> Unit
) {
    val isDark = ThemeState.isDarkMode
    val bgColor = if (isDark) Color(0xFF1C2626) else Color(0xFFE8F7F6)
    val iconColor = if (isDark) Color.White else Color.Black

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp)
            .background(bgColor)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(64.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(onClick = onHome) {
                Icon(Icons.Default.Home, contentDescription = "Inicio",
                    tint = iconColor, modifier = Modifier.size(26.dp))
            }
            IconButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = "Publicar",
                    tint = iconColor, modifier = Modifier.size(26.dp))
            }
            IconButton(onClick = onMap) {
                Icon(Icons.Default.Map, contentDescription = "Mapa",
                    tint = iconColor, modifier = Modifier.size(26.dp))
            }
            IconButton(onClick = onProfile) {
                Icon(Icons.Default.Person, contentDescription = "Perfil",
                    tint = iconColor, modifier = Modifier.size(26.dp))
            }
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
