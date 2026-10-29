package com.logisticspro.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.logisticspro.app.ui.screens.fleet.FleetMapScreen
import com.logisticspro.app.data.model.RoutePoint
import com.logisticspro.app.ui.viewmodel.HomeUiEvent
import com.logisticspro.app.ui.viewmodel.HomeViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToFleet: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showStockDialog by remember { mutableStateOf(false) }
    var showScanner by remember { mutableStateOf(false) }
    var showDeliveryConfirmation by remember { mutableStateOf(false) }
    var scannedBarcode by remember { mutableStateOf("") }

    val routePoints by viewModel.routePoints.collectAsState()

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showScanner = true
        } else {
            android.widget.Toast.makeText(context, "Kamera izni reddedildi", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is HomeUiEvent.ShowToast -> {
                    android.widget.Toast.makeText(context, event.message, android.widget.Toast.LENGTH_SHORT).show()
                }
                HomeUiEvent.ShowStockUpdateDialog -> {
                    showStockDialog = true
                }
                HomeUiEvent.OpenBarcodeScanner -> {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        showScanner = true
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
                is HomeUiEvent.ShowDeliveryConfirmation -> {
                    scannedBarcode = event.barcode
                    showScanner = false
                    showDeliveryConfirmation = true
                }
            }
        }
    }

    if (showScanner) {
        Box(modifier = Modifier.fillMaxSize()) {
            BarcodeScannerScreen(
                onBarcodeScanned = { barcode ->
                    viewModel.onBarcodeScanned(barcode)
                }
            )
            IconButton(
                onClick = { showScanner = false },
                modifier = Modifier.padding(16.dp).align(androidx.compose.ui.Alignment.TopEnd)
            ) {
                Icon(Icons.Filled.Close, contentDescription = "Kapat", tint = Color.White)
            }
        }
    }

    if (showDeliveryConfirmation) {
        DeliveryConfirmationDialog(
            packageId = scannedBarcode,
            onDismiss = { showDeliveryConfirmation = false },
            onConfirm = {
                viewModel.confirmDelivery(scannedBarcode)
                showDeliveryConfirmation = false
            }
        )
    }

    if (showStockDialog) {
        StockUpdateDialog(
            onDismiss = { showStockDialog = false },
            onConfirm = { id, qty ->
                viewModel.updateStock(id, qty)
                showStockDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logistics Pro", fontWeight = FontWeight.Black, color = Color(0xFF0D47A1)) },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color(0xFF1976D2))
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = Color(0xFF1976D2))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            // AI Chatbot FAB
            FloatingActionButton(
                onClick = onNavigateToChat,
                containerColor = Color(0xFF0D47A1),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(60.dp)
            ) {
                Text("🤖", fontSize = 26.sp)
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("DASHBOARD", fontSize = 10.sp) },
                    selected = true,
                    onClick = { /*TODO*/ },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0D47A1),
                        selectedTextColor = Color(0xFF0D47A1),
                        indicatorColor = Color(0xFFBBDEFB)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Inventory, contentDescription = "Tasks") },
                    label = { Text("TASKS", fontSize = 10.sp) },
                    selected = false,
                    onClick = { /*TODO*/ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.LocalShipping, contentDescription = "Fleet") },
                    label = { Text("FLEET", fontSize = 10.sp) },
                    selected = false,
                    onClick = onNavigateToFleet
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Profile") },
                    label = { Text("PROFILE", fontSize = 10.sp) },
                    selected = false,
                    onClick = { /*TODO*/ }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "WH-042 Bölge Sorumlusu",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Hoş Geldin, Alex Miller",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF003461), Color(0xFF004B87))
                                )
                            )
                            .clickable { viewModel.onScanBarcodeClick() }
                            .padding(20.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.height(120.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.QrCodeScanner, contentDescription = "Barcode", tint = Color.White)
                            }
                            Column {
                                Text("Barkod Okut", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("Hızlı sevkiyat girişi yapın", color = Color(0xFF8ABCFF), fontSize = 12.sp)
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFFFF752D))
                            .clickable { viewModel.onUpdateStockClick() }
                            .padding(20.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.height(120.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF5F2100).copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Inventory, contentDescription = "Inventory", tint = Color(0xFF5F2100))
                            }
                            Column {
                                Text("Stok Güncelle", color = Color(0xFF5F2100), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("Mevcut envanter adetlerini düzenle", color = Color(0xFF7C2E00), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Canlı Filo Takibi + Teslimat Noktaları Listesi
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Canlı Filo Takibi", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        val deliveredCount = routePoints.count { it.isDelivered }
                        val totalDeliveryPoints = routePoints.size - 1 // depo hariç
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    if (deliveredCount == totalDeliveryPoints) Color(0xFF4CAF50).copy(alpha = 0.15f)
                                    else Color(0xFFDFE3E8)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "$deliveredCount / $totalDeliveryPoints Teslim",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (deliveredCount == totalDeliveryPoints) Color(0xFF2E7D32) else Color.DarkGray
                            )
                        }
                    }

                    // Canvas Harita
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.LightGray)
                    ) {
                        FleetMapScreen(
                            route = routePoints,
                            onMapLongClick = { lat, lng ->
                                viewModel.addRoutePoint(lat, lng)
                            }
                        )
                    }

                    // Teslimat Noktaları Kartları
                    routePoints.forEachIndexed { index, point ->
                        if (index == 0) return@forEachIndexed // Depo gösterilmez

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (point.isDelivered) Color(0xFFE8F5E9) else Color(0xFFF1F4FA)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (point.isDelivered) Color(0xFF4CAF50) else Color(0xFFFF752D)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (point.isDelivered) {
                                        Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text("$index", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                                Column {
                                    Text(
                                        point.name.ifEmpty { "Teslimat Noktası $index" },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (point.isDelivered) Color(0xFF2E7D32) else Color(0xFF1A1A1A)
                                    )
                                    Text(
                                        if (point.isDelivered) "Teslim edildi ✓" else "Bekliyor",
                                        fontSize = 12.sp,
                                        color = if (point.isDelivered) Color(0xFF4CAF50) else Color.Gray
                                    )
                                }
                            }

                            if (!point.isDelivered) {
                                Button(
                                    onClick = { viewModel.markDelivered(index) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF0D47A1)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("Teslim Et", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(width = 6.dp, height = 24.dp).clip(CircleShape).background(Color(0xFFA33F00)))
                            Text("Bekleyen Depo İşleri", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFDFE3E8))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("4 Aktif Görev", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        }
                    }

                    TaskCard(
                        icon = Icons.Filled.LocalShipping,
                        title = "Incoming Shipments",
                        subtitle = "128 kalem mal girişi bekliyor",
                        tagText = "Yüksek",
                        tagColor = Color(0xFFFFDAD6),
                        tagTextColor = Color(0xFF93000A),
                        iconBgColor = Color(0xFF3D4A5E),
                        iconColor = Color(0xFFACB9D1)
                    )

                    TaskCard(
                        icon = Icons.Filled.TakeoutDining,
                        title = "A-04 Palet Düzenleme",
                        subtitle = "800kg kapasite optimizasyonu",
                        tagText = "Normal",
                        tagColor = Color(0xFF3D4A5E),
                        tagTextColor = Color(0xFFACB9D1),
                        iconBgColor = Color(0xFFDFE3E8),
                        iconColor = Color.DarkGray
                    )

                    TaskCard(
                        icon = Icons.Filled.VerifiedUser,
                        title = "Güvenlik Denetimi",
                        subtitle = "Bölge WH-042 ekipman kontrolü",
                        tagText = "Acil",
                        tagColor = Color(0xFFFF752D),
                        tagTextColor = Color(0xFF5F2100),
                        iconBgColor = Color(0xFFDFE3E8),
                        iconColor = Color.DarkGray
                    )
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFDFE3E8), RoundedCornerShape(32.dp))
                        .padding(24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Depo Doluluk Oranı", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("84%", fontSize = 48.sp, fontWeight = FontWeight.Black, color = Color(0xFF003461))
                            Text("+2.4% geçen haftadan", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDFE3E8))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.84f)
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .background(Color(0xFF003461))
                            )
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(top = 8.dp)) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFF1F4FA))
                                    .padding(16.dp)
                            ) {
                                Text("KULLANILABİLİR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Text("1,240 m²", fontSize = 18.sp, fontWeight = FontWeight.Black)
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFF1F4FA))
                                    .padding(16.dp)
                            ) {
                                Text("DOLU", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Text("6,500 m²", fontSize = 18.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }

            // AI Asistan bilgi kartı — en altta
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF1A237E), Color(0xFF0D47A1))
                            )
                        )
                        .clickable { onNavigateToChat() }
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🤖", fontSize = 28.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("AI Lojistik Asistanı", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                "Stok analizi, rota optimizasyonu ve maliyet tahmini için AI'a sorun",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryConfirmationDialog(
    packageId: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = Color(0xFF0D47A1)) },
        title = { Text("Teslimat Onayı") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Paket ID: $packageId", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Bu paketin teslim edildiğini onaylıyor musunuz?", textAlign = TextAlign.Center)
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
            ) {
                Text("Teslimatı Onayla")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

@Composable
fun StockUpdateDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var productId by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Stok Güncelle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = productId,
                    onValueChange = { productId = it },
                    label = { Text("Ürün ID veya SKU") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Yeni Miktar") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(productId, quantity) }) {
                Text("Güncelle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

@Composable
fun TaskCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tagText: String,
    tagColor: Color,
    tagTextColor: Color,
    iconBgColor: Color,
    iconColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF1F4FA))
            .clickable { /*TODO*/ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor)
            }
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, fontSize = 14.sp, color = Color.Gray)
            }
        }
        
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(tagColor)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(tagText.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Black, color = tagTextColor)
            }
        }
    }
}
