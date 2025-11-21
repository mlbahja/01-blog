#!/bin/bash

# Login and get token
TOKEN=$(curl -s -X POST http://localhost:9000/auth/login -H "Content-Type: application/json" -d '{"email":"test@test.com","password":"test123"}' | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

echo "Got token: ${TOKEN:0:20}..."

# Add comments to posts 1-10
comments=("Great post!" "Very informative" "Thanks for sharing" "I learned a lot" "Nice work" "Interesting topic" "Well written" "Good content" "Keep it up" "Awesome")

for i in {1..10}; do
  comment="${comments[$((i-1))]}"
  curl -s -X POST "http://localhost:9000/auth/posts/$i/comments" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d "{\"content\":\"$comment on post $i\"}" > /dev/null
  echo "Added comment to post $i"
done

echo "Done adding comments!"
