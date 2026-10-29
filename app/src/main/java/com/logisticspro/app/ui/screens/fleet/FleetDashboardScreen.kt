package com.logisticspro.app.ui.screens.fleet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.logisticspro.app.data.model.RoutePoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FleetDashboardScreen() {
    // Mock route for demonstration
    val mockRoute = listOf(
        RoutePoint(41.0082, 28.9784), // Istanbul
        RoutePoint(41.0150, 28.9790),
        RoutePoint(41.0250, 28.9850),
        RoutePoint(40.9833, 29.1167)  // Kadıköy
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Filo Takip Paneli") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Box(modifier = Modifier.weight(1f)) {
                FleetMapScreen(route = mockRoute)
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Aktif Teslimat: Kadıköy Depo", style = MaterialTheme.typography.headlineSmall)
                    Text("Durum: Yolda", color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { /* Update status */ }) {
                        Text("Teslim Edildi İşaretle")
                    }
                }
            }
        }
    }
}
