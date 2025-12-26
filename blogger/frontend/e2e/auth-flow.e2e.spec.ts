import { test, expect } from '@playwright/test';

/**
 * E2E Tests for Authentication Flow
 *
 * Tests the complete user authentication journey:
 * 1. User Registration
 * 2. User Login
 * 3. Access Protected Routes
 * 4. Logout
 */

const BASE_URL = 'http://localhost:4200';
const API_URL = 'http://localhost:8080';

// Test data
const testUser = {
  username: `testuser_${Date.now()}`,
  email: `testuser${Date.now()}@example.com`,
  password: 'TestPassword123!'
};

test.describe('Authentication Flow', () => {

  test.beforeEach(async ({ page }) => {
    // Navigate to the application
    await page.goto(BASE_URL);
  });

  test('Complete authentication flow: register -> login -> access protected routes -> logout', async ({ page }) => {

    // Step 1: Navigate to registration page
    await page.click('text=Register');
    await expect(page).toHaveURL(`${BASE_URL}/register`);

    // Step 2: Fill registration form
    await page.fill('input[name="username"]', testUser.username);
    await page.fill('input[name="email"]', testUser.email);
    await page.fill('input[name="password"]', testUser.password);

    // Step 3: Submit registration
    await page.click('button[type="submit"]');

    // Step 4: Wait for registration success and redirect to login
    await expect(page).toHaveURL(`${BASE_URL}/login`, { timeout: 10000 });

    // Step 5: Login with newly created account
    await page.fill('input[name="username"]', testUser.username);
    await page.fill('input[name="password"]', testUser.password);
    await page.click('button[type="submit"]');

    // Step 6: Verify successful login and redirect to home
    await expect(page).toHaveURL(`${BASE_URL}/home`, { timeout: 10000 });

    // Step 7: Verify user is logged in (check for user-specific elements)
    await expect(page.locator('text=Profile')).toBeVisible();
    await expect(page.locator('text=Logout')).toBeVisible();

    // Step 8: Access protected route - Profile
    await page.click('text=Profile');
    await expect(page).toHaveURL(new RegExp(`${BASE_URL}/profile`));

    // Step 9: Logout
    await page.click('text=Logout');
    await expect(page).toHaveURL(`${BASE_URL}/login`);

    // Step 10: Verify cannot access protected routes after logout
    await page.goto(`${BASE_URL}/home`);
    await expect(page).toHaveURL(`${BASE_URL}/login`);
  });

  test('Should prevent access to protected routes when not authenticated', async ({ page }) => {
    // Try to access home page
    await page.goto(`${BASE_URL}/home`);

    // Should redirect to login
    await expect(page).toHaveURL(`${BASE_URL}/login`);

    // Try to access profile page
    await page.goto(`${BASE_URL}/profile`);

    // Should redirect to login
    await expect(page).toHaveURL(`${BASE_URL}/login`);

    // Try to access admin page
    await page.goto(`${BASE_URL}/admin`);

    // Should redirect to login or unauthorized
    const url = page.url();
    expect(url).toMatch(/login|unauthorized/);
  });

  test('Should show validation errors for invalid registration', async ({ page }) => {
    await page.click('text=Register');

    // Try to submit empty form
    await page.click('button[type="submit"]');

    // Should show validation errors
    await expect(page.locator('text=/required|invalid/i')).toBeVisible();

    // Try with invalid email
    await page.fill('input[name="username"]', 'testuser');
    await page.fill('input[name="email"]', 'invalidemail');
    await page.fill('input[name="password"]', '123');
    await page.click('button[type="submit"]');

    // Should show email validation error
    await expect(page.locator('text=/email|valid/i')).toBeVisible();
  });

  test('Should show error for invalid login credentials', async ({ page }) => {
    await page.click('text=Login');

    // Try to login with invalid credentials
    await page.fill('input[name="username"]', 'nonexistentuser');
    await page.fill('input[name="password"]', 'wrongpassword');
    await page.click('button[type="submit"]');

    // Should show error message
    await expect(page.locator('text=/invalid|incorrect|failed/i')).toBeVisible();
  });
});

test.describe('Post Creation and Interaction Flow', () => {

  let authToken: string;

  test.beforeEach(async ({ page }) => {
    // Login before each test
    await page.goto(`${BASE_URL}/login`);
    await page.fill('input[name="username"]', testUser.username);
    await page.fill('input[name="password"]', testUser.password);
    await page.click('button[type="submit"]');
    await expect(page).toHaveURL(`${BASE_URL}/home`);

    // Get auth token from localStorage
    authToken = await page.evaluate(() => localStorage.getItem('jwt_token') || '');
  });

  test('Complete post lifecycle: create -> edit -> delete', async ({ page }) => {
    const postTitle = `Test Post ${Date.now()}`;
    const postContent = 'This is test content for E2E testing';

    // Step 1: Create a new post
    await page.fill('input[placeholder*="title" i]', postTitle);
    await page.fill('textarea[placeholder*="content" i]', postContent);
    await page.click('button:has-text("Post")');

    // Step 2: Verify post appears in feed
    await expect(page.locator(`text=${postTitle}`)).toBeVisible({ timeout: 5000 });

    // Step 3: Click on post to view details
    await page.click(`text=${postTitle}`);

    // Step 4: Verify post detail page
    await expect(page.locator('h1')).toContainText(postTitle);
    await expect(page.locator('text=' + postContent)).toBeVisible();

    // Step 5: Edit the post
    await page.click('button:has-text("Edit")');
    const updatedTitle = `${postTitle} - Updated`;
    await page.fill('input[name="editedTitle"]', updatedTitle);
    await page.click('button:has-text("Save")');

    // Step 6: Verify update
    await expect(page.locator('h1')).toContainText(updatedTitle);

    // Step 7: Delete the post
    await page.click('button:has-text("Delete")');
    page.on('dialog', dialog => dialog.accept()); // Accept confirmation dialog
    await page.click('button:has-text("Delete")'); // Click delete button

    // Step 8: Verify redirect to home
    await expect(page).toHaveURL(`${BASE_URL}/home`, { timeout: 5000 });
  });

  test('Like and unlike a post', async ({ page }) => {
    // Assuming there's at least one post in the feed
    const likeButton = page.locator('button:has-text("Like")').first();

    if (await likeButton.isVisible()) {
      // Get initial like count
      const likeCountText = await page.locator('.like-count').first().textContent();

      // Click like button
      await likeButton.click();

      // Wait for like count to update
      await page.waitForTimeout(1000);

      // Click unlike
      await page.locator('button:has-text("Unlike")').first().click();

      // Verify like count returned to original
      await page.waitForTimeout(1000);
      const finalLikeCountText = await page.locator('.like-count').first().textContent();
      expect(finalLikeCountText).toBe(likeCountText);
    }
  });

  test('Add a comment to a post', async ({ page }) => {
    // Find and click on first post
    const firstPost = page.locator('.post-card').first();
    if (await firstPost.isVisible()) {
      await firstPost.click();

      // Add a comment
      const commentText = `Test comment ${Date.now()}`;
      await page.fill('textarea[placeholder*="comment" i]', commentText);
      await page.click('button:has-text("Comment")');

      // Verify comment appears
      await expect(page.locator(`text=${commentText}`)).toBeVisible({ timeout: 5000 });
    }
  });
});

test.describe('Admin Features', () => {

  test.beforeEach(async ({ page }) => {
    // This test requires an admin account
    // You should create an admin account for testing purposes
    await page.goto(`${BASE_URL}/login`);
  });

  test('Admin can access admin panel', async ({ page }) => {
    // Login as admin (you'll need to provide admin credentials)
    // await page.fill('input[name="username"]', 'admin');
    // await page.fill('input[name="password"]', 'adminpassword');
    // await page.click('button[type="submit"]');

    // Navigate to admin panel
    // await page.click('text=Admin Panel');
    // await expect(page).toHaveURL(`${BASE_URL}/admin`);

    // Verify admin dashboard elements
    // await expect(page.locator('text=Dashboard')).toBeVisible();
    // await expect(page.locator('text=User Management')).toBeVisible();

    test.skip('Skipping admin tests - requires admin account setup');
  });
});
