import requests
import uuid

BASE_URL = "http://localhost:8080"
TIMEOUT = 30

def test_get_account_balance_api():
    # To test the balance endpoint, we need an account.
    # Since resource ID is not provided, create a new account first.
    headers = {
        "Content-Type": "application/json"
    }

    # Create a user, login, get token, then create account with auth header since account creation likely requires an authenticated user.
    # From PRD: User registration and login return JWT tokens for auth.
    # We'll register a unique user, login, create account, then test balance endpoint.
    session = requests.Session()

    # Register user
    username = f"testuser_{uuid.uuid4().hex[:8]}"
    email = f"{username}@example.com"
    password = "TestPassword123!"
    full_name = "Test User"
    phone = "1234567890"

    register_payload = {
        "username": username,
        "email": email,
        "password": password,
        "fullName": full_name,
        "phone": phone
    }
    try:
        reg_resp = session.post(f"{BASE_URL}/api/auth/register", json=register_payload, headers=headers, timeout=TIMEOUT)
        assert reg_resp.status_code == 200, f"User registration failed: {reg_resp.status_code} {reg_resp.text}"
        reg_data = reg_resp.json()
        assert reg_data.get("success") is True, f"User registration success flag false: {reg_data}"
        token = reg_data["data"]["token"]
        assert token, "No token returned on registration"
    except Exception as e:
        raise AssertionError(f"User registration failed: {e}")

    # Set auth header for further requests
    auth_headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {token}"
    }

    # Create account
    account_payload = {
        "accountType": "SAVINGS",
        "currency": "USD"
    }
    account_id = None
    try:
        create_acc_resp = session.post(f"{BASE_URL}/api/accounts", json=account_payload, headers=auth_headers, timeout=TIMEOUT)
        assert create_acc_resp.status_code == 200, f"Account creation failed: {create_acc_resp.status_code} {create_acc_resp.text}"
        acc_data = create_acc_resp.json()
        assert acc_data.get("success") is True, f"Account creation success flag false: {acc_data}"
        acc_info = acc_data.get("data")
        assert acc_info and "id" in acc_info, f"Account id not found in response: {acc_data}"
        account_id = acc_info["id"]

        # Now call the GET /api/accounts/{id}/balance endpoint
        balance_resp = session.get(f"{BASE_URL}/api/accounts/{account_id}/balance", headers=auth_headers, timeout=TIMEOUT)
        assert balance_resp.status_code == 200, f"Balance retrieval failed: {balance_resp.status_code} {balance_resp.text}"
        balance_data = balance_resp.json()
        assert balance_data.get("success") is True, f"Balance retrieval success flag false: {balance_data}"
        assert "data" in balance_data, f"Balance data missing in response: {balance_data}"
        # The balance should be numeric (float or int)
        balance_value = balance_data["data"]
        assert isinstance(balance_value, (int, float)), f"Balance is not a number: {balance_value}"

        # Optionally, validate that the balance equals the balance reported in account creation response (likely 0 on new account)
        assert abs(balance_value - acc_info.get("balance", 0)) < 1e-6, f"Balance mismatch: endpoint balance {balance_value}, account creation balance {acc_info.get('balance')}"
    finally:
        # Cleanup: delete the created account and user if such endpoints existed
        # PRD does not specify account deletion or user deletion endpoints, so skipping actual deletion.
        # If in a real environment, add cleanup code here.
        pass

test_get_account_balance_api()
