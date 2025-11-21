#!/bin/bash

# Login and get token
TOKEN=$(curl -s -X POST http://localhost:9000/auth/login -H "Content-Type: application/json" -d '{"email":"test@test.com","password":"test123"}' | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

echo "Got token: ${TOKEN:0:20}..."

# Create 15 posts
for i in {2..16}; do
  curl -s -X POST "http://localhost:9000/auth/posts" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d "{\"title\":\"Post Number $i\",\"content\":\"This is the content of post number $i. It contains interesting information about various topics and ideas.\"}"
  echo "Created post $i"
done

echo "Done creating posts!"
