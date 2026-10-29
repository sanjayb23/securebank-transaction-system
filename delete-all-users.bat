@echo off
echo WARNING: This will delete ALL users and related data!
echo Press any key to continue or Ctrl+C to cancel...
pause

echo Deleting all data from database...

docker exec -it postgres psql -U postgres -d securebank -c "
DELETE FROM audit_logs;
DELETE FROM transactions;
DELETE FROM accounts;
DELETE FROM users;
"

echo All users deleted successfully!
pause