package com.logisticspro.app.ui.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Barkod okut butonuna basildiginda scanner acilma event'i yayinlanmali`() = runTest {
        // When
        viewModel.onScanBarcodeClick()

        // Then
        val event = viewModel.uiEvent.first()
        assertTrue(event is HomeUiEvent.OpenBarcodeScanner)
    }

    @Test
    fun `Barkod okundugunda teslimat onay event'i yayinlanmali`() = runTest {
        // Given
        val barcode = "PKG12345"

        // When
        viewModel.onBarcodeScanned(barcode)

        // Then
        val event = viewModel.uiEvent.first()
        assertTrue(event is HomeUiEvent.ShowDeliveryConfirmation)
        assertEquals(barcode, (event as HomeUiEvent.ShowDeliveryConfirmation).barcode)
    }

    @Test
    fun `Teslimat onaylandiginda basari mesaji yayinlanmali`() = runTest {
        // Given
        val packageId = "PKG12345"

        // When
        viewModel.confirmDelivery(packageId)

        // Then
        val event = viewModel.uiEvent.first()
        assertTrue(event is HomeUiEvent.ShowToast)
        assertEquals("Paket #PKG12345 teslimatı başarıyla onaylandı!", (event as HomeUiEvent.ShowToast).message)
    }

    @Test
    fun `Stok guncelle butonuna basildiginda diyalog event'i yayinlanmali`() = runTest {
        // When
        viewModel.onUpdateStockClick()

        // Then
        val event = viewModel.uiEvent.first()
        assertEquals(HomeUiEvent.ShowStockUpdateDialog, event)
    }

    @Test
    fun `Gecerli verilerle stok guncellendiginde basari mesaji yayinlanmali`() = runTest {
        // Given
        val productId = "SKU-123"
        val quantity = "50"

        // When
        viewModel.updateStock(productId, quantity)

        // Then
        val event = viewModel.uiEvent.first()
        assertTrue(event is HomeUiEvent.ShowToast)
        assertEquals("Ürün ID: SKU-123 için stok 50 adet olarak güncellendi.", (event as HomeUiEvent.ShowToast).message)
    }
}
