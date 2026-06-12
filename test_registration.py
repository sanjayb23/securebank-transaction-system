import requests
import json
import uuid

BASE_URL = "http://localhost:8080"
REGISTER_ENDPOINT = "/api/auth/register"

def test_registration(username=None):
    """Test user registration with a new username"""
    
    # Generate unique username if not provided
    if not username:
        unique_str = str(uuid.uuid4()).replace("-", "")[:8]
        username = f"testuser_{unique_str}"
    
    payload = {
        "username": username,
        "email": f"{username}@example.com",
        "password": "StrongPass!23",
        "fullName": "Test User",
        "phone": "+1234567890"
    }
    
    headers = {"Content-Type": "application/json"}
    
    print(f"Testing registration for username: {username}")
    print(f"Request URL: {BASE_URL}{REGISTER_ENDPOINT}")
    print(f"Payload: {json.dumps(payload, indent=2)}")
    print("-" * 50)
    
    try:
        response = requests.post(
            BASE_URL + REGISTER_ENDPOINT, 
            json=payload, 
            headers=headers, 
            timeout=30
        )
        
        print(f"Status Code: {response.status_code}")
        print(f"Response: {json.dumps(response.json(), indent=2)}")
        
        if response.status_code == 200:
            print("✅ Registration successful!")
            return True
        else:
            print("❌ Registration failed!")
            return False
            
    except requests.RequestException as e:
        print(f"❌ Request failed: {e}")
        return False

if __name__ == "__main__":
    # Test with auto-generated username
    test_registration()
    
    print("\n" + "="*60 + "\n")
    
    # Test with custom username
    custom_username = input("Enter a custom username to test (or press Enter to skip): ").strip()
    if custom_username:
        test_registration(custom_username)