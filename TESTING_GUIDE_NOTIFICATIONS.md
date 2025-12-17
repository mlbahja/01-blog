# Notification System - Testing Guide

This guide will help you test the complete Notification functionality in your blog application.

---

## Prerequisites

1. **Start the backend server**:
   ```bash
   cd blogger/backend
   ./mvnw spring-boot:run
   ```
   Server will run on: `http://localhost:8080`

2. **Tools needed**:
   - Postman, Insomnia, or cURL
   - Or use Angular frontend (if available)

3. **Test accounts needed**:
   - At least 2 user accounts to test follow/notification features

---

## Overview: Notification Types

Your notification system supports 5 types:

1. **NEW_POST** - Someone you follow published a new post
2. **NEW_FOLLOWER** - Someone followed you
3. **POST_LIKE** - Someone liked your post
4. **COMMENT** - Someone commented on your post
5. **COMMENT_LIKE** - Someone liked your comment

---

## Step 1: Create Test Users

### Create User 1 (Alice)
```bash
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "username": "alice",
  "email": "alice@example.com",
  "password": "password123",
  "fullName": "Alice Smith"
}
```

**Save Alice's token!**

### Create User 2 (Bob)
```bash
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "username": "bob",
  "email": "bob@example.com",
  "password": "password123",
  "fullName": "Bob Johnson"
}
```

**Save Bob's token!**

---

## Step 2: Test NEW_FOLLOWER Notifications

### Bob Follows Alice
```bash
POST http://localhost:8080/auth/users/1/follow
Authorization: Bearer BOB_TOKEN
```

(Replace `1` with Alice's user ID)

**Expected Response** (200 OK):
```json
{
  "message": "Successfully followed user"
}
```

### Alice Checks Notifications
```bash
GET http://localhost:8080/auth/notifications/unread
Authorization: Bearer ALICE_TOKEN
```

**Expected Response** (200 OK):
```json
[
  {
    "id": 1,
    "user": {
      "id": 1,
      "username": "alice"
    },
    "message": "bob started following you",
    "type": "NEW_FOLLOWER",
    "relatedPostId": null,
    "relatedUserId": 2,
    "isRead": false,
    "createdAt": "2025-12-17T14:30:00"
  }
]
```

### Alice Checks Unread Count
```bash
GET http://localhost:8080/auth/notifications/unread/count
Authorization: Bearer ALICE_TOKEN
```

**Expected Response** (200 OK):
```json
{
  "count": 1
}
```

---

## Step 3: Test NEW_POST Notifications

### Alice Creates a Post
```bash
POST http://localhost:8080/auth/posts
Authorization: Bearer ALICE_TOKEN
Content-Type: application/json

{
  "title": "Alice's First Post",
  "content": "Hello everyone! This is my first post.",
  "mediaUrl": "https://example.com/image.jpg",
  "mediaType": "image",
  "tags": ["hello", "first-post"]
}
```

**Expected Response** (200 OK) with post details

### Bob Checks Notifications (He Follows Alice)
```bash
GET http://localhost:8080/auth/notifications/unread
Authorization: Bearer BOB_TOKEN
```

**Expected Response** (200 OK):
```json
[
  {
    "id": 2,
    "user": {
      "id": 2,
      "username": "bob"
    },
    "message": "alice published a new post: Alice's First Post",
    "type": "NEW_POST",
    "relatedPostId": 1,
    "relatedUserId": 1,
    "isRead": false,
    "createdAt": "2025-12-17T14:35:00"
  }
]
```

---

## Step 4: Test POST_LIKE Notifications

### Bob Likes Alice's Post
```bash
POST http://localhost:8080/auth/posts/1/like
Authorization: Bearer BOB_TOKEN
```

(Replace `1` with the post ID from Step 3)

**Expected Response** (200 OK):
```json
{
  "message": "Post liked",
  "likeCount": 1
}
```

### Alice Checks New Notifications
```bash
GET http://localhost:8080/auth/notifications/unread
Authorization: Bearer ALICE_TOKEN
```

**Expected Response** (200 OK):
```json
[
  {
    "id": 1,
    "message": "bob started following you",
    "type": "NEW_FOLLOWER",
    "isRead": false,
    "createdAt": "2025-12-17T14:30:00"
  },
  {
    "id": 3,
    "message": "bob liked your post: Alice's First Post",
    "type": "POST_LIKE",
    "relatedPostId": 1,
    "relatedUserId": 2,
    "isRead": false,
    "createdAt": "2025-12-17T14:40:00"
  }
]
```

---

## Step 5: Test COMMENT Notifications

### Bob Comments on Alice's Post
```bash
POST http://localhost:8080/auth/posts/1/comments
Authorization: Bearer BOB_TOKEN
Content-Type: application/json

{
  "content": "Great post, Alice!"
}
```

**Expected Response** (200 OK):
```json
{
  "message": "Comment added successfully",
  "commentContent": "Great post, Alice!",
  "author": "bob"
}
```

### Alice Checks Notifications
```bash
GET http://localhost:8080/auth/notifications/unread
Authorization: Bearer ALICE_TOKEN
```

**Expected Response** (200 OK) - Should now have 3 unread notifications including:
```json
{
  "id": 4,
  "message": "bob commented on your post: Alice's First Post",
  "type": "COMMENT",
  "relatedPostId": 1,
  "relatedUserId": 2,
  "isRead": false,
  "createdAt": "2025-12-17T14:45:00"
}
```

---

## Step 6: Test Notification Management

### Get All Notifications (Paginated)
```bash
GET http://localhost:8080/auth/notifications/paginated?page=0&size=10
Authorization: Bearer ALICE_TOKEN
```

**Expected Response** (200 OK):
```json
{
  "content": [
    { "id": 4, "message": "bob commented...", "isRead": false },
    { "id": 3, "message": "bob liked...", "isRead": false },
    { "id": 1, "message": "bob started following...", "isRead": false }
  ],
  "totalElements": 3,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```

### Mark Specific Notification as Read
```bash
PUT http://localhost:8080/auth/notifications/1/read
Authorization: Bearer ALICE_TOKEN
```

**Expected Response** (200 OK):
```json
{
  "message": "Notification marked as read"
}
```

### Verify Unread Count Decreased
```bash
GET http://localhost:8080/auth/notifications/unread/count
Authorization: Bearer ALICE_TOKEN
```

**Expected Response** (200 OK):
```json
{
  "count": 2
}
```

### Mark All Notifications as Read
```bash
PUT http://localhost:8080/auth/notifications/read-all
Authorization: Bearer ALICE_TOKEN
```

**Expected Response** (200 OK):
```json
{
  "message": "All notifications marked as read"
}
```

### Verify All Are Read
```bash
GET http://localhost:8080/auth/notifications/unread/count
Authorization: Bearer ALICE_TOKEN
```

**Expected Response** (200 OK):
```json
{
  "count": 0
}
```

### Delete Specific Notification
```bash
DELETE http://localhost:8080/auth/notifications/1
Authorization: Bearer ALICE_TOKEN
```

**Expected Response** (200 OK):
```json
{
  "message": "Notification deleted successfully"
}
```

### Delete All Read Notifications (Cleanup)
```bash
DELETE http://localhost:8080/auth/notifications/read
Authorization: Bearer ALICE_TOKEN
```

**Expected Response** (200 OK):
```json
{
  "message": "Read notifications deleted successfully"
}
```

---

## Step 7: Test Edge Cases

### Test 1: User Doesn't Get Notification for Their Own Actions

**Bob likes his own post:**
```bash
POST http://localhost:8080/auth/posts/2/like
Authorization: Bearer BOB_TOKEN
```

Bob should NOT receive a notification (code prevents self-notifications).

### Test 2: Notifications Don't Go to Banned Users

**Ban Alice (requires admin):**
```sql
UPDATE users SET is_banned = true WHERE username = 'alice';
```

**Bob performs an action (like, comment, follow):**
Alice should NOT receive any notifications while banned.

### Test 3: Multiple Followers Get Notifications

**Create User 3 (Charlie):**
```bash
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "username": "charlie",
  "email": "charlie@example.com",
  "password": "password123",
  "fullName": "Charlie Brown"
}
```

**Charlie Follows Alice:**
```bash
POST http://localhost:8080/auth/users/1/follow
Authorization: Bearer CHARLIE_TOKEN
```

**Alice Creates New Post:**
```bash
POST http://localhost:8080/auth/posts
Authorization: Bearer ALICE_TOKEN
Content-Type: application/json

{
  "title": "Alice's Second Post",
  "content": "Another great post!",
  "tags": ["update"]
}
```

**Both Bob and Charlie should receive NEW_POST notifications!**

Verify Bob's notifications:
```bash
GET http://localhost:8080/auth/notifications/unread
Authorization: Bearer BOB_TOKEN
```

Verify Charlie's notifications:
```bash
GET http://localhost:8080/auth/notifications/unread
Authorization: Bearer CHARLIE_TOKEN
```

---

## Step 8: Test Security

### Test 1: Cannot Access Other User's Notifications
```bash
GET http://localhost:8080/auth/notifications
Authorization: Bearer BOB_TOKEN
```

Bob should only see HIS notifications, not Alice's.

### Test 2: Cannot Mark Other User's Notification as Read
```bash
PUT http://localhost:8080/auth/notifications/1/read
Authorization: Bearer BOB_TOKEN
```

If notification ID 1 belongs to Alice, Bob should get an error:
```json
{
  "error": "Unauthorized to mark this notification as read"
}
```

### Test 3: Unauthenticated Access Denied
```bash
GET http://localhost:8080/auth/notifications
```

**Expected Response** (401 UNAUTHORIZED or 403 FORBIDDEN)

---

## Expected Database State

### Notifications Table
```sql
SELECT * FROM notifications;
```

**Expected columns**:
- id
- user_id (foreign key to users)
- message
- type (enum: NEW_POST, NEW_FOLLOWER, POST_LIKE, COMMENT, COMMENT_LIKE)
- related_post_id (nullable)
- related_user_id (nullable)
- is_read (boolean, default false)
- created_at (timestamp)

**Sample data**:
```
| id | user_id | message                                    | type          | related_post_id | related_user_id | is_read | created_at          |
|----|---------|-------------------------------------------|---------------|-----------------|-----------------|---------|---------------------|
| 1  | 1       | bob started following you                 | NEW_FOLLOWER  | NULL            | 2               | true    | 2025-12-17 14:30:00 |
| 2  | 2       | alice published a new post: Alice's...    | NEW_POST      | 1               | 1               | false   | 2025-12-17 14:35:00 |
| 3  | 1       | bob liked your post: Alice's First Post   | POST_LIKE     | 1               | 2               | true    | 2025-12-17 14:40:00 |
| 4  | 1       | bob commented on your post: Alice's...    | COMMENT       | 1               | 2               | true    | 2025-12-17 14:45:00 |
```

---

## Testing Workflow Diagram

```
Alice (User 1)          Bob (User 2)
     |                       |
     |<------- follow -------|  ✅ Alice gets NEW_FOLLOWER notification
     |                       |
     |---- create post ----->|  ✅ Bob gets NEW_POST notification
     |                       |
     |<------- like ---------|  ✅ Alice gets POST_LIKE notification
     |                       |
     |<----- comment --------|  ✅ Alice gets COMMENT notification
     |                       |
```

---

## Common Issues & Troubleshooting

### Issue 1: Notifications Not Created
**Possible causes**:
- User is banned (notifications disabled for banned users)
- User performing action on their own content (self-notifications prevented)
- Transaction not committed

**Solution**: Check user ban status, verify action is by different user

### Issue 2: Duplicate Notifications
**Cause**: Multiple rapid actions trigger multiple notifications
**Solution**: This is expected behavior. Each action creates a notification.

### Issue 3: Cannot See Notifications
**Cause**: Using wrong user's token
**Solution**: Verify JWT token belongs to the user who should receive notifications

### Issue 4: JWT Token Expired
**Cause**: Token expired (default: 24 hours)
**Solution**: Login again to get new token

---

## Testing Checklist

- [ ] User receives notification when someone follows them
- [ ] Followers receive notification when user posts
- [ ] Post author receives notification on post like
- [ ] Post author receives notification on comment
- [ ] User can view all notifications
- [ ] User can view only unread notifications
- [ ] User can get unread notification count
- [ ] User can mark specific notification as read
- [ ] User can mark all notifications as read
- [ ] User can delete specific notification
- [ ] User can delete all read notifications
- [ ] Pagination works correctly
- [ ] Self-notifications are prevented
- [ ] Banned users don't receive notifications
- [ ] Users can only access their own notifications
- [ ] Multiple followers all get notified
- [ ] Timestamps are accurate

---

## API Endpoints Summary

### User Endpoints (All Require Authentication)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/auth/notifications` | Get all notifications |
| GET | `/auth/notifications/paginated?page=0&size=20` | Get paginated notifications |
| GET | `/auth/notifications/unread` | Get unread notifications |
| GET | `/auth/notifications/unread/count` | Get unread count |
| PUT | `/auth/notifications/{id}/read` | Mark specific as read |
| PUT | `/auth/notifications/read-all` | Mark all as read |
| DELETE | `/auth/notifications/{id}` | Delete specific notification |
| DELETE | `/auth/notifications/read` | Delete all read notifications |

---

## Success Criteria

✅ All notification types are created correctly
✅ Users receive appropriate notifications
✅ Self-notifications are prevented
✅ Banned users don't receive notifications
✅ Notifications can be marked as read
✅ Notifications can be deleted
✅ Security works (users can't access others' notifications)
✅ Pagination works correctly
✅ Unread count is accurate
✅ Multiple followers all receive notifications

---

## Integration with Frontend

When integrating with Angular frontend:

1. **Poll for notifications** every 30-60 seconds:
   ```typescript
   setInterval(() => {
     this.checkUnreadCount();
   }, 30000);
   ```

2. **Show badge** with unread count in navbar

3. **Real-time updates** (optional):
   - Use WebSockets/SSE for instant notifications
   - Or implement push notifications

4. **Notification dropdown**:
   - Show latest 5-10 notifications
   - "See all" link to full notification page
   - Click notification to mark as read and navigate to related content

---

## Next Steps

Once notifications are working:
1. Add WebSocket support for real-time notifications
2. Add email notifications for important events
3. Add notification preferences (allow users to control which notifications they receive)
4. Add notification sound/visual effects
5. Add "mark all as read" in bulk
6. Add notification grouping (e.g., "Bob and 3 others liked your post")
