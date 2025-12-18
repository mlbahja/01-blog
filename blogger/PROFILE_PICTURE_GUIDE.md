# Profile Picture Feature - User Guide

## ✅ Feature is Fully Implemented and Ready to Use!

Your blog application now has complete profile picture functionality. Users can upload images from their device or use image URLs.

## How to Use

### For Users:

1. **Navigate to Profile Edit**
   - Click on "Edit Profile" from your profile page
   - Or go to `/profile/edit`

2. **Upload from Device**
   - Click "Choose a profile picture from your device"
   - Select an image file (JPG, PNG, GIF, etc.)
   - Click "Upload Now"
   - Wait for success message
   - Click "Save Changes" to apply

3. **Use Image URL (Alternative)**
   - Enter an image URL in the "Profile Picture URL" field
   - Click "Save Changes"

4. **View Your Profile**
   - Your uploaded image will appear on your profile page
   - It will also display throughout the app

## Technical Details

### Backend Endpoints

**Upload Profile Picture:**
```
POST /auth/users/upload-profile-picture
Content-Type: multipart/form-data
Body: file (MultipartFile)
Response: { url: string, filename: string, message: string }
```

**Update Profile (including picture URL):**
```
PUT /auth/users/{id}
Body: { profilePictureUrl?: string, ... }
```

**Serve Uploaded Files:**
```
GET /uploads/{filename}
```

### Frontend Components

- **ProfileEditComponent** - UI for uploading pictures
- **ProfileComponent** - Displays profile picture
- **UserService** - Handles API calls

### File Storage

- Uploaded files are stored in: `backend/uploads/`
- Files are renamed with UUIDs to prevent conflicts
- Only image files are accepted for profile pictures

## Security Features

✅ File validation (images only for profile pictures)
✅ Unique filename generation (UUID)
✅ Path traversal prevention
✅ Banned user check before upload
✅ Authentication required

## Supported Image Formats

- JPEG/JPG
- PNG
- GIF
- WebP
- Other standard image formats

## Image URL Fallback

Users have TWO options:
1. **profilePictureUrl** - Uploaded image (priority)
2. **avatar** - External URL (fallback)

The app will show profilePictureUrl first, then avatar, then placeholder.

## Testing

To test the feature:

1. **Login** to your account
2. **Navigate** to Edit Profile
3. **Choose** an image from your computer
4. **Click** "Upload Now"
5. **Wait** for success message
6. **Click** "Save Changes"
7. **Go** to your profile to see the uploaded image

## Configuration

File upload directory can be configured in `application.properties`:
```properties
file.upload-dir=uploads
```

Default: `uploads/` directory in project root

## Notes

- Max file size is determined by Spring Boot defaults
- Images are served at: `http://localhost:8080/uploads/{filename}`
- Frontend automatically constructs the full URL
- Error handling shows toast notifications to users
