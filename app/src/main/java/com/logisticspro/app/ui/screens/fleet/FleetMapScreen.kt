package com.logisticspro.app.ui.screens.fleet

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.logisticspro.app.data.model.RoutePoint

@Composable
fun FleetMapScreen(
    route: List<RoutePoint>,
    onMapLongClick: (Double, Double) -> Unit = { _, _ -> }
) {
    if (route.isEmpty()) return

    // Kamerayı ilk noktaya (depoya) odakla
    val startPos = LatLng(route.first().latitude, route.first().longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startPos, 12f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLongClick = { latLng ->
            onMapLongClick(latLng.latitude, latLng.longitude)
        },
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false
        )
    ) {
        val latLngList = mutableListOf<LatLng>()
        
        route.forEachIndexed { index, point ->
            val latLng = LatLng(point.latitude, point.longitude)
            val isStart = index == 0
            
            // Marker rengini belirle (Teslim edildi = Yeşil, Teslim edilmedi = Kırmızı, Başlangıç = Mavi)
            val markerColor = when {
                isStart -> BitmapDescriptorFactory.HUE_BLUE
                point.isDelivered -> BitmapDescriptorFactory.HUE_GREEN
                else -> BitmapDescriptorFactory.HUE_ORANGE
            }
            
            // Eğer nokta teslim edilmemişse veya başlangıç noktasıysa rota çizgisine ekle
            if (!point.isDelivered || isStart) {
                latLngList.add(latLng)
            }

            Marker(
                state = MarkerState(position = latLng),
                title = if (point.name.isNotEmpty()) point.name else "Nokta $index",
                snippet = if (point.isDelivered) "Teslim Edildi" else "Bekliyor",
                icon = BitmapDescriptorFactory.defaultMarker(markerColor)
            )
        }

        // Kalan teslimat noktaları arasında rota çizgisi çiz
        if (latLngList.size >= 2) {
            Polyline(
                points = latLngList,
                color = Color(0xFF0D47A1),
                width = 12f
            )
        }
    }
}
