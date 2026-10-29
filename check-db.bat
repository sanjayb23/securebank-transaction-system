@echo off
echo Connecting to PostgreSQL database...

REM Connect to PostgreSQL container
docker exec -it postgres psql -U postgres -d securebank -c "SELECT id, username, email, full_name, role FROM users;"

pause