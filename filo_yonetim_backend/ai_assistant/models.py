from django.db import models
from django.utils import timezone
from accounts.models import CustomUser

class AIChatMessage(models.Model):
    user = models.ForeignKey(CustomUser, on_delete=models.CASCADE, verbose_name="Kullanıcı")
    message = models.TextField(verbose_name="Kullanıcı Mesajı")
    response = models.TextField(blank=True, null=True, verbose_name="AI Cevabı")
    created_at = models.DateTimeField(default=timezone.now, verbose_name="Tarih")

    def __str__(self):
        return f"{self.user} - {self.created_at.strftime('%H:%M')}"

    class Meta:
        verbose_name = "AI Sohbet Mesajı"
        verbose_name_plural = "AI Sohbet Mesajları"
        ordering = ['-created_at']