import requests

BASE_URL = "http://localhost:8080"
TIMEOUT = 30

def test_get_current_user_api():
    # First, register a new user
    register_url = f"{BASE_URL}/api/auth/register"
    login_url = f"{BASE_URL}/api/auth/login"
    me_url = f"{BASE_URL}/api/auth/me"
    user_data = {
        "username": "testuser_tc003",
        "email": "testuser_tc003@example.com",
        "password": "TestPassword123!",
        "fullName": "Test User TC003",
        "phone": "+1234567890"
    }
    headers = {"Content-Type": "application/json"}

    token = None
    try:
        # Register user
        resp = requests.post(register_url, json=user_data, headers=headers, timeout=TIMEOUT)
        assert resp.status_code == 200, f"Registration failed with status {resp.status_code}"
        body = resp.json()
        assert "success" in body and body["success"] is True
        assert "data" in body and "token" in body["data"]
        token = body["data"]["token"]

        # Login user to confirm and get fresh token (optional, but to confirm login)
        login_data = {
            "username": user_data["username"],
            "password": user_data["password"]
        }
        resp_login = requests.post(login_url, json=login_data, headers=headers, timeout=TIMEOUT)
        assert resp_login.status_code == 200, f"Login failed with status {resp_login.status_code}"
        body_login = resp_login.json()
        assert "success" in body_login and body_login["success"] is True
        assert "data" in body_login and "token" in body_login["data"]
        token = body_login["data"]["token"]

        # Use token to get current user info
        auth_headers = {
            "Authorization": f"Bearer {token}"
        }
        resp_me = requests.get(me_url, headers=auth_headers, timeout=TIMEOUT)
        assert resp_me.status_code == 200, f"Get current user failed with status {resp_me.status_code}"
        body_me = resp_me.json()
        assert "success" in body_me and body_me["success"] is True
        assert "data" in body_me

        user = body_me["data"]
        expected_fields = ["id", "username", "email", "fullName", "phone", "role", "isVerified", "createdAt"]
        for field in expected_fields:
            assert field in user, f"Field '{field}' missing in user data"

        # Validate the returned username/email matches registered info
        assert user["username"] == user_data["username"]
        assert user["email"] == user_data["email"]
        assert user["fullName"] == user_data["fullName"]
        assert user["phone"] == user_data["phone"]
        # Role and isVerified type checks
        assert isinstance(user["role"], str)
        assert isinstance(user["isVerified"], bool)

    finally:
        # Cleanup: if an admin endpoint existed to delete user, we would call it here
        # As no such info present, omit cleanup
        pass

test_get_current_user_api()