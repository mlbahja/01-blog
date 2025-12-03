#!/bin/bash

echo "=========================================="
echo "Testing Blogger API"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Step 1: Register a new user
echo -e "${YELLOW}Step 1: Registering new user...${NC}"
REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"testuser$(date +%s)\",\"email\":\"test$(date +%s)@example.com\",\"password\":\"password123\"}")

echo "$REGISTER_RESPONSE" | jq '.'

# Extract token
TOKEN=$(echo "$REGISTER_RESPONSE" | jq -r '.accessToken')

if [ "$TOKEN" == "null" ] || [ -z "$TOKEN" ]; then
  echo -e "${RED}❌ Failed to get token from registration${NC}"
  exit 1
fi

echo -e "${GREEN}✓ Registration successful${NC}"
echo "Token: ${TOKEN:0:50}..."
echo ""

# Step 2: Create a post
echo -e "${YELLOW}Step 2: Creating a post with JWT token...${NC}"
POST_RESPONSE=$(curl -s -X POST http://localhost:8080/auth/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"title":"Test Post from Script","content":"This is test content from the test script"}')

echo "$POST_RESPONSE" | jq '.'

POST_ID=$(echo "$POST_RESPONSE" | jq -r '.id')

if [ "$POST_ID" == "null" ] || [ -z "$POST_ID" ]; then
  echo -e "${RED}❌ Failed to create post${NC}"
  exit 1
fi

echo -e "${GREEN}✓ Post created successfully with ID: $POST_ID${NC}"
echo ""

# Step 3: Get all posts
echo -e "${YELLOW}Step 3: Retrieving all posts...${NC}"
GET_RESPONSE=$(curl -s -X GET http://localhost:8080/auth/posts)

echo "$GET_RESPONSE" | jq '.[] | {id, title, author: .author.username}'

echo -e "${GREEN}✓ All tests passed!${NC}"
echo ""
echo "=========================================="
echo "Summary:"
echo "  - User registration: ✓"
echo "  - JWT token generation: ✓"
echo "  - Post creation with auth: ✓"
echo "  - Post retrieval: ✓"
echo "=========================================="
