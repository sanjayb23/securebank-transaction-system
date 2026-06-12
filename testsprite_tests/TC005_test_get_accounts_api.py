import requests
import uuid

BASE_URL = "http://localhost:8080"
TIMEOUT = 30

def test_get_accounts_api():
    # Use unique username to avoid conflicts
    unique_suffix = uuid.uuid4().hex[:8]
    username = f"testuser_{unique_suffix}"
    email = f"{username}@example.com"
    password = "SecurePass123!"
    full_name = "Test User"
    phone = "1234567890"
    
    headers = {
        "Content-Type": "application/json"
    }
    
    token = None
    created_account_id = None
    
    try:
        # Register user
        register_payload = {
            "username": username,
            "email": email,
            "password": password,
            "fullName": full_name,
            "phone": phone
        }
        reg_resp = requests.post(
            f"{BASE_URL}/api/auth/register",
            json=register_payload,
            headers=headers,
            timeout=TIMEOUT
        )
        assert reg_resp.status_code == 200, f"Register failed: {reg_resp.text}"
        reg_json = reg_resp.json()
        assert reg_json.get("success") is True
        assert "token" in reg_json.get("data", {})
        token = reg_json["data"]["token"]
        
        auth_headers = {
            "Content-Type": "application/json",
            "Authorization": f"Bearer {token}"
        }
        
        # Create an account for this user (needed to have accounts to get)
        create_account_payload = {
            "accountType": "SAVINGS",
            "currency": "USD"
        }
        create_resp = requests.post(
            f"{BASE_URL}/api/accounts",
            json=create_account_payload,
            headers=auth_headers,
            timeout=TIMEOUT
        )
        assert create_resp.status_code == 200, f"Account creation failed: {create_resp.text}"
        create_json = create_resp.json()
        assert create_json.get("success") is True
        assert "data" in create_json and isinstance(create_json["data"], dict)
        created_account_id = create_json["data"].get("id")
        assert created_account_id is not None
        
        # Call GET /api/accounts to retrieve accounts list
        get_resp = requests.get(
            f"{BASE_URL}/api/accounts",
            headers=auth_headers,
            timeout=TIMEOUT
        )
        assert get_resp.status_code == 200, f"GET /api/accounts failed: {get_resp.text}"
        get_json = get_resp.json()
        assert get_json.get("success") is True
        accounts = get_json.get("data")
        assert isinstance(accounts, list), "Data is not a list"
        
        # Verify that the created account is in the list
        account_ids = [account.get("id") for account in accounts if isinstance(account, dict)]
        assert created_account_id in account_ids, "Created account not found in accounts list"
        
    finally:
        # Cleanup: delete the created account if applicable
        if token and created_account_id:
            try:
                requests.delete(
                    f"{BASE_URL}/api/accounts/{created_account_id}",
                    headers={"Authorization": f"Bearer {token}"},
                    timeout=TIMEOUT
                )
            except Exception:
                pass

test_get_accounts_api()