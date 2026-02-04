# Admin Dashboard API Reference

## Base URL
```
http://localhost:8080/api/admin
```

## Authentication
All endpoints require admin authentication. Include the Firebase ID token in the Authorization header:
```
Authorization: Bearer <firebase-id-token>
```

---

## Coordinator Management

### 1. Create Coordinator
Create a new coordinator account.

**Endpoint:** `POST /coordinators`

**Request Body:**
```json
{
  "firstName": "Sarah",
  "lastName": "Johnson",
  "phoneNumber": "+94712345678",
  "email": "sarah@university.edu",
  "department": "Computer Science and Technology",
  "degree": "Master's in Computer Science",
  "shortIntroduction": "Passionate about organizing tech events and fostering student engagement in technology.",
  "password": "SecurePassword123!"
}
```

**Response:** `201 Created`
```json
{
  "id": "coord_abc123",
  "firstName": "Sarah",
  "lastName": "Johnson",
  "phoneNumber": "+94712345678",
  "email": "sarah@university.edu",
  "department": "Computer Science and Technology",
  "degree": "Master's in Computer Science",
  "shortIntroduction": "Passionate about organizing tech events...",
  "active": true,
  "eventCount": 0,
  "createdAt": "2026-02-03T10:30:00"
}
```

---

### 2. Get All Coordinators
Retrieve list of all coordinators with event counts.

**Endpoint:** `GET /coordinators`

**Response:** `200 OK`
```json
[
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
  },
  {
    "id": "coord_def456",
    "firstName": "Michael",
    "lastName": "Chen",
    "email": "michael@university.edu",
    "phoneNumber": "+94723456789",
    "department": "Engineering Technology",
    "degree": "Bachelor's in Engineering",
    "shortIntroduction": "Engineering event specialist...",
    "active": true,
    "eventCount": 3,
    "createdAt": "2026-01-20T14:15:00"
  }
]
```

---

### 3. Get Coordinator by ID
Retrieve a specific coordinator's details.

**Endpoint:** `GET /coordinators/{id}`

**Response:** `200 OK`
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

---

### 4. Update Coordinator Status
Toggle coordinator active/inactive status.

**Endpoint:** `PATCH /coordinators/{id}/status`

**Request Body:**
```json
{
  "active": false
}
```

**Response:** `200 OK`
```json
{
  "id": "coord_abc123",
  "firstName": "Sarah",
  "lastName": "Johnson",
  "active": false,
  "eventCount": 5,
  ...
}
```

---

### 5. Delete Coordinator
Permanently delete a coordinator.

**Endpoint:** `DELETE /coordinators/{id}`

**Response:** `200 OK`
```json
{
  "message": "Coordinator deleted successfully"
}
```

---

### 6. Get Departments
Retrieve list of departments for dropdown.

**Endpoint:** `GET /coordinators/departments`

**Response:** `200 OK`
```json
[
  "Computer Science and Technology",
  "Engineering Technology",
  "Biosystems Technology Honours",
  "Export Agriculture",
  ...
]
```

---

### 7. Get Degree Programmes
Retrieve list of degree programmes for dropdown.

**Endpoint:** `GET /coordinators/degree-programmes`

**Response:** `200 OK`
```json
[
  "Animal Production and Food Technology",
  "Export Agriculture",
  "Computer Science and Technology",
  ...
]
```

---

## Event Management

### 8. Get All Events
Retrieve all events with their status.

**Endpoint:** `GET /events`

**Response:** `200 OK`
```json
[
  {
    "eventId": "event_123",
    "coordinatorId": "coord_abc123",
    "title": "Tech Innovation Summit 2024",
    "venue": "Main Auditorium",
    "dateTime": "2024-06-15T09:00:00Z",
    "description": "Annual technology innovation summit...",
    "organizingDepartment": "Computer Science and Technology",
    "status": "PENDING",
    "category": "Technology",
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
]
```

---

### 9. Approve Event
Approve a pending event.

**Endpoint:** `POST /events/{id}/approve`

**Response:** `200 OK`
```json
{
  "eventId": "event_123",
  "title": "Tech Innovation Summit 2024",
  "status": "APPROVED",
  "updatedAt": "2026-02-03T15:20:00Z",
  ...
}
```

---

### 10. Reject Event
Reject a pending event.

**Endpoint:** `POST /events/{id}/reject`

**Response:** `200 OK`
```json
{
  "eventId": "event_123",
  "title": "Tech Innovation Summit 2024",
  "status": "REJECTED",
  "updatedAt": "2026-02-03T15:20:00Z",
  ...
}
```

---

### 11. Get Pending Events Count
Get the number of events awaiting approval.

**Endpoint:** `GET /events/pending/count`

**Response:** `200 OK`
```json
{
  "count": 8
}
```

---

### 12. Get Event by ID
Retrieve a specific event's details.

**Endpoint:** `GET /events/{id}`

**Response:** `200 OK`

---

### 13. Delete Event
Delete an event.

**Endpoint:** `DELETE /events/{id}`

**Response:** `200 OK`
```json
{
  "message": "Event deleted successfully"
}
```

---

### 14. Filter Events by Category
Filter events by category.

**Endpoint:** `GET /events/filter?category={category}`

**Query Parameters:**
- `category` - Event category (Technology, Career, Culture, Sports, etc.)

**Response:** `200 OK`

---

## Product Management

### 15. Get All Products
Retrieve all products.

**Endpoint:** `GET /products`

**Response:** `200 OK`
```json
[
  {
    "id": "prod_123",
    "name": "University Hoodie - Navy",
    "description": "Official university hoodie in navy blue",
    "price": 45.00,
    "category": "Apparel",
    "imageUrl": "https://...",
    "sellerId": "user_456",
    "sellerName": "Campus Store",
    "status": "PENDING",
    "createdAt": "2026-01-20T10:00:00"
  }
]
```

---

### 16. Approve Product
Approve a pending product.

**Endpoint:** `POST /products/{id}/approve`

**Response:** `200 OK`
```json
{
  "id": "prod_123",
  "name": "University Hoodie - Navy",
  "status": "APPROVED",
  ...
}
```

---

### 17. Disable Product
Disable/delete a product.

**Endpoint:** `POST /products/{id}/disable`

**Response:** `200 OK`
```json
{
  "id": "prod_123",
  "name": "University Hoodie - Navy",
  "status": "DELETED",
  ...
}
```

---

### 18. Get Pending Products Count
Get the number of products awaiting approval.

**Endpoint:** `GET /products/pending/count`

**Response:** `200 OK`
```json
{
  "count": 2
}
```

---

### 19. Get Product by ID
Retrieve a specific product's details.

**Endpoint:** `GET /products/{id}`

**Response:** `200 OK`

---

### 20. Delete Product
Permanently delete a product.

**Endpoint:** `DELETE /products/{id}`

**Response:** `200 OK`
```json
{
  "message": "Product deleted successfully"
}
```

---

## User Management

### 21. Get All Users
Retrieve all registered users.

**Endpoint:** `GET /users`

**Response:** `200 OK`

---

### 22. Get University Students
Retrieve only university student accounts.

**Endpoint:** `GET /users/university-students`

**Response:** `200 OK`

---

### 23. Get External Users
Retrieve only external user accounts.

**Endpoint:** `GET /users/external-users`

**Response:** `200 OK`

---

### 24. Get Pending Verification Users
Retrieve users awaiting ID verification.

**Endpoint:** `GET /users/pending-verification`

**Response:** `200 OK`
```json
[
  {
    "id": "user_789",
    "firstName": "Alice",
    "lastName": "Brown",
    "email": "alice@university.edu",
    "userType": "UNIVERSITY_STUDENT",
    "verificationStatus": "PENDING",
    "registeredAt": "2026-02-01T08:30:00"
  }
]
```

---

### 25. Get User Statistics
Get count of users by type and verification status.

**Endpoint:** `GET /users/stats`

**Response:** `200 OK`
```json
{
  "totalUniversityStudents": 385,
  "totalExternalUsers": 135,
  "totalPendingVerification": 12
}
```

---

### 26. Update User Status
Update user active/inactive status.

**Endpoint:** `PATCH /users/{id}/status`

**Request Body:**
```json
{
  "active": false
}
```

**Response:** `200 OK`

---

### 27. Verify Student ID
Approve or reject student ID verification.

**Endpoint:** `PATCH /users/{id}/verify`

**Request Body:**
```json
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

**Response:** `200 OK`

---

### 28. Delete User
Delete a user account.

**Endpoint:** `DELETE /users/{id}`

**Response:** `200 OK`
```json
{
  "message": "User deleted successfully"
}
```

---

## Dashboard Statistics

### 29. Get Dashboard Stats
Retrieve overview statistics for the admin dashboard.

**Endpoint:** `GET /dashboard/stats`

**Response:** `200 OK`
```json
{
  "totalEvents": 148,
  "eventsPercentageChange": 12.5,
  "activeUsers": 3427,
  "usersPercentageChange": -3.2,
  "totalProducts": 156,
  "productsPercentageChange": 8.3,
  "productsSold": 234,
  "productsSoldIsNew": true,
  "recentEvents": [
    {
      "eventId": "event_123",
      "title": "Tech Summit 2024",
      "status": "PENDING",
      "createdAt": "2026-01-15T10:30:00Z"
    }
  ],
  "topCoordinators": [
    {
      "id": "coord_abc123",
      "name": "John Doe",
      "eventCount": 45
    }
  ]
}
```

---

## Payment Management

### 30. Get Payment Statistics
Retrieve payment and transaction statistics.

**Endpoint:** `GET /payments/stats`

**Response:** `200 OK`

---

### 31. Get All Transactions
Retrieve all payment transactions.

**Endpoint:** `GET /payments/transactions`

**Response:** `200 OK`

---

### 32. Get Recent Transactions
Retrieve recent payment transactions.

**Endpoint:** `GET /payments/transactions/recent?limit={limit}`

**Query Parameters:**
- `limit` - Number of transactions to return (default: 10)

**Response:** `200 OK`

---

## Status Codes

- `200 OK` - Request successful
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Missing or invalid authentication
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---

## Error Response Format

```json
{
  "error": "Error message description",
  "status": 400,
  "timestamp": "2026-02-03T15:30:00Z"
}
```

---

## Notes

1. All timestamps are in ISO 8601 format (UTC)
2. All endpoints require admin-level authentication
3. Product status values: `PENDING`, `APPROVED`, `AVAILABLE`, `SOLD`, `DELETED`
4. Event status values: `DRAFT`, `PENDING`, `APPROVED`, `REJECTED`, `PUBLISHED`, `COMPLETED`, `CANCELLED`
5. User verification status: `PENDING`, `VERIFIED`, `REJECTED`
