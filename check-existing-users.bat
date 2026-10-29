@echo off
echo Checking existing users in database...

docker exec -it postgres psql -U postgres -d securebank -c "SELECT username, email, full_name FROM users;"

pause