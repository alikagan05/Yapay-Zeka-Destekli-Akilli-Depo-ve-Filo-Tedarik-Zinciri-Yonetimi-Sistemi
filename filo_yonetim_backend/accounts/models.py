from django.contrib.auth.models import AbstractUser
from django.db import models

class CustomUser(AbstractUser):
    ROLE_CHOICES = (
        ('MANAGER', 'Depo Yöneticisi'),
        ('STAFF', 'Depo Personeli'),
        ('DRIVER', 'Filo Şoförü'),
    )
    role = models.CharField(max_length=20, choices=ROLE_CHOICES, default='STAFF')

    def __str__(self):
        return f"{self.username} - {self.get_role_display()}"
