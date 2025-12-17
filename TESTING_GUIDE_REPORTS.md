# Report System - Testing Guide

This guide will help you test the complete Report functionality in your blog application.

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
   - Regular user account (for reporting)
   - Admin account (for managing reports)

---

## Step 1: Register Test Users

### Create Regular User
```bash
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "testuser@example.com",
  "password": "password123",
  "fullName": "Test User"
}
```

**Expected Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "testuser",
    "email": "testuser@example.com",
    "role": "USER"
  }
}
```

### Create Admin User
```bash
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "username": "admin",
  "email": "admin@example.com",
  "password": "admin123",
  "fullName": "Admin User"
}
```

**Note**: After registration, manually update the database to give admin role:
```sql
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';
```

---

## Step 2: Create Test Posts

### Login as Regular User
```bash
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

Save the token from response!

### Create a Post
```bash
POST http://localhost:8080/auth/posts
Authorization: Bearer YOUR_USER_TOKEN
Content-Type: application/json

{
  "title": "My Test Post",
  "content": "This is a test post that we will report",
  "mediaUrl": "https://example.com/image.jpg",
  "mediaType": "image",
  "tags": ["test", "demo"]
}
```

**Expected Response** (200 OK):
```json
{
  "id": 1,
  "title": "My Test Post",
  "content": "This is a test post that we will report",
  "author": {
    "id": 1,
    "username": "testuser"
  },
  "likeCount": 0,
  "createdAt": "2025-12-17T14:00:00"
}
```

**Save the post ID** (e.g., `1`) for reporting!

---

## Step 3: Test Report Creation (User)

### Create a Report
```bash
POST http://localhost:8080/auth/reports
Authorization: Bearer YOUR_USER_TOKEN
Content-Type: application/json

{
  "postId": 1,
  "message": "This post contains inappropriate content"
}
```

**Expected Response** (201 CREATED):
```json
{
  "id": 1,
  "reporterId": 1,
  "reporterUsername": "testuser",
  "postId": 1,
  "postTitle": "My Test Post",
  "message": "This post contains inappropriate content",
  "resolved": false,
  "createdAt": "2025-12-17T14:05:00",
  "adminNotes": null
}
```

### Get My Reports
```bash
GET http://localhost:8080/auth/reports/my
Authorization: Bearer YOUR_USER_TOKEN
```

**Expected Response** (200 OK):
```json
[
  {
    "id": 1,
    "reporterId": 1,
    "reporterUsername": "testuser",
    "postId": 1,
    "postTitle": "My Test Post",
    "message": "This post contains inappropriate content",
    "resolved": false,
    "createdAt": "2025-12-17T14:05:00",
    "adminNotes": null
  }
]
```

### Test Duplicate Report Prevention (Optional)
Try creating the same report again:
```bash
POST http://localhost:8080/auth/reports
Authorization: Bearer YOUR_USER_TOKEN
Content-Type: application/json

{
  "postId": 1,
  "message": "Another report on same post"
}
```

**Note**: The code has duplicate prevention commented out. If enabled, you'll get an error.

---

## Step 4: Test Admin Report Management

### Login as Admin
```bash
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Save the admin token!

### Get All Reports (Admin Only)
```bash
GET http://localhost:8080/auth/reports/admin/all
Authorization: Bearer YOUR_ADMIN_TOKEN
```

**Expected Response** (200 OK):
```json
[
  {
    "id": 1,
    "reporterId": 1,
    "reporterUsername": "testuser",
    "postId": 1,
    "postTitle": "My Test Post",
    "message": "This post contains inappropriate content",
    "resolved": false,
    "createdAt": "2025-12-17T14:05:00",
    "adminNotes": null
  }
]
```

### Get Unresolved Reports
```bash
GET http://localhost:8080/auth/reports/admin/unresolved
Authorization: Bearer YOUR_ADMIN_TOKEN
```

**Expected Response**: Same as above (since report is unresolved)

### Get Count of Unresolved Reports
```bash
GET http://localhost:8080/auth/reports/admin/count-unresolved
Authorization: Bearer YOUR_ADMIN_TOKEN
```

**Expected Response** (200 OK):
```json
{
  "count": 1
}
```

### Update Report (Mark as Resolved)
```bash
PUT http://localhost:8080/auth/reports/admin/1
Authorization: Bearer YOUR_ADMIN_TOKEN
Content-Type: application/json

{
  "resolved": true,
  "adminNotes": "Reviewed and removed inappropriate content"
}
```

**Expected Response** (200 OK):
```json
{
  "id": 1,
  "reporterId": 1,
  "reporterUsername": "testuser",
  "postId": 1,
  "postTitle": "My Test Post",
  "message": "This post contains inappropriate content",
  "resolved": true,
  "createdAt": "2025-12-17T14:05:00",
  "adminNotes": "Reviewed and removed inappropriate content"
}
```

### Verify Unresolved Count Decreased
```bash
GET http://localhost:8080/auth/reports/admin/count-unresolved
Authorization: Bearer YOUR_ADMIN_TOKEN
```

**Expected Response** (200 OK):
```json
{
  "count": 0
}
```

### Delete Report
```bash
DELETE http://localhost:8080/auth/reports/admin/1
Authorization: Bearer YOUR_ADMIN_TOKEN
```

**Expected Response** (200 OK):
```json
{
  "message": "Report deleted"
}
```

---

## Step 5: Test Security & Permissions

### Test 1: Regular User Cannot Access Admin Endpoints
```bash
GET http://localhost:8080/auth/reports/admin/all
Authorization: Bearer YOUR_USER_TOKEN
```

**Expected Response** (403 FORBIDDEN):
```json
{
  "error": "Access Denied"
}
```

### Test 2: Unauthenticated User Cannot Report
```bash
POST http://localhost:8080/auth/reports
Content-Type: application/json

{
  "postId": 1,
  "message": "Test report"
}
```

**Expected Response** (403 FORBIDDEN or 401 UNAUTHORIZED)

### Test 3: Report Non-Existent Post
```bash
POST http://localhost:8080/auth/reports
Authorization: Bearer YOUR_USER_TOKEN
Content-Type: application/json

{
  "postId": 99999,
  "message": "Reporting non-existent post"
}
```

**Expected Response** (400 BAD REQUEST):
```json
{
  "error": "Post not found"
}
```

---

## Expected Database State

After successful testing, check your database:

### Reports Table
```sql
SELECT * FROM reports;
```

**Expected columns**:
- id
- reporter_id (foreign key to users)
- post_id (foreign key to posts)
- message
- resolved (boolean)
- created_at (timestamp)
- admin_notes (text, nullable)

---

## Common Issues & Troubleshooting

### Issue 1: 403 Forbidden on Admin Endpoints
**Cause**: User doesn't have ADMIN role
**Solution**: Update database:
```sql
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';
```

### Issue 2: "Post not found" Error
**Cause**: Post ID doesn't exist
**Solution**: Create a post first or use existing post ID

### Issue 3: JWT Token Expired
**Cause**: Token expired (default: 24 hours)
**Solution**: Login again to get new token

### Issue 4: Cannot Report Own Post
**Note**: The current implementation allows users to report their own posts. If you want to prevent this, add this check in `ReportService.createReport()`:
```java
if (post.getAuthor().getId().equals(reporter.getId())) {
    throw new RuntimeException("Cannot report your own post");
}
```

---

## Testing Checklist

- [ ] User can register and login
- [ ] User can create posts
- [ ] User can report posts with a message
- [ ] User can view their own reports
- [ ] User CANNOT access admin report endpoints
- [ ] Admin can view all reports
- [ ] Admin can view unresolved reports
- [ ] Admin can count unresolved reports
- [ ] Admin can mark reports as resolved
- [ ] Admin can add admin notes to reports
- [ ] Admin can delete reports
- [ ] Reports include timestamps
- [ ] Error handling works (non-existent posts, unauthorized access)

---

## Success Criteria

✅ All API endpoints return expected responses
✅ Security restrictions work correctly
✅ Data is persisted in database
✅ Admin can manage reports effectively
✅ Users can only see their own reports
✅ Error messages are clear and helpful

---

## Next Steps

Once reports are working:
1. Test with frontend Angular application
2. Add email notifications for admins on new reports
3. Add report categories (spam, harassment, inappropriate, etc.)
4. Add bulk actions for admins (resolve multiple reports)
5. Add report history/audit log
