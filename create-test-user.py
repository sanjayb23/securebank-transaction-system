import requests
import json

# API endpoint
base_url = "http://localhost:8080/api"

# Test user data
test_user = {
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test123!",
    "fullName": "Test User",
    "phone": "1234567890"
}

try:
    # Register test user
    response = requests.post(f"{base_url}/auth/register", json=test_user)
    
    if response.status_code == 200 or response.status_code == 201:
        print("✅ Test user created successfully!")
        print(f"Username: {test_user['username']}")
        print(f"Password: {test_user['password']}")
        print(f"Email: {test_user['email']}")
    else:
        print(f"❌ Failed to create user: {response.status_code}")
        print(response.text)
        
except Exception as e:
    print(f"❌ Error: {e}")
    print("Make sure your backend is running on http://localhost:8080")