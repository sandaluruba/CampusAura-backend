# Admin Dashboard Endpoints

Base URL: `http://localhost:8080/api/admin`

## User Role & Profile Endpoints
```
GET    /api/user/{uid}                           Get user by UID (includes role)
GET    /api/user/profile                         Get authenticated user's profile
GET    /api/user/me                              Get full authenticated user details
```

## Dashboard Overview
```
GET    /dashboard/stats                          Get dashboard statistics
```

## Coordinator Management
```
POST   /coordinators                             Create new coordinator
GET    /coordinators                             Get all coordinators with event counts
GET    /coordinators/{id}                        Get coordinator by ID
PATCH  /coordinators/{id}/status                 Update coordinator status (active/inactive)
DELETE /coordinators/{id}                        Delete coordinator
GET    /coordinators/departments                 Get department list for dropdown
GET    /coordinators/degree-programmes           Get degree programmes for dropdown
```

## Event Management
```
GET    /events                                   Get all events
GET    /events/{id}                              Get event by ID
DELETE /events/{id}                              Delete event
GET    /events/filter?category={category}        Filter events by category
POST   /events/{id}/approve                      Approve pending event
POST   /events/{id}/reject                       Reject pending event
GET    /events/pending/count                     Get count of pending events
```

## User Management
```
GET    /users                                    Get all users
GET    /users/university-students                Get university students only
GET    /users/external-users                     Get external users only
GET    /users/pending-verification               Get users pending ID verification
GET    /users/stats                              Get user statistics (counts by type)
PATCH  /users/{id}/status                        Update user status (active/inactive)
PATCH  /users/{id}/verify                        Verify student ID (approve/reject)
DELETE /users/{id}                               Delete user
```

## Product Management
```
GET    /products                                 Get all products
GET    /products/{id}                            Get product by ID
DELETE /products/{id}                            Delete product
POST   /products/{id}/approve                    Approve pending product
POST   /products/{id}/disable                    Disable/delete product
GET    /products/pending/count                   Get count of pending products
```

## Payment/Transaction Management
```
GET    /payments/stats                           Get payment statistics
GET    /payments/transactions                    Get all transactions
GET    /payments/transactions/recent?limit={n}   Get recent transactions (default limit: 10)
```

---

## Quick Reference by Feature

### Dashboard Statistics
- Total Events: From `/dashboard/stats`
- Active Users: From `/dashboard/stats`
- Total Products: From `/dashboard/stats`
- Products Sold: From `/dashboard/stats`
- Recent Events: From `/dashboard/stats`
- Top Coordinators: From `/dashboard/stats`

### Pending Approvals
- Pending Events Count: `/events/pending/count`
- Pending Products Count: `/products/pending/count`
- Pending User Verifications: `/users/pending-verification`

### Status Updates
- Coordinator Status: `PATCH /coordinators/{id}/status`
- User Status: `PATCH /users/{id}/status`
- Event Approval: `POST /events/{id}/approve`
- Event Rejection: `POST /events/{id}/reject`
- Product Approval: `POST /products/{id}/approve`
- Product Disable: `POST /products/{id}/disable`
- User Verification: `PATCH /users/{id}/verify`

---

## Request Body Examples

### Create Coordinator
```json
POST /coordinators
{
  "firstName": "Sarah",
  "lastName": "Johnson",
  "phoneNumber": "+94712345678",
  "email": "sarah@university.edu",
  "department": "Computer Science and Technology",
  "degree": "Master's in Computer Science",
  "shortIntroduction": "Passionate about tech events...",
  "password": "SecurePassword123!"
}
```

### Update Status (Coordinator/User)
```json
PATCH /coordinators/{id}/status
{
  "active": false
}
```

### Verify Student
```json
PATCH /users/{id}/verify
{
  "status": "VERIFIED"
}
```
or
```json
{
  "status": "REJECTED"
}
```

---

## Response Examples

### Get User by UID (with role)
```json
GET /api/user/{uid}
{
  "uid": "firebase_uid_123",
  "email": "john.doe@std.uwu.ac.lk",
  "name": "John Doe",
  "role": "STUDENT",
  "verified": true
}
```

### Coordinator with Event Count
```json
{
  "id": "coord_abc123",
  "firstName": "Sarah",
  "lastName": "Johnson",
  "email": "sarah@university.edu",
  "phoneNumber": "+94712345678",
  "department": "Computer Science and Technology",
  "degree": "Master's in Computer Science",
  "shortIntroduction": "Passionate about tech events...",
  "active": true,
  "eventCount": 5,
  "createdAt": "2026-01-15T10:30:00"
}
```

### Dashboard Stats
```json
{
  "totalEvents": 148,
  "activeUsers": 3427,
  "totalProducts": 156,
  "productsSold": 234,
  "recentEvents": [...],
  "topCoordinators": [...]
}
```

### Pending Counts
```json
{
  "count": 8
}
```

### User Stats
```json
{
  "totalUniversityStudents": 385,
  "totalExternalUsers": 135,
  "totalPendingVerification": 12
}
```

---

## Authentication & Security

### Firebase Token Verification
All authenticated endpoints verify Firebase ID tokens using `FirebaseAuth.getInstance().verifyIdToken(token)`.

### Authentication Requirements

**Public Endpoints (No Auth Required):**
- `GET /api/public/**`
- `GET /api/events/landing-page`
- `GET /api/events/latest`
- `GET /api/events/public/**`
- `POST /api/auth/validate-email`
- `POST /api/auth/validate-registration`
- `GET /api/auth/registration-info`

**Authenticated Endpoints:**
- `GET /api/user/{uid}` - Requires valid Firebase token
- `GET /api/user/profile` - Requires valid Firebase token
- `GET /api/user/me` - Requires valid Firebase token

**Admin Only Endpoints:**
- All `/api/admin/**` endpoints require `ROLE_ADMIN`

**Coordinator Endpoints:**
- All `/api/coordinator/**` endpoints require `ROLE_ADMIN` or `ROLE_COORDINATOR`

### How to Include Authentication
Include Firebase ID token in the Authorization header:
```
Authorization: Bearer <firebase-id-token>
```

### Example Request with Authentication
```bash
curl -X GET http://localhost:8080/api/user/firebase_uid_123 \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6..."
```

### Token Validation Process
1. Extract token from `Authorization: Bearer <token>` header
2. Verify token with Firebase: `FirebaseAuth.getInstance().verifyIdToken(token)`
3. Sync user to Firestore (create if new user, fetch if existing)
4. Set Spring Security authentication with user's role
5. Allow request to proceed if authorized

### Error Responses
**401 Unauthorized** - Missing, invalid, or expired token:
```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired token"
}
```

**403 Forbidden** - Valid token but insufficient permissions:
```json
{
  "error": "Forbidden",
  "message": "Access denied"
}
```

---

## HTTP Status Codes
- `200` - Success
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `500` - Server Error
