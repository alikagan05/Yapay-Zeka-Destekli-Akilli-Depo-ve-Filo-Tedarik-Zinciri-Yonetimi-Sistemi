from django.contrib import admin
from .models import Warehouse, Product, Vehicle, Shipment

@admin.register(Warehouse)
class WarehouseAdmin(admin.ModelAdmin):
    list_display = ['name', 'location', 'capacity', 'is_active']
    list_filter = ['is_active']
    search_fields = ['name', 'location']
    ordering = ['name']


@admin.register(Product)
class ProductAdmin(admin.ModelAdmin):
    list_display = ['name', 'sku', 'category', 'stock_quantity', 'unit_price', 'warehouse']
    list_filter = ['category', 'warehouse']
    search_fields = ['name', 'sku']
    ordering = ['-stock_quantity']


@admin.register(Vehicle)
class VehicleAdmin(admin.ModelAdmin):
    list_display = ['plate_number', 'vehicle_type', 'capacity', 'is_available', 'driver']
    list_filter = ['is_available', 'vehicle_type']
    search_fields = ['plate_number']
    ordering = ['plate_number']


@admin.register(Shipment)
class ShipmentAdmin(admin.ModelAdmin):
    list_display = ['shipment_code', 'product', 'quantity', 'vehicle', 'status', 'shipment_date']
    list_filter = ['status', 'shipment_date']
    search_fields = ['shipment_code']
    ordering = ['-shipment_date']