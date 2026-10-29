from django.urls import path
from .views import AIChatView

urlpatterns = [
    path('chat/', AIChatView.as_view(), name='ai-chat'),
]