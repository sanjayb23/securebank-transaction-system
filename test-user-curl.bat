@echo off
echo Testing user registration...

curl -X POST http://localhost:8080/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"Test123!@#\",\"fullName\":\"Test User\",\"phone\":\"9876543210\"}"

echo.
echo.
echo Testing login...

curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser\",\"password\":\"Test123!@#\"}"

pause