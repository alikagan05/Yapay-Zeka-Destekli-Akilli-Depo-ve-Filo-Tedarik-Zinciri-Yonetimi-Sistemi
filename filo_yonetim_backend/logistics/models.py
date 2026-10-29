from django.db import models
from django.utils import timezone
from accounts.models import CustomUser

class Warehouse(models.Model):
    name = models.CharField(max_length=100, verbose_name="Depo Adı")
    location = models.CharField(max_length=255, verbose_name="Konum")
    capacity = models.IntegerField(verbose_name="Kapasite (m³)")
    is_active = models.BooleanField(default=True, verbose_name="Aktif mi?")

    def __str__(self):
        return self.name

    class Meta:
        verbose_name = "Depo"
        verbose_name_plural = "Depolar"


class Product(models.Model):
    name = models.CharField(max_length=200, verbose_name="Ürün Adı")
    sku = models.CharField(max_length=50, unique=True, verbose_name="Stok Kodu")
    category = models.CharField(max_length=100, verbose_name="Kategori")
    stock_quantity = models.IntegerField(default=0, verbose_name="Stok Miktarı")
    critical_stock_level = models.IntegerField(default=10, verbose_name="Kritik Stok Seviyesi")
    unit_price = models.DecimalField(max_digits=10, decimal_places=2, default=0, verbose_name="Birim Fiyat")
    warehouse = models.ForeignKey(Warehouse, on_delete=models.CASCADE, related_name="products", verbose_name="Depo")

    def __str__(self):
        return f"{self.name} ({self.sku})"

    class Meta:
        verbose_name = "Ürün"
        verbose_name_plural = "Ürünler"


class Vehicle(models.Model):
    plate_number = models.CharField(max_length=20, unique=True, verbose_name="Plaka")
    vehicle_type = models.CharField(max_length=50, verbose_name="Araç Tipi")
    capacity = models.IntegerField(verbose_name="Kapasite (kg)")
    is_available = models.BooleanField(default=True, verbose_name="Müsait mi?")
    driver = models.ForeignKey(CustomUser, on_delete=models.SET_NULL, null=True, blank=True,
                               related_name="vehicles", verbose_name="Sürücü")

    def __str__(self):
        return self.plate_number

    class Meta:
        verbose_name = "Araç"
        verbose_name_plural = "Araçlar"


class Shipment(models.Model):
    STATUS_CHOICES = [
        ('pending', 'Bekliyor'),
        ('in_transit', 'Yolda'),
        ('delivered', 'Teslim Edildi'),
        ('cancelled', 'İptal Edildi'),
    ]

    shipment_code = models.CharField(max_length=50, unique=True, verbose_name="Sevkiyat Kodu")
    product = models.ForeignKey(Product, on_delete=models.CASCADE, verbose_name="Ürün")
    quantity = models.PositiveIntegerField(verbose_name="Miktar")
    vehicle = models.ForeignKey(Vehicle, on_delete=models.SET_NULL, null=True, verbose_name="Araç")
    driver = models.ForeignKey(CustomUser, on_delete=models.SET_NULL, null=True, verbose_name="Sürücü")
    status = models.CharField(max_length=20, choices=STATUS_CHOICES, default='pending', verbose_name="Durum")
    shipment_date = models.DateTimeField(default=timezone.now, verbose_name="Sevkiyat Tarihi")
    delivery_date = models.DateTimeField(null=True, blank=True, verbose_name="Teslim Tarihi")

    def __str__(self):
        return self.shipment_code

    class Meta:
        verbose_name = "Sevkiyat"
        verbose_name_plural = "Sevkiyatlar"