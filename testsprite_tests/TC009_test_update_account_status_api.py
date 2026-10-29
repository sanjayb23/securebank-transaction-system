import requests
import uuid

BASE_URL = "http://localhost:8080"
TIMEOUT = 30

def register_test_user():
    register_url = f"{BASE_URL}/api/auth/register"
    headers = {"Content-Type": "application/json"}
    username = f"user_{uuid.uuid4().hex[:8]}"
    email = f"{username}@example.com"
    payload = {
        "username": username,
        "email": email,
        "password": "TestPass123!",
        "fullName": "Test User",
        "phone": "1234567890"
    }
    response = requests.post(register_url, headers=headers, json=payload, timeout=TIMEOUT)
    assert response.status_code == 200, f"User registration failed with status {response.status_code}"
    resp_json = response.json()
    assert resp_json.get("success") is True, f"User registration failed: {resp_json.get('message')}"
    return username

def get_auth_token():
    username = register_test_user()
    login_url = f"{BASE_URL}/api/auth/login"
    headers = {"Content-Type": "application/json"}
    payload = {
        "username": username,
        "password": "TestPass123!"
    }
    response = requests.post(login_url, headers=headers, json=payload, timeout=TIMEOUT)
    assert response.status_code == 200, f"Login failed with status {response.status_code}"
    resp_json = response.json()
    assert resp_json.get("success") is True, "Login success flag not true"
    data = resp_json.get("data")
    assert data is not None, "Login data missing"
    token = data.get("token")
    assert token is not None and isinstance(token, str), "Token missing or invalid in login response"
    return token

def test_update_account_status_api():
    # First, create an account to obtain a valid ID
    token = get_auth_token()
    create_account_url = f"{BASE_URL}/api/accounts"
    headers = {'Content-Type': 'application/json', 'Authorization': f'Bearer {token}'}
    create_payload = {
        "accountType": "SAVINGS",
        "currency": "USD"
    }
    account_id = None
    try:
        create_resp = requests.post(create_account_url, json=create_payload, headers=headers, timeout=TIMEOUT)
        assert create_resp.status_code == 200, f"Account creation failed: {create_resp.text}"
        create_data = create_resp.json()
        assert create_data.get("success") is True, f"Account creation response unsuccessful: {create_resp.text}"
        account = create_data.get("data")
        assert account and "id" in account, f"Account ID missing in response: {create_resp.text}"
        account_id = account["id"]

        # Now update the account status
        update_status_url = f"{BASE_URL}/api/accounts/{account_id}/status"
        # Example valid status values might be "ACTIVE", "FROZEN", but not specified, so using "FROZEN"
        valid_status = "FROZEN"
        patch_headers = {'Content-Type': 'application/json', 'Authorization': f'Bearer {token}'}
        update_resp = requests.patch(update_status_url, json=valid_status, headers=patch_headers, timeout=TIMEOUT)
        assert update_resp.status_code == 200, f"Status update failed: {update_resp.text}"

        update_data = update_resp.json()
        assert update_data.get("success") is True, f"Status update response unsuccessful: {update_resp.text}"
        assert update_data.get("data") == valid_status, f"Status not updated correctly. Expected {valid_status}, got {update_data.get('data')}"

        # Optionally, fetch the account to confirm status change
        get_account_url = f"{BASE_URL}/api/accounts/{account_id}"
        get_headers = {'Authorization': f'Bearer {token}'}
        get_resp = requests.get(get_account_url, headers=get_headers, timeout=TIMEOUT)
        assert get_resp.status_code == 200, f"Failed to get account after status update: {get_resp.text}"
        get_data = get_resp.json()
        assert get_data.get("success") is True, f"Get account response unsuccessful: {get_resp.text}"
        account_info = get_data.get("data")
        assert account_info.get("status") == valid_status, f"Account status not updated. Expected {valid_status}, got {account_info.get('status')}"

    finally:
        # Clean up: delete the created account if deletion endpoint existed
        # Since deletion is not described in PRD, skipping deletion step.

        # If deletion were available, code would go here

        pass


test_update_account_status_api()