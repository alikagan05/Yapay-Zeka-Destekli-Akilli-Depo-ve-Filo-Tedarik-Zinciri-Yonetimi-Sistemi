package com.logisticspro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logisticspro.app.data.model.RoutePoint
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeUiEvent {
    data class ShowToast(val message: String) : HomeUiEvent()
    object ShowStockUpdateDialog : HomeUiEvent()
    object OpenBarcodeScanner : HomeUiEvent()
    data class ShowDeliveryConfirmation(val barcode: String) : HomeUiEvent()
}

class HomeViewModel : ViewModel() {
    private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // Teslimat rotası state'i
    private val _routePoints = MutableStateFlow(
        optimizeRoute(
            listOf(
                RoutePoint(41.0082, 28.9784, "Ana Depo"),
                RoutePoint(41.0367, 28.9850, "Taksim Şube"),
                RoutePoint(41.0150, 28.9390, "Fatih Depo"),
                RoutePoint(41.0250, 28.9750, "Galata Teslimat"),
                RoutePoint(41.0450, 29.0050, "Beşiktaş Müşteri")
            )
        )
    )
    val routePoints: StateFlow<List<RoutePoint>> = _routePoints.asStateFlow()

    fun markDelivered(index: Int) {
        viewModelScope.launch {
            val currentList = _routePoints.value.toMutableList()
            if (index in currentList.indices && index != 0) { // 0 = Depo, teslim edilemez
                val deliveredPointName = currentList[index].name
                currentList[index] = currentList[index].copy(isDelivered = true)
                _routePoints.value = optimizeRoute(currentList)
                _uiEvent.emit(HomeUiEvent.ShowToast("✅ $deliveredPointName teslim edildi! Rota güncellendi."))
            }
        }
    }

    fun addRoutePoint(lat: Double, lng: Double) {
        viewModelScope.launch {
            val currentList = _routePoints.value.toMutableList()
            val newIndex = currentList.size
            currentList.add(RoutePoint(lat, lng, "Yeni Nokta $newIndex", false))
            _routePoints.value = optimizeRoute(currentList)
            _uiEvent.emit(HomeUiEvent.ShowToast("📍 Yeni nokta eklendi! Rota optimize edildi."))
        }
    }

    private fun optimizeRoute(points: List<RoutePoint>): List<RoutePoint> {
        if (points.isEmpty()) return points
        
        val depo = points.first() // index 0 is always Depo
        
        // Delivered points stay in their current relative order
        val delivered = points.filter { it.isDelivered && it != depo }
        
        // Undelivered points will be sorted
        val undelivered = points.filter { !it.isDelivered && it != depo }.toMutableList()
        
        val result = mutableListOf<RoutePoint>()
        result.add(depo)
        result.addAll(delivered)
        
        // Find nearest neighbor for the remaining points
        var currentLocation = delivered.lastOrNull() ?: depo
        
        while (undelivered.isNotEmpty()) {
            val nearest = undelivered.minByOrNull { calculateDistance(currentLocation, it) }!!
            result.add(nearest)
            undelivered.remove(nearest)
            currentLocation = nearest
        }
        
        return result
    }

    private fun calculateDistance(p1: RoutePoint, p2: RoutePoint): Double {
        val latDiff = p1.latitude - p2.latitude
        val lngDiff = p1.longitude - p2.longitude
        return Math.sqrt(latDiff * latDiff + lngDiff * lngDiff)
    }

    fun onScanBarcodeClick() {
        viewModelScope.launch {
            _uiEvent.emit(HomeUiEvent.OpenBarcodeScanner)
        }
    }

    fun onBarcodeScanned(barcode: String) {
        viewModelScope.launch {
            _uiEvent.emit(HomeUiEvent.ShowDeliveryConfirmation(barcode))
        }
    }

    fun confirmDelivery(packageId: String) {
        viewModelScope.launch {
            // Simüle edilmiş teslimat onayı
            _uiEvent.emit(HomeUiEvent.ShowToast("Paket #$packageId teslimatı başarıyla onaylandı!"))
        }
    }

    fun onUpdateStockClick() {
        viewModelScope.launch {
            _uiEvent.emit(HomeUiEvent.ShowStockUpdateDialog)
        }
    }

    fun updateStock(productId: String, quantity: String) {
        viewModelScope.launch {
            if (productId.isBlank() || quantity.isBlank()) {
                _uiEvent.emit(HomeUiEvent.ShowToast("Lütfen tüm alanları doldurunuz"))
                return@launch
            }
            
            // Simüle edilmiş API çağrısı
            _uiEvent.emit(HomeUiEvent.ShowToast("Ürün ID: $productId için stok $quantity adet olarak güncellendi."))
        }
    }
}
