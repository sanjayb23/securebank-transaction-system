import requests
import json
import uuid

def test_registration():
    """Test user registration with a new username"""
    
    # Generate unique username
    unique_str = str(uuid.uuid4()).replace("-", "")[:8]
    username = f"newuser_{unique_str}"
    
    payload = {
        "username": username,
        "email": f"{username}@example.com", 
        "password": "StrongPass!23",
        "fullName": "New Test User",
        "phone": "+1234567890"
    }
    
    url = "http://localhost:8080/api/auth/register"
    headers = {"Content-Type": "application/json"}
    
    print(f"Testing registration for: {username}")
    
    response = requests.post(url, json=payload, headers=headers)
    
    print(f"Status: {response.status_code}")
    print(f"Response: {json.dumps(response.json(), indent=2)}")
    
    if response.status_code == 200:
        print("SUCCESS: Registration completed!")
        return response.json()
    else:
        print("FAILED: Registration failed!")
        return None

if __name__ == "__main__":
    test_registration()