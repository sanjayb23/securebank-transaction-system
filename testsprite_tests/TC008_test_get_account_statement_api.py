import requests
import uuid

BASE_URL = "http://localhost:8080"
TIMEOUT = 30

def register_user():
    url = f"{BASE_URL}/api/auth/register"
    unique_suffix = str(uuid.uuid4()).split('-')[0]
    payload = {
        "username": f"testuser_{unique_suffix}",
        "email": f"testuser_{unique_suffix}@example.com",
        "password": "TestPassword123!",
        "fullName": "Test User",
        "phone": "1234567890"
    }
    response = requests.post(url, json=payload, timeout=TIMEOUT)
    response.raise_for_status()
    data = response.json()
    assert data.get("success") is True
    token = data.get("data", {}).get("token")
    assert token is not None
    return token

def create_account(token, account_type="SAVINGS", currency="USD"):
    url = f"{BASE_URL}/api/accounts"
    headers = {"Authorization": f"Bearer {token}"}
    payload = {
        "accountType": account_type,
        "currency": currency
    }
    response = requests.post(url, json=payload, headers=headers, timeout=TIMEOUT)
    response.raise_for_status()
    data = response.json()
    assert data.get("success") is True
    account_data = data.get("data")
    assert account_data is not None
    account_id = account_data.get("id")
    assert isinstance(account_id, int)
    return account_id

def delete_account(token, account_id):
    # There is no delete api documented; ignoring deletion
    # as no explicit delete endpoint given in PRD for accounts.
    # If needed, could be implemented here.
    pass

def test_get_account_statement_api():
    token = None
    account_id = None
    headers = {}
    try:
        # Register a user and get token
        token = register_user()
        headers = {"Authorization": f"Bearer {token}"}
        # Create a new account to test statement
        account_id = create_account(token)
        # Call the statement endpoint
        url = f"{BASE_URL}/api/accounts/{account_id}/statement"
        response = requests.get(url, headers=headers, timeout=TIMEOUT)
        response.raise_for_status()
        resp_json = response.json()
        # Assert top level success & message
        assert resp_json.get("success") is True
        assert "message" in resp_json
        data = resp_json.get("data")
        assert isinstance(data, dict)
        # Validate statement content list
        content = data.get("content")
        assert isinstance(content, list)
        for txn in content:
            # Each transaction should have specified keys and types
            assert isinstance(txn.get("id"), str)
            assert isinstance(txn.get("transactionId"), str)
            assert isinstance(txn.get("transactionType"), str)
            assert isinstance(txn.get("amount"), (int, float))
            assert isinstance(txn.get("fee"), (int, float))
            # description may be None or str
            desc = txn.get("description")
            assert desc is None or isinstance(desc, str)
            assert isinstance(txn.get("fromAccountNumber"), (str, type(None)))
            assert isinstance(txn.get("toAccountNumber"), (str, type(None)))
            assert isinstance(txn.get("status"), str)
            # createdAt should be str datetime format (basic check)
            created_at = txn.get("createdAt")
            assert isinstance(created_at, str)
        # Validate pagination keys and their types
        assert isinstance(data.get("pageable"), dict)
        assert isinstance(data.get("totalElements"), int)
        assert isinstance(data.get("totalPages"), int)
        assert isinstance(data.get("size"), int)
        assert isinstance(data.get("number"), int)
        assert isinstance(data.get("first"), bool)
        assert isinstance(data.get("last"), bool)
        assert isinstance(data.get("empty"), bool)
    finally:
        # No delete account API provided; so no cleanup possible
        pass

test_get_account_statement_api()