package com.uade.huellitas.presentation.profile.alerts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uade.huellitas.domain.model.AlertStatus
import com.uade.huellitas.presentation.shared.AlertCard
import com.uade.huellitas.ui.theme.HuellitasTeal
import com.uade.huellitas.ui.theme.Urbanist

private enum class MyAlertFilter {
    ACTIVE,
    RESOLVED,
    ALL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAlertsScreen(
    viewModel: MyAlertsViewModel,
    onBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentFilter by remember { mutableStateOf(MyAlertFilter.ACTIVE) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis avisos",
                        fontFamily = Urbanist,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is MyAlertsUiState.Loading -> {
                    CircularProgressIndicator(
                        color = HuellitasTeal,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is MyAlertsUiState.Empty -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Sin avisos", fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Todavia no publicaste ningun aviso",
                            fontFamily = Urbanist,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                is MyAlertsUiState.Success -> {
                    val filteredAlerts = state.alerts.filter { alert ->
                        when (currentFilter) {
                            MyAlertFilter.ACTIVE -> alert.status == AlertStatus.ACTIVE
                            MyAlertFilter.RESOLVED -> alert.status == AlertStatus.RESOLVED
                            MyAlertFilter.ALL -> true
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            AlertsFilterRow(
                                currentFilter = currentFilter,
                                onChange = { currentFilter = it }
                            )
                        }
                        if (filteredAlerts.isEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 48.dp, horizontal = 24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = when (currentFilter) {
                                            MyAlertFilter.ACTIVE -> "No tenés avisos activos"
                                            MyAlertFilter.RESOLVED -> "Todavía no resolviste avisos"
                                            MyAlertFilter.ALL -> "Todavía no publicaste ningún aviso"
                                        },
                                        fontFamily = Urbanist,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        items(filteredAlerts, key = { it.id }) { alert ->
                            AlertCard(
                                alert = alert,
                                onClick = { onNavigateToDetail(alert.id) }
                            )
                        }
                    }
                }

                is MyAlertsUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            fontFamily = Urbanist,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.retry() },
                            colors = ButtonDefaults.buttonColors(containerColor = HuellitasTeal)
                        ) {
                            Text(
                                text = "Reintentar",
                                fontFamily = Urbanist,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AlertsFilterRow(
    currentFilter: MyAlertFilter,
    onChange: (MyAlertFilter) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MyAlertFilter.entries.forEach { filter ->
            val selected = filter == currentFilter
            Surface(
                onClick = { onChange(filter) },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(999.dp),
                color = if (selected) HuellitasTeal else Color(0xFFF0F0F0)
            ) {
                Text(
                    text = when (filter) {
                        MyAlertFilter.ACTIVE -> "Activos"
                        MyAlertFilter.RESOLVED -> "Resueltos"
                        MyAlertFilter.ALL -> "Todos"
                    },
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    fontFamily = Urbanist,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 13.sp,
                    color = if (selected) Color.White else Color(0xFF3D3D3D)
                )
            }
        }
    }
}
