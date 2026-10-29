import requests
import uuid

BASE_URL = "http://localhost:8080"
REGISTER_ENDPOINT = "/api/auth/register"
TIMEOUT = 30


def test_user_registration_api():
    url = BASE_URL + REGISTER_ENDPOINT
    unique_str = str(uuid.uuid4()).replace("-", "")[:8]
    payload = {
        "username": f"testuser_{unique_str}",
        "email": f"testuser_{unique_str}@example.com",
        "password": "StrongPass!23",
        "fullName": "Test User",
        "phone": "+1234567890"
    }
    headers = {
        "Content-Type": "application/json"
    }

    try:
        response = requests.post(url, json=payload, headers=headers, timeout=TIMEOUT)
    except requests.RequestException as e:
        assert False, f"Request to register user failed: {e}"

    assert response.status_code == 200, f"Expected status code 200, got {response.status_code}"
    try:
        resp_json = response.json()
    except Exception as e:
        assert False, f"Response is not valid JSON: {e}"

    assert isinstance(resp_json, dict), "Response JSON is not a dictionary"
    assert resp_json.get("success") is True, f"Expected success=True, got {resp_json.get('success')}"
    assert "message" in resp_json, "Response JSON missing 'message'"
    assert "data" in resp_json and isinstance(resp_json["data"], dict), "Response JSON missing 'data' or 'data' is not an object"
    data = resp_json["data"]
    assert "token" in data and isinstance(data["token"], str) and len(data["token"]) > 0, "JWT token missing or empty in response data"
    assert "type" in data and isinstance(data["type"], str) and len(data["type"]) > 0, "Token type missing or empty in response data"


test_user_registration_api()