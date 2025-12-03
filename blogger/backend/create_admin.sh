#!/bin/bash

echo "=========================================="
echo "Creating Admin User"
echo "=========================================="
echo ""

# Register admin user
echo "Step 1: Registering admin user..."
RESPONSE=$(curl -s -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","email":"admin@blogger.com","password":"admin123"}')

echo "$RESPONSE"
echo ""

# Extract user ID
USER_ID=$(echo "$RESPONSE" | grep -o '"id":[0-9]*' | cut -d':' -f2)

if [ -z "$USER_ID" ]; then
  echo "❌ Failed to create user"
  exit 1
fi

echo "✓ User created with ID: $USER_ID"
echo ""

# Login to get admin token
echo "Step 2: Logging in as admin..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "❌ Failed to login"
  exit 1
fi

echo "✓ Logged in successfully"
echo "Token: ${TOKEN:0:50}..."
echo ""

echo "=========================================="
echo "Admin Account Created!"
echo "=========================================="
echo ""
echo "Credentials:"
echo "  Username: admin"
echo "  Email: admin@blogger.com"
echo "  Password: admin123"
echo ""
echo "⚠️  IMPORTANT: You need to manually change this user's role to ADMIN in the database:"
echo ""
echo "SQL Command:"
echo "  UPDATE users SET role = 'ADMIN' WHERE username = 'admin';"
echo ""
echo "Or use MySQL command line:"
echo "  mysql -u root -p -P 3307 -h localhost blog_db"
echo "  UPDATE users SET role = 'ADMIN' WHERE username = 'admin';"
echo ""
echo "=========================================="
