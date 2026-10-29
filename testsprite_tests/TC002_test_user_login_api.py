import requests
import uuid

BASE_URL = "http://localhost:8080"
TIMEOUT = 30

def test_user_login_api():
    register_url = f"{BASE_URL}/api/auth/register"
    login_url = f"{BASE_URL}/api/auth/login"

    # Create unique username/email using uuid to avoid conflict
    unique_suffix = str(uuid.uuid4()).replace('-', '')[:8]
    username = f"user_{unique_suffix}"
    email = f"{username}@test.com"
    password = "TestPass123!"

    headers = {
        "Content-Type": "application/json"
    }

    register_payload = {
        "username": username,
        "email": email,
        "password": password,
        "fullName": "Test User",
        "phone": "1234567890"
    }

    # Register user first to ensure valid credentials for login
    try:
        reg_response = requests.post(register_url, json=register_payload, headers=headers, timeout=TIMEOUT)
        assert reg_response.status_code == 200, f"User registration failed with status code {reg_response.status_code}"
        reg_json = reg_response.json()
        assert reg_json.get("success") is True, f"User registration not successful: {reg_json.get('message')}"

        # Now attempt to login with the registered credentials
        login_payload = {
            "username": username,
            "password": password
        }
        login_response = requests.post(login_url, json=login_payload, headers=headers, timeout=TIMEOUT)
        assert login_response.status_code == 200, f"Login failed with status code {login_response.status_code}"

        login_json = login_response.json()
        assert login_json.get("success") is True, f"Login not successful: {login_json.get('message')}"
        assert "data" in login_json and isinstance(login_json["data"], dict), "Login response missing 'data' object"
        token = login_json["data"].get("token")
        token_type = login_json["data"].get("type")
        assert token and isinstance(token, str) and len(token) > 0, "JWT token not found or invalid"
        assert token_type and isinstance(token_type, str) and len(token_type) > 0, "Token type not found or invalid"
    finally:
        # Cleanup: if the API had a user delete endpoint, it would be called here.
        # Since none documented, no cleanup step.

        pass

test_user_login_api()