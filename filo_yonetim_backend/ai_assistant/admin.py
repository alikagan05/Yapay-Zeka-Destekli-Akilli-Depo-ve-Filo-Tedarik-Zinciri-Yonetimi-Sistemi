from django.contrib import admin
from .models import AIChatMessage

@admin.register(AIChatMessage)
class AIChatMessageAdmin(admin.ModelAdmin):
    list_display = ['user', 'message', 'created_at']
    list_filter = ['user', 'created_at']
    search_fields = ['message', 'response']
    readonly_fields = ['user', 'message', 'response', 'created_at']
    ordering = ['-created_at']

    fieldsets = [
        ('Sohbet Bilgileri', {
            'fields': ['user', 'message', 'response', 'created_at']
        }),
    ]