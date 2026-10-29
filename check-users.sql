-- Connect to PostgreSQL and run these queries

-- 1. Check all users
SELECT id, username, email, full_name, role, is_verified, created_at 
FROM users;

-- 2. Check user passwords (hashed)
SELECT username, password 
FROM users;

-- 3. Check accounts for users
SELECT u.username, a.account_number, a.account_type, a.balance, a.status
FROM users u 
LEFT JOIN accounts a ON u.id = a.user_id;

-- 4. Check if any test users exist
SELECT * FROM users WHERE username LIKE '%test%' OR email LIKE '%test%';