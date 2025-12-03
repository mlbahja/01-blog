# Blogger App - Complete Testing Guide

## ✅ What's Implemented

### 1. **Authentication System**
- ✅ User Registration
- ✅ User Login
- ✅ JWT Token Authentication (24-hour expiration)
- ✅ Auto-login after registration

### 2. **Post Management**
- ✅ Create posts
- ✅ View all posts
- ✅ View single post
- ✅ Delete posts
- ✅ Like/Unlike posts
- ✅ Check if user liked a post

### 3. **Comment System**
- ✅ Add comments to posts
- ✅ Like/Unlike comments **(NEW!)**
- ✅ Check if user liked a comment **(NEW!)**
- ✅ View comments on posts

### 4. **User Profile**
- ✅ View own profile (`/auth/users/me`)
- ✅ View other user profiles (`/auth/users/{id}`)
- ✅ Update profile (full name, bio, avatar, profile picture)
- ✅ Change password
- ✅ Delete account

### 5. **Admin Panel**
- ✅ Dashboard statistics
- ✅ View all users
- ✅ Ban/Unban users
- ✅ Change user roles
- ✅ Delete users
- ✅ View all posts
- ✅ Delete posts

---

## 🔧 Setup Instructions

### 1. Update Admin User Role

The admin user has been created but needs role upgrade in the database.

**Option A: Using MySQL Workbench or phpMyAdmin**
```sql
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';
```

**Option B: Using command line (if MySQL is accessible)**
```bash
mysql -u root -p -P 3307 -h localhost blog_db
# Enter password: pass123
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';
SELECT id, username, email, role FROM users WHERE username = 'admin';
```

### 2. Start Backend
```bash
cd /mnt/c/Users/mlbah/bloging/01-blog/blogger/backend
./mvnw spring-boot:run
```

### 3. Start Frontend
```bash
cd /mnt/c/Users/mlbah/bloging/01-blog/blogger/frontend
ng serve
```

---

## 🧪 Testing Guide

### **Test 1: Regular User Features**

#### 1.1 Register and Login
1. Go to `http://localhost:4200`
2. Click "Register"
3. Create account:
   - Username: `testuser1`
   - Email: `testuser1@test.com`
   - Password: `password123`
4. Should auto-login after registration

#### 1.2 Create a Post
1. Click "Create Post" button
2. Enter:
   - Title: "My First Post"
   - Content: "This is my first blog post!"
3. Click "Publish"
4. ✅ Should see success message and post appears in feed

#### 1.3 Like a Post
1. Click the heart icon on any post
2. ✅ Like count should increase
3. Click again to unlike
4. ✅ Like count should decrease

#### 1.4 Add a Comment
1. Click on a post to expand it
2. Type a comment in the input field
3. Press Enter or click submit
4. ✅ Comment should appear below the post

#### 1.5 Like a Comment **(NEW!)**
1. Find the heart icon next to any comment
2. Click to like
3. ✅ Comment like count should increase
4. Click again to unlike

#### 1.6 View Profile
1. Click on your username or "Profile" link
2. URL should be: `http://localhost:4200/auth/users/me`
3. ✅ Should see your profile information

#### 1.7 Update Profile
1. On profile page, click "Edit Profile"
2. Update:
   - Full Name
   - Bio
   - Avatar URL
3. Click "Save"
4. ✅ Changes should be saved and displayed

#### 1.8 Change Password
1. On profile page, click "Change Password"
2. Enter:
   - Current password
   - New password
   - Confirm new password
3. Click "Change Password"
4. ✅ Should see success message

---

### **Test 2: Admin Features**

#### 2.1 Login as Admin
1. Logout if currently logged in
2. Login with:
   - Username: `admin`
   - Password: `admin123`
3. ✅ Should see admin dashboard link

#### 2.2 Access Admin Dashboard
1. Navigate to admin panel (usually `/admin` or `/auth/admin`)
2. ✅ Should see:
   - Total users count
   - Total posts count
   - Active users count
   - Banned users count
   - Posts today
   - New users this week

#### 2.3 View All Users
1. In admin panel, go to "Users" section
2. ✅ Should see list of all registered users
3. ✅ Each user should show: username, email, role, ban status

#### 2.4 Ban a User
1. Find a regular user in the list
2. Click "Ban" button
3. ✅ User status should change to "Banned"
4. ✅ That user should not be able to login

#### 2.5 Unban a User
1. Find the banned user
2. Click "Unban" button
3. ✅ User status should change back to active

#### 2.6 Change User Role
1. Find a regular user
2. Click "Promote to Admin" or similar button
3. ✅ User role should change to ADMIN

#### 2.7 Delete a Post (Moderation)
1. In admin panel, go to "Posts" section
2. Find any inappropriate post
3. Click "Delete"
4. ✅ Post should be removed from system

#### 2.8 Delete a User
1. In admin panel, find a user to delete
2. Click "Delete User"
3. Confirm deletion
4. ✅ User should be removed from system

---

## 📝 API Endpoints Reference

### Authentication
```
POST   /auth/register          - Register new user
POST   /auth/login             - Login user
GET    /auth/home              - Test endpoint
```

### Posts
```
GET    /auth/posts             - Get all posts
GET    /auth/posts/{id}        - Get single post
POST   /auth/posts             - Create post (auth required)
DELETE /auth/posts/{id}        - Delete post (auth required)
POST   /auth/posts/{id}/like   - Like post (auth required)
DELETE /auth/posts/{id}/like   - Unlike post (auth required)
GET    /auth/posts/{id}/liked  - Check if user liked post
```

### Comments
```
POST   /auth/posts/{postId}/comments                          - Add comment (auth required)
POST   /auth/posts/{postId}/comments/{commentId}/like        - Like comment (NEW!)
DELETE /auth/posts/{postId}/comments/{commentId}/like        - Unlike comment (NEW!)
GET    /auth/posts/{postId}/comments/{commentId}/liked       - Check if user liked comment (NEW!)
```

### User Profile
```
GET    /auth/users/me          - Get current user profile
GET    /auth/users/{id}        - Get user profile by ID
PUT    /auth/users/{id}        - Update user profile
PUT    /auth/users/{id}/password - Change password
DELETE /auth/users/{id}        - Delete user account
```

### Admin
```
GET    /auth/admin/stats              - Get dashboard statistics
GET    /auth/admin/users              - Get all users
PUT    /auth/admin/users/{id}/ban     - Ban a user
PUT    /auth/admin/users/{id}/unban   - Unban a user
PUT    /auth/admin/users/{id}/role    - Change user role
DELETE /auth/admin/users/{id}         - Delete a user
GET    /auth/admin/posts              - Get all posts
DELETE /auth/admin/posts/{id}         - Delete a post
```

---

## 🐛 Known Issues and Fixes

### Issue 1: Posts/Comments Creation Failed (403 Error)
**Status:** ✅ FIXED
- Fixed JWT interceptor in `main.ts`
- Fixed JWT token expiration (24 hours)

### Issue 2: Comments Creation Failed (500 StackOverflow)
**Status:** ✅ FIXED
- Fixed circular JSON serialization
- Added `@ToString` and `@EqualsAndHashCode` exclusions

### Issue 3: Admin Role Not Working
**Status:** ⚠️ MANUAL STEP REQUIRED
- Need to update user role in database using SQL
- See setup instructions above

---

## 🚀 New Features Added

1. **Comment Likes** - Users can now like/unlike comments
2. **Comment Like Count** - Display number of likes on each comment
3. **Better Error Handling** - More descriptive error messages
4. **Admin Account Creation** - Automated script to create admin user

---

## 📱 Access URLs

- **Frontend:** http://localhost:4200
- **Backend API:** http://localhost:8080
- **User Home:** http://localhost:4200/auth/home
- **User Profile:** http://localhost:4200/auth/users/me
- **Admin Panel:** http://localhost:4200/admin (or wherever you configured it)

---

## 🔑 Test Accounts

### Admin Account
- **Username:** admin
- **Email:** admin@blogger.com
- **Password:** admin123
- **Role:** ADMIN (after database update)

### Regular User (if created)
- **Username:** testuser
- **Email:** test@example.com
- **Password:** password123
- **Role:** USER

---

## ✅ Testing Checklist

- [ ] User registration works
- [ ] User login works
- [ ] Create post works
- [ ] Like/unlike post works
- [ ] Add comment works
- [ ] Like/unlike comment works (NEW!)
- [ ] View profile works
- [ ] Update profile works
- [ ] Change password works
- [ ] Admin can access dashboard
- [ ] Admin can view all users
- [ ] Admin can ban/unban users
- [ ] Admin can change user roles
- [ ] Admin can delete posts
- [ ] Admin can delete users

---

Happy Testing! 🎉
