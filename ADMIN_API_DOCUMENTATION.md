# Admin Dashboard API Documentation

## Base URL
```
http://localhost:8080/api/admin
```

## Authentication
All endpoints require:
- Firebase Authentication token in the Authorization header
- Admin role assigned in Firebase custom claims
```
Authorization: Bearer <firebase-id-token>
```

## API Endpoints

### 1. Dashboard Section

#### Get Dashboard Statistics
Get total events, users, products, products sold, and 5 recent events.

**Endpoint:** `GET /dashboard/stats`

**Response:**
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
      "status": "PUBLISHED",
      "organizingDepartment": "Computer Science"
    }
  ]
}
```

---

### 2. Coordinator Management Section

#### Get Degree Programmes (for dropdown)
**Endpoint:** `GET /coordinators/degree-programmes`

**Response:**
```json
[
  "Animal Production and Food Technology",
  "Export Agriculture",
  "Computer Science and Technology",
  "Industrial Information Technology",
  ...
]
```

#### Register New Coordinator
**Endpoint:** `POST /coordinators`

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+94771234567",
  "email": "john.doe@example.com",
  "degreeProgramme": "Computer Science and Technology"
}
```

**Response:** (201 Created)
```json
{
  "id": "coord123",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+94771234567",
  "email": "john.doe@example.com",
  "degreeProgramme": "Computer Science and Technology",
  "active": true,
  "createdAt": "2026-01-28T10:30:00"
}
```

#### Get All Coordinators
**Endpoint:** `GET /coordinators`

**Response:**
```json
[
  {
    "id": "coord123",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "+94771234567",
    "degreeProgramme": "Computer Science and Technology",
    "active": true,
    "createdAt": "2026-01-28T10:30:00"
  }
]
```

#### Get Coordinator by ID
**Endpoint:** `GET /coordinators/{id}`

#### Update Coordinator Status (Active/Inactive)
**Endpoint:** `PATCH /coordinators/{id}/status`

**Request Body:**
```json
{
  "active": false
}
```

#### Delete Coordinator
**Endpoint:** `DELETE /coordinators/{id}`

**Response:**
```json
{
  "message": "Coordinator deleted successfully"
}
```

---

### 3. Event Management Section

#### Get All Events
**Endpoint:** `GET /events`

**Response:**
```json
[
  {
    "eventId": "event123",
    "title": "Tech Conference 2026",
    "venue": "Main Hall",
    "dateTime": "2026-02-15T10:00:00",
    "ticketsAvailable": true,
    "description": "Annual tech conference",
    "organizingDepartment": "Computer Science",
    "status": "PUBLISHED",
    "createdAt": "2026-01-20T08:00:00",
    "updatedAt": "2026-01-28T10:30:00"
  }
]
```

#### Get Event by ID
**Endpoint:** `GET /events/{id}`

#### Delete Event
**Endpoint:** `DELETE /events/{id}`

**Response:**
```json
{
  "message": "Event deleted successfully"
}
```

#### Filter Events by Category
**Endpoint:** `GET /events/filter?category={category}`

**Query Parameters:**
- `category`: technology, career, culture, sports

**Example:** `GET /events/filter?category=technology`

---

### 4. User Management Section

#### Get All Users
**Endpoint:** `GET /users`

**Response:**
```json
[
  {
    "id": "user123",
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane@example.com",
    "phoneNumber": "+94771234567",
    "userType": "UNIVERSITY_STUDENT",
    "verificationStatus": "VERIFIED",
    "idImageUrl": "https://storage.url/id-image.jpg",
    "studentId": "ST2023001",
    "active": true,
    "createdAt": "2026-01-15T09:00:00"
  }
]
```

#### Get University Students
**Endpoint:** `GET /users/university-students`

#### Get External Users
**Endpoint:** `GET /users/external-users`

#### Get Pending Verification Users
**Endpoint:** `GET /users/pending-verification`

Returns users with `verificationStatus: "PENDING"`

#### Get User Statistics
**Endpoint:** `GET /users/stats`

**Response:**
```json
{
  "totalUniversityStudents": 120,
  "totalExternalUsers": 30,
  "totalPendingVerification": 5
}
```

#### Update User Status (Active/Inactive)
**Endpoint:** `PATCH /users/{id}/status`

**Request Body:**
```json
{
  "active": false
}
```

#### Verify Student (Approve/Reject)
**Endpoint:** `PATCH /users/{id}/verify`

**Request Body:**
```json
{
  "status": "VERIFIED"
}
```

**Status Options:**
- `VERIFIED` - Approve verification
- `REJECTED` - Reject verification
- `PENDING` - Reset to pending

#### Delete User
**Endpoint:** `DELETE /users/{id}`

**Response:**
```json
{
  "message": "User deleted successfully"
}
```

---

### 5. Product Management Section

#### Get All Products
**Endpoint:** `GET /products`

**Response:**
```json
[
  {
    "id": "prod123",
    "name": "Textbook - Data Structures",
    "description": "Used textbook in good condition",
    "price": 1500.00,
    "category": "Books",
    "imageUrl": "https://storage.url/product.jpg",
    "sellerId": "user123",
    "sellerName": "John Doe",
    "status": "AVAILABLE",
    "createdAt": "2026-01-20T10:00:00",
    "soldAt": null
  }
]
```

**Product Status:**
- `AVAILABLE` - Product is available for sale
- `SOLD` - Product has been sold
- `DELETED` - Product has been deleted

#### Get Product by ID
**Endpoint:** `GET /products/{id}`

#### Delete Product
**Endpoint:** `DELETE /products/{id}`

Soft deletes the product (sets status to DELETED)

**Response:**
```json
{
  "message": "Product deleted successfully"
}
```

---

### 6. Payment Section

#### Get Payment Statistics
**Endpoint:** `GET /payments/stats`

**Response:**
```json
{
  "ticketRevenue": 125000.00,
  "marketplaceRevenue": 45000.00,
  "totalRevenue": 170000.00,
  "recentTransactions": [
    {
      "id": "trans123",
      "type": "TICKET",
      "userId": "user123",
      "userName": "Jane Smith",
      "eventId": "event123",
      "eventName": "Tech Conference 2026",
      "amount": 5000.00,
      "paymentMethod": "Card",
      "status": "COMPLETED",
      "createdAt": "2026-01-28T14:30:00",
      "completedAt": "2026-01-28T14:30:15"
    }
  ]
}
```

#### Get All Transactions
**Endpoint:** `GET /payments/transactions`

**Response:**
```json
[
  {
    "id": "trans123",
    "type": "TICKET",
    "userId": "user123",
    "userName": "Jane Smith",
    "eventId": "event123",
    "eventName": "Tech Conference 2026",
    "productId": null,
    "productName": null,
    "amount": 5000.00,
    "paymentMethod": "Card",
    "status": "COMPLETED",
    "createdAt": "2026-01-28T14:30:00",
    "completedAt": "2026-01-28T14:30:15"
  }
]
```

**Transaction Types:**
- `TICKET` - Event ticket purchase
- `MARKETPLACE` - Marketplace product purchase

**Transaction Status:**
- `PENDING` - Payment pending
- `COMPLETED` - Payment completed
- `FAILED` - Payment failed
- `REFUNDED` - Payment refunded

#### Get Recent Transactions
**Endpoint:** `GET /payments/transactions/recent?limit={limit}`

**Query Parameters:**
- `limit` (optional, default: 10): Number of transactions to return

**Example:** `GET /payments/transactions/recent?limit=5`

---

## Error Responses

### 401 Unauthorized
```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired token"
}
```

### 403 Forbidden
```json
{
  "error": "Forbidden",
  "message": "Access denied. Admin role required."
}
```

### 404 Not Found
```json
{
  "error": "Not Found",
  "message": "Resource not found"
}
```

### 500 Internal Server Error
```json
{
  "error": "Internal Server Error",
  "message": "An error occurred"
}
```

---

## Setting Admin Role in Firebase

To set a user as admin in Firebase, use Firebase Admin SDK to set custom claims:

```javascript
// Using Firebase Admin SDK (Node.js)
admin.auth().setCustomUserClaims(uid, { admin: true, role: 'admin' });
```

Or using Firebase Console:
1. Go to Firebase Console → Authentication
2. Select the user
3. Set custom claims in Cloud Functions or using Firebase Admin SDK

---

## CORS Configuration

The API allows requests from:
- `http://localhost:3000` (React development)
- `http://localhost:4200` (Angular development)

Allowed methods: GET, POST, PUT, DELETE, PATCH, OPTIONS

---

## Notes for Frontend Development

1. **Authentication**: Always include the Firebase ID token in the Authorization header
2. **Admin Role**: Ensure users have `admin: true` in Firebase custom claims
3. **Error Handling**: Handle 401 (Unauthorized) and 403 (Forbidden) responses appropriately
4. **Date Formats**: All dates are in ISO 8601 format (e.g., "2026-01-28T10:30:00")
5. **Pagination**: Currently not implemented. Consider client-side pagination for large datasets.
6. **File Uploads**: For ID verification images and product images, use Firebase Storage and store the URL in the database.

---

## Example React Hook for API Calls

```javascript
import { getAuth } from 'firebase/auth';

const useAdminAPI = () => {
  const getAuthToken = async () => {
    const auth = getAuth();
    const user = auth.currentUser;
    if (!user) throw new Error('Not authenticated');
    return await user.getIdToken();
  };

  const getDashboardStats = async () => {
    const token = await getAuthToken();
    const response = await fetch('http://localhost:8080/api/admin/dashboard/stats', {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    if (!response.ok) throw new Error('Failed to fetch dashboard stats');
    return await response.json();
  };

  // Add more API methods as needed...

  return { getDashboardStats };
};
```
