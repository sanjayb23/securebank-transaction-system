import requests

BASE_URL = "http://localhost:8080"
TIMEOUT = 30

# Helper function to register a user
# Using a fixed test user data consistent with the PRD requirements

def register_test_user():
    import uuid
    register_url = f"{BASE_URL}/api/auth/register"
    headers = {"Content-Type": "application/json"}
    username = f"testuser_{uuid.uuid4().hex[:8]}"
    email = f"{username}@example.com"
    payload = {
        "username": username,
        "email": email,
        "password": "testpass",
        "fullName": "Test User",
        "phone": "1234567890"
    }
    response = requests.post(register_url, headers=headers, json=payload, timeout=TIMEOUT)
    assert response.status_code == 200, f"User registration failed with status {response.status_code}"
    resp_json = response.json()
    assert resp_json.get("success") is True, f"User registration failed: {resp_json.get('message')}"
    return username

# Helper function to login and get JWT token
# Assumption: a valid user "testuser" with password "testpass" exists in the system for testing
# If not, register first

def get_auth_token():
    # Register user first to ensure existence
    username = register_test_user()

    login_url = f"{BASE_URL}/api/auth/login"
    headers = {"Content-Type": "application/json"}
    payload = {
        "username": username,
        "password": "testpass"
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


def test_get_account_by_id_api():
    """
    Verify that the endpoint /api/accounts/{id} returns the correct account details for a valid account ID.
    Since the resource ID is not provided, create a new account, then get it by ID, then finally delete the account.
    """

    create_url = f"{BASE_URL}/api/accounts"
    get_url_template = f"{BASE_URL}/api/accounts/{{}}"

    token = get_auth_token()

    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {token}"
    }

    # Sample valid payload for account creation - choose savings and USD as example
    create_payload = {
        "accountType": "SAVINGS",
        "currency": "USD"
    }

    account_id = None

    try:
        # Create a new account
        response = requests.post(create_url, headers=headers, json=create_payload, timeout=TIMEOUT)
        assert response.status_code == 200, f"Account creation failed with status {response.status_code}"
        resp_json = response.json()
        assert resp_json.get("success") is True, "Account creation success flag not true"
        data = resp_json.get("data")
        assert data is not None, "Account creation data missing"
        account_id = data.get("id")
        assert isinstance(account_id, int), "Account ID is not an integer"
        # Validate key fields in created account data
        assert data.get("accountType") == create_payload["accountType"], "Account type mismatch on creation"
        assert data.get("currency") == create_payload["currency"], "Currency mismatch on creation"
        assert "accountNumber" in data and isinstance(data["accountNumber"], str), "Missing or invalid accountNumber"
        assert "balance" in data and isinstance(data["balance"], (int, float)), "Missing or invalid balance"
        assert "status" in data and isinstance(data["status"], str), "Missing or invalid status"
        assert "createdAt" in data and isinstance(data["createdAt"], str), "Missing or invalid createdAt"

        # Get account by ID
        get_url = get_url_template.format(account_id)
        get_response = requests.get(get_url, headers=headers, timeout=TIMEOUT)
        assert get_response.status_code == 200, f"Get account by ID failed with status {get_response.status_code}"
        get_resp_json = get_response.json()
        assert get_resp_json.get("success") is True, "Get account by ID success flag not true"
        get_data = get_resp_json.get("data")
        assert get_data is not None, "Get account by ID data missing"
        # Validate returned data matches created account
        assert get_data.get("id") == account_id, "Account ID mismatch"
        assert get_data.get("accountType") == create_payload["accountType"], "Account type mismatch"
        assert get_data.get("currency") == create_payload["currency"], "Currency mismatch"
        assert get_data.get("accountNumber") == data["accountNumber"], "Account number mismatch"
        assert isinstance(get_data.get("balance"), (int, float)), "Invalid balance type"
        assert isinstance(get_data.get("status"), str), "Invalid status type"
        assert isinstance(get_data.get("createdAt"), str), "Invalid createdAt type"

    finally:
        # Cleanup: No explicit account deletion endpoint given in PRD, so skipping delete attempt
        pass


test_get_account_by_id_api()
