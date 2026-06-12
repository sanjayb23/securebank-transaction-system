import requests
import uuid

BASE_URL = "http://localhost:8080"
TIMEOUT = 30

def test_deposit_money_api():
    # Step 1: Register a new user
    register_url = f"{BASE_URL}/api/auth/register"
    unique_suffix = str(uuid.uuid4())[:8]
    user_data = {
        "username": f"user_{unique_suffix}",
        "email": f"user_{unique_suffix}@example.com",
        "password": "StrongPassword!123",
        "fullName": "Test User",
        "phone": "1234567890"
    }
    try:
        register_resp = requests.post(register_url, json=user_data, timeout=TIMEOUT)
        assert register_resp.status_code == 200, f"User registration failed: {register_resp.text}"
        register_resp_json = register_resp.json()
        assert register_resp_json.get("success") is True, f"User registration not successful: {register_resp.text}"
        token = register_resp_json["data"]["token"]
        assert token, "No token received after registration"

        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }

        # Step 2: Create a new bank account to deposit into
        create_account_url = f"{BASE_URL}/api/accounts"
        account_data = {
            "accountType": "SAVINGS",
            "currency": "USD"
        }
        create_acc_resp = requests.post(create_account_url, json=account_data, headers=headers, timeout=TIMEOUT)
        assert create_acc_resp.status_code == 200, f"Account creation failed: {create_acc_resp.text}"
        create_acc_json = create_acc_resp.json()
        assert create_acc_json.get("success") is True, f"Account creation not successful: {create_acc_resp.text}"
        account_id = create_acc_json["data"]["id"]

        # Step 3: Deposit money into the created account
        deposit_url = f"{BASE_URL}/api/transactions/deposit"
        deposit_amount = 150.75
        deposit_payload = {
            "accountId": account_id,
            "amount": deposit_amount
        }
        deposit_resp = requests.post(deposit_url, json=deposit_payload, headers=headers, timeout=TIMEOUT)
        assert deposit_resp.status_code == 200, f"Deposit request failed: {deposit_resp.text}"
        deposit_json = deposit_resp.json()
        assert deposit_json.get("success") is True, f"Deposit not successful: {deposit_resp.text}"
        data = deposit_json.get("data")
        assert data is not None, "Deposit response missing data"
        # Validate transaction details
        assert data.get("transactionType") == "DEPOSIT", "Transaction type is not DEPOSIT"
        assert abs(data.get("amount", 0) - deposit_amount) < 0.0001, "Deposit amount mismatch"
        assert data.get("status") in ("COMPLETED", "SUCCESS"), "Unexpected transaction status"
        assert data.get("toAccountNumber") is not None, "toAccountNumber missing in transaction data"
        assert data.get("id") is not None, "Transaction ID missing"
        assert data.get("transactionId") is not None, "Transaction external ID missing"
        assert data.get("createdAt") is not None, "Transaction creation time missing"
    finally:
        # Cleanup: Delete the created account and user if the API supported deletion
        # Since no delete endpoints provided in PRD, skipping cleanup.
        pass

test_deposit_money_api()