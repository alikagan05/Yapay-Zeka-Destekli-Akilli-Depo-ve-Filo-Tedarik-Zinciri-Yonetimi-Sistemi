"""
URL configuration for core project.
"""

from django.contrib import admin
from django.urls import path, include
from django.views.generic import TemplateView

urlpatterns = [
    path('admin/', admin.site.urls),

    # Authentication
    path('api/auth/', include('accounts.urls')),
    path('accounts/', include('django.contrib.auth.urls')),

    # ===================== API ENDPOINTS =====================
    path('api/', include('logistics.urls')),     # API için /api/ öneki

    path('api/ai/', include('ai_assistant.urls')),

    # Web sayfaları (şu an için ana sayfa)
    path('', include('logistics.urls')),         # Web arayüzü

    path('chat/', TemplateView.as_view(template_name='chat.html'), name='chat'),

    path('dashboard/', TemplateView.as_view(template_name='dashboard.html'), name='dashboard'),
]