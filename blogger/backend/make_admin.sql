-- Update specific users to ADMIN role
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';
UPDATE users SET role = 'ADMIN' WHERE username = 'superadmin';

-- Verify the update
SELECT id, username, email, role, created_at FROM users WHERE role = 'ADMIN';
