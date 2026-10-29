import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'core.settings')
django.setup()

from django.contrib.auth import get_user_model
from rest_framework.test import APIClient

User = get_user_model()

# 1. Create a test user
user, created = User.objects.get_or_create(username='testdriver', defaults={'email': 'driver@test.com', 'role': 'DRIVER'})
if created:
    user.set_password('TestPass123!')
    user.save()
    print("User 'testdriver' created successfully.")
else:
    print("User 'testdriver' already exists.")

# 2. Test Login API
client = APIClient()
response = client.post('/api/auth/login/', {'username': 'testdriver', 'password': 'TestPass123!'})
print(f"\nLogin Response Status: {response.status_code}")
data = response.json()
print("Access Token present:", 'access' in data)

# 3. Test Me API
if 'access' in data:
    client.credentials(HTTP_AUTHORIZATION='Bearer ' + data['access'])
    me_response = client.get('/api/auth/me/')
    print(f"\nMe Response Status: {me_response.status_code}")
    print("User Data from /api/auth/me/:", me_response.json())
else:
    print("Failed to get access token.", data)
