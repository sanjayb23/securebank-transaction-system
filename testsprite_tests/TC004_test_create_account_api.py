import requests
import uuid

BASE_URL = "http://localhost:8080"
TIMEOUT = 30

def test_create_account_api():
    # First, register and login a user to get JWT token for authentication
    register_url = f"{BASE_URL}/api/auth/register"
    login_url = f"{BASE_URL}/api/auth/login"
    create_account_url = f"{BASE_URL}/api/accounts"
    
    username = f"user_{uuid.uuid4().hex[:8]}"
    password = "TestPass123!"
    email = f"{username}@example.com"
    full_name = "Test User"
    phone = "1234567890"
    
    headers = {"Content-Type": "application/json"}
    
    # Register user
    register_payload = {
        "username": username,
        "email": email,
        "password": password,
        "fullName": full_name,
        "phone": phone
    }
    try:
        reg_resp = requests.post(register_url, json=register_payload, headers=headers, timeout=TIMEOUT)
        assert reg_resp.status_code == 200, f"Registration failed: {reg_resp.text}"
        reg_json = reg_resp.json()
        assert reg_json.get("success") is True, "Registration success flag false"
        token = reg_json.get("data", {}).get("token")
        assert token, "Token not found in registration response"
    except Exception as e:
        raise AssertionError(f"User registration failed: {e}")
    
    # Login user
    login_payload = {
        "username": username,
        "password": password
    }
    try:
        login_resp = requests.post(login_url, json=login_payload, headers=headers, timeout=TIMEOUT)
        assert login_resp.status_code == 200, f"Login failed: {login_resp.text}"
        login_json = login_resp.json()
        assert login_json.get("success") is True, "Login success flag false"
        token = login_json.get("data", {}).get("token")
        assert token, "Token not found in login response"
    except Exception as e:
        raise AssertionError(f"User login failed: {e}")
    
    auth_headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {token}"
    }
    
    # Prepare payload for account creation
    account_payload = {
        "accountType": "SAVINGS",
        "currency": "USD"
    }
    
    account_id = None
    try:
        # Create account
        resp = requests.post(create_account_url, json=account_payload, headers=auth_headers, timeout=TIMEOUT)
        assert resp.status_code == 200, f"Account creation failed: {resp.text}"
        resp_json = resp.json()
        assert resp_json.get("success") is True, "Account creation success flag false"
        data = resp_json.get("data")
        assert data, "No data in account creation response"
        
        # Validate mandatory returned fields
        account_id = data.get("id")
        assert isinstance(account_id, int), "Account ID missing or not int"
        assert isinstance(data.get("accountNumber"), str) and data.get("accountNumber"), "Account number invalid"
        assert data.get("accountType") == account_payload["accountType"], "Account type mismatch"
        assert isinstance(data.get("balance"), (int, float)), "Balance missing or invalid type"
        assert str(data.get("currency")).upper() == account_payload["currency"], "Currency mismatch"
        assert isinstance(data.get("status"), str) and data.get("status"), "Status missing or invalid"
        assert isinstance(data.get("createdAt"), str) and data.get("createdAt"), "CreatedAt missing or invalid"
    finally:
        # Clean up: delete the created account if API for deletion existed
        # The PRD does not specify account deletion endpoint so skip this step.
        pass

test_create_account_api()
