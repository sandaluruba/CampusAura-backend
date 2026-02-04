# Postman Testing Guide - Admin Dashboard API

## Prerequisites

1. **Install Postman**: Download from [postman.com](https://www.postman.com/downloads/)
2. **Start Your Backend**: Run your Spring Boot application (should be running on `http://localhost:8080`)
3. **Firebase Project**: Have your Firebase project ready with Authentication enabled

---

## Step 1: Get Firebase Authentication Token

### Option A: Using Firebase Console (Easiest)

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Go to **Authentication** → **Users**
4. Create a test user or use existing user
5. Note down the **UID** of the user

### Option B: Using Your React App

1. Login to your React application
2. Open browser **Developer Tools** (F12)
3. Go to **Console** tab
4. Run this command:
```javascript
firebase.auth().currentUser.getIdToken().then(token => console.log(token));
```
5. Copy the token that appears in console (it's a long string)

### Option C: Using Firebase REST API

**Create a test endpoint in your Spring Boot app** (temporary for testing):

```java
// Add this to AdminController.java temporarily
@PostMapping("/test/login")
public ResponseEntity<Map<String, String>> testLogin(@RequestBody Map<String, String> credentials) {
    // This is just for testing - don't use in production
    Map<String, String> response = new HashMap<>();
    response.put("message", "Use Firebase Authentication to get token");
    return ResponseEntity.ok(response);
}
```

---

## Step 2: Set Admin Custom Claims (IMPORTANT!)

Before testing admin endpoints, you need to set the user as admin.

### Create a Node.js Script

1. Create a file `set-admin.js`:

```javascript
const admin = require('firebase-admin');

// Initialize Firebase Admin with your service account
const serviceAccount = require('./path-to-your-firebase-key.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

// Replace with your user's UID
const userId = 'YOUR_USER_UID_HERE';

admin.auth().setCustomUserClaims(userId, { 
  admin: true, 
  role: 'admin' 
})
.then(() => {
  console.log('✅ Admin role set successfully for user:', userId);
  process.exit(0);
})
.catch(error => {
  console.error('❌ Error setting admin role:', error);
  process.exit(1);
});
```

2. Install Firebase Admin SDK:
```bash
npm install firebase-admin
```

3. Run the script:
```bash
node set-admin.js
```

4. **Important**: After setting custom claims, the user must **logout and login again** to get a new token with admin claims.

---

## Step 3: Setup Postman

### Create a New Collection

1. Open Postman
2. Click **New** → **Collection**
3. Name it: **"CampusAura Admin API"**
4. Click **Create**

### Set Collection Variables

1. Click on your collection
2. Go to **Variables** tab
3. Add these variables:

| Variable Name | Initial Value | Current Value |
|--------------|---------------|---------------|
| `base_url` | `http://localhost:8080/api/admin` | `http://localhost:8080/api/admin` |
| `token` | (leave empty) | (paste your Firebase token here) |

4. Click **Save**

---

## Step 4: Configure Authorization for All Requests

1. Click on your collection name
2. Go to **Authorization** tab
3. Select **Type**: Bearer Token
4. In **Token** field, enter: `{{token}}`
5. Click **Save**

This will automatically add the token to all requests in this collection!

---

## Step 5: Test Each Endpoint

### Test 1: Dashboard Statistics

**Purpose**: Get overview statistics

1. Click **Add Request**
2. Name: `Get Dashboard Stats`
3. Method: **GET**
4. URL: `{{base_url}}/dashboard/stats`
5. Click **Send**

**Expected Response** (200 OK):
```json
{
  "totalEvents": 25,
  "totalUsers": 150,
  "totalProducts": 45,
  "productsSold": 12,
  "recentEvents": [
    {
      "eventId": "event123",
      "title": "Tech Conference 2026",
      "venue": "Main Hall",
      "dateTime": "2026-02-15T10:00:00",
      "status": "PUBLISHED"
    }
  ]
}
```

**If you get an error:**
- ❌ 401 Unauthorized → Token is invalid or expired
- ❌ 403 Forbidden → User doesn't have admin role
- ❌ 500 Internal Server Error → Check backend logs

---

### Test 2: Get Degree Programmes

**Purpose**: Get the list for coordinator registration form

1. Add Request
2. Name: `Get Degree Programmes`
3. Method: **GET**
4. URL: `{{base_url}}/coordinators/degree-programmes`
5. Click **Send**

**Expected Response** (200 OK):
```json
[
  "Animal Production and Food Technology",
  "Export Agriculture",
  "Aquatic Resources Technology",
  "Tea Technology and Value Addition",
  "Computer Science and Technology",
  ...
]
```

---

### Test 3: Register a Coordinator

**Purpose**: Create a new coordinator

1. Add Request
2. Name: `Register Coordinator`
3. Method: **POST**
4. URL: `{{base_url}}/coordinators`
5. Go to **Body** tab
6. Select **raw** and **JSON**
7. Enter this JSON:

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+94771234567",
  "email": "john.doe@test.com",
  "degreeProgramme": "Computer Science and Technology"
}
```

8. Click **Send**

**Expected Response** (201 Created):
```json
{
  "id": "generated-id-123",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+94771234567",
  "email": "john.doe@test.com",
  "degreeProgramme": "Computer Science and Technology",
  "active": true,
  "createdAt": "2026-01-28T10:30:00"
}
```

**Save the `id` value** - you'll need it for next tests!

---

### Test 4: Get All Coordinators

**Purpose**: List all registered coordinators

1. Add Request
2. Name: `Get All Coordinators`
3. Method: **GET**
4. URL: `{{base_url}}/coordinators`
5. Click **Send**

**Expected Response** (200 OK):
```json
[
  {
    "id": "coord123",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@test.com",
    "phoneNumber": "+94771234567",
    "degreeProgramme": "Computer Science and Technology",
    "active": true,
    "createdAt": "2026-01-28T10:30:00"
  }
]
```

---

### Test 5: Update Coordinator Status

**Purpose**: Activate or deactivate a coordinator

1. Add Request
2. Name: `Update Coordinator Status`
3. Method: **PATCH**
4. URL: `{{base_url}}/coordinators/YOUR_COORDINATOR_ID/status`
   - Replace `YOUR_COORDINATOR_ID` with the ID from Test 3
5. Go to **Body** tab
6. Select **raw** and **JSON**
7. Enter:

```json
{
  "active": false
}
```

8. Click **Send**

**Expected Response** (200 OK):
```json
{
  "id": "coord123",
  "firstName": "John",
  "lastName": "Doe",
  "active": false,
  ...
}
```

---

### Test 6: Get All Events

**Purpose**: View all events in the system

1. Add Request
2. Name: `Get All Events`
3. Method: **GET**
4. URL: `{{base_url}}/events`
5. Click **Send**

**Expected Response** (200 OK):
```json
[
  {
    "eventId": "event123",
    "title": "Tech Conference 2026",
    "venue": "Main Hall",
    "dateTime": "2026-02-15T10:00:00",
    "description": "Annual tech conference",
    "status": "PUBLISHED"
  }
]
```

---

### Test 7: Filter Events by Category

**Purpose**: Filter events by type

1. Add Request
2. Name: `Filter Events by Category`
3. Method: **GET**
4. URL: `{{base_url}}/events/filter?category=technology`
5. Try different categories: `technology`, `career`, `culture`, `sports`
6. Click **Send**

---

### Test 8: Delete Event

**Purpose**: Remove an event

1. Add Request
2. Name: `Delete Event`
3. Method: **DELETE**
4. URL: `{{base_url}}/events/YOUR_EVENT_ID`
   - Replace `YOUR_EVENT_ID` with an actual event ID
5. Click **Send**

**Expected Response** (200 OK):
```json
{
  "message": "Event deleted successfully"
}
```

---

### Test 9: Get User Statistics

**Purpose**: View user counts and statistics

1. Add Request
2. Name: `Get User Stats`
3. Method: **GET**
4. URL: `{{base_url}}/users/stats`
5. Click **Send**

**Expected Response** (200 OK):
```json
{
  "totalUniversityStudents": 120,
  "totalExternalUsers": 30,
  "totalPendingVerification": 5
}
```

---

### Test 10: Get Pending Verification Users

**Purpose**: View users waiting for verification

1. Add Request
2. Name: `Get Pending Verification`
3. Method: **GET**
4. URL: `{{base_url}}/users/pending-verification`
5. Click **Send**

**Expected Response** (200 OK):
```json
[
  {
    "id": "user123",
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane@example.com",
    "userType": "UNIVERSITY_STUDENT",
    "verificationStatus": "PENDING",
    "idImageUrl": "https://storage.url/id-image.jpg",
    "studentId": "ST2023001"
  }
]
```

---

### Test 11: Verify a Student

**Purpose**: Approve or reject student verification

1. Add Request
2. Name: `Verify Student`
3. Method: **PATCH**
4. URL: `{{base_url}}/users/USER_ID/verify`
   - Replace `USER_ID` with actual user ID
5. Go to **Body** tab
6. Select **raw** and **JSON**
7. To **approve**:

```json
{
  "status": "VERIFIED"
}
```

Or to **reject**:

```json
{
  "status": "REJECTED"
}
```

8. Click **Send**

---

### Test 12: Update User Status

**Purpose**: Activate or deactivate user account

1. Add Request
2. Name: `Update User Status`
3. Method: **PATCH**
4. URL: `{{base_url}}/users/USER_ID/status`
5. Body:

```json
{
  "active": false
}
```

6. Click **Send**

---

### Test 13: Get All Products

**Purpose**: View marketplace products

1. Add Request
2. Name: `Get All Products`
3. Method: **GET**
4. URL: `{{base_url}}/products`
5. Click **Send**

**Expected Response** (200 OK):
```json
[
  {
    "id": "prod123",
    "name": "Textbook - Data Structures",
    "description": "Used textbook in good condition",
    "price": 1500.00,
    "category": "Books",
    "imageUrl": "https://storage.url/product.jpg",
    "status": "AVAILABLE"
  }
]
```

---

### Test 14: Delete Product

**Purpose**: Remove a product from marketplace

1. Add Request
2. Name: `Delete Product`
3. Method: **DELETE**
4. URL: `{{base_url}}/products/PRODUCT_ID`
5. Click **Send**

**Expected Response** (200 OK):
```json
{
  "message": "Product deleted successfully"
}
```

---

### Test 15: Get Payment Statistics

**Purpose**: View revenue and transactions

1. Add Request
2. Name: `Get Payment Stats`
3. Method: **GET**
4. URL: `{{base_url}}/payments/stats`
5. Click **Send**

**Expected Response** (200 OK):
```json
{
  "ticketRevenue": 125000.00,
  "marketplaceRevenue": 45000.00,
  "totalRevenue": 170000.00,
  "recentTransactions": [
    {
      "id": "trans123",
      "type": "TICKET",
      "userName": "Jane Smith",
      "amount": 5000.00,
      "status": "COMPLETED"
    }
  ]
}
```

---

### Test 16: Get Recent Transactions

**Purpose**: View latest transactions

1. Add Request
2. Name: `Get Recent Transactions`
3. Method: **GET**
4. URL: `{{base_url}}/payments/transactions/recent?limit=10`
5. Change `limit` parameter to get different numbers
6. Click **Send**

---

## Step 6: Save and Export Collection

### Save Your Collection

1. Click on collection name
2. Click the **three dots** (...)
3. Select **Export**
4. Choose **Collection v2.1** format
5. Save the JSON file

### Share with Team

You can import this collection file in any Postman instance!

---

## Troubleshooting Guide

### Problem 1: 401 Unauthorized Error

**Error Response:**
```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired token"
}
```

**Solutions:**
1. ✅ Get a fresh Firebase token (tokens expire after 1 hour)
2. ✅ Update the `token` variable in Postman
3. ✅ Make sure user is logged in to Firebase
4. ✅ Check if Authorization header is being sent

**How to check in Postman:**
- Go to request **Headers** tab
- You should see: `Authorization: Bearer your-token-here`

---

### Problem 2: 403 Forbidden Error

**Error Response:**
```json
{
  "error": "Forbidden",
  "message": "Access denied"
}
```

**Solutions:**
1. ✅ Verify admin custom claims are set
2. ✅ Run the `set-admin.js` script again
3. ✅ User must logout and login again after setting claims
4. ✅ Get a new token after setting admin role

**Verify claims in token:**
```javascript
// In browser console after login
firebase.auth().currentUser.getIdTokenResult()
  .then(result => console.log(result.claims));
```

You should see:
```javascript
{
  admin: true,
  role: "admin"
}
```

---

### Problem 3: 404 Not Found

**Error:**
```
Cannot GET /api/admin/dashboard/stats
```

**Solutions:**
1. ✅ Check if backend is running on port 8080
2. ✅ Verify the URL is correct
3. ✅ Check for typos in endpoint path
4. ✅ Make sure you're using `{{base_url}}` variable

---

### Problem 4: 500 Internal Server Error

**Solutions:**
1. ✅ Check backend console logs for error details
2. ✅ Verify Firestore is initialized correctly
3. ✅ Check if Firebase service account key is loaded
4. ✅ Verify collections exist in Firestore

---

### Problem 5: CORS Error

**Error in browser console:**
```
Access to fetch blocked by CORS policy
```

**Solution:**
- CORS errors don't affect Postman
- If testing from browser, check `SecurityConfig.java`
- Add your frontend URL to allowed origins

---

## Quick Reference: Common Status Codes

| Code | Meaning | Common Cause |
|------|---------|--------------|
| 200 | OK | Request successful |
| 201 | Created | Resource created successfully |
| 400 | Bad Request | Invalid request data |
| 401 | Unauthorized | Missing or invalid token |
| 403 | Forbidden | No admin permissions |
| 404 | Not Found | Resource doesn't exist |
| 500 | Internal Server Error | Backend error, check logs |

---

## Testing Checklist

Use this checklist to verify all endpoints work:

### Dashboard
- [ ] Get dashboard statistics

### Coordinators
- [ ] Get degree programmes list
- [ ] Register new coordinator
- [ ] Get all coordinators
- [ ] Get coordinator by ID
- [ ] Update coordinator status
- [ ] Delete coordinator

### Events
- [ ] Get all events
- [ ] Get event by ID
- [ ] Filter events by category
- [ ] Delete event

### Users
- [ ] Get all users
- [ ] Get university students
- [ ] Get external users
- [ ] Get pending verification
- [ ] Get user statistics
- [ ] Update user status
- [ ] Verify student
- [ ] Delete user

### Products
- [ ] Get all products
- [ ] Get product by ID
- [ ] Delete product

### Payments
- [ ] Get payment statistics
- [ ] Get all transactions
- [ ] Get recent transactions

---

## Sample Postman Collection JSON

Here's a ready-to-import Postman collection:

```json
{
  "info": {
    "name": "CampusAura Admin API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "base_url",
      "value": "http://localhost:8080/api/admin"
    },
    {
      "key": "token",
      "value": "YOUR_FIREBASE_TOKEN_HERE"
    }
  ],
  "auth": {
    "type": "bearer",
    "bearer": [
      {
        "key": "token",
        "value": "{{token}}",
        "type": "string"
      }
    ]
  },
  "item": [
    {
      "name": "Dashboard",
      "item": [
        {
          "name": "Get Dashboard Stats",
          "request": {
            "method": "GET",
            "url": "{{base_url}}/dashboard/stats"
          }
        }
      ]
    },
    {
      "name": "Coordinators",
      "item": [
        {
          "name": "Get Degree Programmes",
          "request": {
            "method": "GET",
            "url": "{{base_url}}/coordinators/degree-programmes"
          }
        },
        {
          "name": "Register Coordinator",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"firstName\": \"John\",\n  \"lastName\": \"Doe\",\n  \"phoneNumber\": \"+94771234567\",\n  \"email\": \"john.doe@test.com\",\n  \"degreeProgramme\": \"Computer Science and Technology\"\n}"
            },
            "url": "{{base_url}}/coordinators"
          }
        },
        {
          "name": "Get All Coordinators",
          "request": {
            "method": "GET",
            "url": "{{base_url}}/coordinators"
          }
        }
      ]
    }
  ]
}
```

Save this as `CampusAura-Admin-API.postman_collection.json` and import into Postman!

---

## Pro Tips

1. **Use Postman Environment Variables** for different environments (dev, staging, production)

2. **Create Tests** to automatically verify responses:
```javascript
// In Postman Tests tab
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has totalEvents", function () {
    pm.expect(pm.response.json()).to.have.property('totalEvents');
});
```

3. **Use Collection Runner** to test all endpoints at once

4. **Save Responses** as examples for documentation

5. **Use Pre-request Scripts** to automatically refresh tokens

---

## Next Steps

1. ✅ Test all endpoints using this guide
2. ✅ Document any issues you find
3. ✅ Share the collection with your frontend team
4. ✅ Create automated tests using Postman Collection Runner
5. ✅ Set up monitoring for production endpoints

Happy Testing! 🚀
