# Admin Dashboard Setup Guide

## Overview
This backend provides a comprehensive admin dashboard for the CampusAura application. Admins can manage coordinators, events, users, products, and view analytics.

## Features Implemented

### 1. Dashboard Section
- View total events, users, products, and products sold
- Display 5 most recent events
- Real-time statistics

### 2. Manage Coordinators
- Register new coordinators with form validation
- View list of all coordinators
- Activate/deactivate coordinator accounts
- Delete coordinators
- Degree programme dropdown with 15 programmes

### 3. Event Management
- View all events
- View individual event details
- Delete events
- Filter events by category (technology, career, culture, sports)
- Admin has all coordinator access

### 4. User Management
- View all users (university students and external users separately)
- Statistics: total university students, external users, pending verifications
- Activate/deactivate user accounts
- Delete users
- Verify university students by reviewing uploaded ID images
- Filter users by type

### 5. Product Management
- View all marketplace products
- View product details
- Delete products (soft delete)

### 6. Payment Section
- View ticket revenue and marketplace revenue
- View total revenue
- Display recent transaction details
- Filter transactions by type

## Database Collections

The following Firestore collections are used:

### coordinators
```
{
  id: string,
  firstName: string,
  lastName: string,
  phoneNumber: string,
  email: string,
  degreeProgramme: string,
  active: boolean,
  createdAt: timestamp,
  updatedAt: timestamp
}
```

### users
```
{
  id: string,
  firstName: string,
  lastName: string,
  email: string,
  phoneNumber: string,
  userType: "UNIVERSITY_STUDENT" | "EXTERNAL_USER",
  verificationStatus: "PENDING" | "VERIFIED" | "REJECTED",
  idImageUrl: string,
  studentId: string,
  active: boolean,
  createdAt: timestamp,
  updatedAt: timestamp
}
```

### products
```
{
  id: string,
  name: string,
  description: string,
  price: number,
  category: string,
  imageUrl: string,
  sellerId: string,
  sellerName: string,
  status: "AVAILABLE" | "SOLD" | "DELETED",
  createdAt: timestamp,
  updatedAt: timestamp,
  soldAt: timestamp
}
```

### transactions
```
{
  id: string,
  type: "TICKET" | "MARKETPLACE",
  userId: string,
  userName: string,
  eventId: string,
  eventName: string,
  productId: string,
  productName: string,
  amount: number,
  paymentMethod: string,
  status: "PENDING" | "COMPLETED" | "FAILED" | "REFUNDED",
  createdAt: timestamp,
  completedAt: timestamp
}
```

## Security Configuration

### Firebase Authentication
The application uses Firebase Authentication with custom claims for role-based access control.

### Admin Role Setup
To grant admin access to a user, set custom claims in Firebase:

```javascript
// Using Firebase Admin SDK
const admin = require('firebase-admin');

// Set admin claim
await admin.auth().setCustomUserClaims(userId, { 
  admin: true, 
  role: 'admin' 
});
```

### Available Roles
- `ROLE_USER` - Default authenticated user
- `ROLE_COORDINATOR` - Event coordinators
- `ROLE_ADMIN` - Full admin access

### Protected Routes
- `/api/admin/**` - Requires ROLE_ADMIN
- `/api/coordinator/**` - Requires ROLE_ADMIN or ROLE_COORDINATOR
- All other `/api/**` - Requires authentication

## Setup Instructions

### 1. Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Firebase project with Firestore and Authentication enabled
- Firebase service account key JSON file

### 2. Configure Firebase
1. Place your Firebase service account key JSON file in `src/main/resources/`
2. Update `application.properties`:
```properties
# Firebase Configuration
firebase.config.path=classpath:your-firebase-key.json

# Server Configuration
server.port=8080
```

### 3. Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

### 4. Create Admin User
After starting the server, create an admin user using Firebase Admin SDK:

```javascript
// admin-setup.js
const admin = require('firebase-admin');
const serviceAccount = require('./path-to-service-account-key.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

// Set admin claims for a user
const userId = 'YOUR_USER_UID';
admin.auth().setCustomUserClaims(userId, { 
  admin: true, 
  role: 'admin' 
})
.then(() => {
  console.log('Admin role set successfully');
})
.catch(error => {
  console.error('Error setting admin role:', error);
});
```

Run the script:
```bash
node admin-setup.js
```

## API Documentation

See [ADMIN_API_DOCUMENTATION.md](ADMIN_API_DOCUMENTATION.md) for complete API reference.

## Testing the API

### Using Postman or Thunder Client

1. **Get Firebase ID Token:**
   - Login to your React app
   - In browser console, run:
   ```javascript
   firebase.auth().currentUser.getIdToken().then(token => console.log(token));
   ```

2. **Make API Request:**
   ```
   GET http://localhost:8080/api/admin/dashboard/stats
   Headers:
     Authorization: Bearer YOUR_FIREBASE_ID_TOKEN
     Content-Type: application/json
   ```

### Example Requests

#### Get Dashboard Stats
```bash
curl -X GET http://localhost:8080/api/admin/dashboard/stats \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### Register Coordinator
```bash
curl -X POST http://localhost:8080/api/admin/coordinators \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+94771234567",
    "email": "john.doe@example.com",
    "degreeProgramme": "Computer Science and Technology"
  }'
```

## Frontend Integration

### React Example

```javascript
import { getAuth } from 'firebase/auth';

// Get dashboard stats
const getDashboardStats = async () => {
  const auth = getAuth();
  const token = await auth.currentUser.getIdToken();
  
  const response = await fetch('http://localhost:8080/api/admin/dashboard/stats', {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  
  if (!response.ok) {
    throw new Error('Failed to fetch dashboard stats');
  }
  
  return await response.json();
};

// Register coordinator
const registerCoordinator = async (coordinatorData) => {
  const auth = getAuth();
  const token = await auth.currentUser.getIdToken();
  
  const response = await fetch('http://localhost:8080/api/admin/coordinators', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(coordinatorData)
  });
  
  if (!response.ok) {
    throw new Error('Failed to register coordinator');
  }
  
  return await response.json();
};
```

## Degree Programmes List

The following degree programmes are available in the coordinator registration form:

1. Animal Production and Food Technology
2. Export Agriculture
3. Aquatic Resources Technology
4. Tea Technology and Value Addition
5. Computer Science and Technology
6. Industrial Information Technology
7. Science & Technology
8. Mineral Resources and Technology
9. Entrepreneurship & Management Studies
10. Hospitality, Tourism & Events Management
11. Human Resource Development
12. English Language & Applied Linguistics
13. Engineering Technology
14. Biosystems Technology Honours
15. Information and Communication Technology Honours

## Troubleshooting

### 401 Unauthorized Error
- Check if Firebase token is valid and not expired
- Ensure user has admin custom claims set
- Verify Authorization header format: `Bearer <token>`

### 403 Forbidden Error
- User doesn't have admin role
- Set custom claims using Firebase Admin SDK

### CORS Issues
- Update `SecurityConfig.java` to include your frontend URL
- Default allowed origins: `http://localhost:3000`, `http://localhost:4200`

### Firebase Connection Issues
- Verify Firebase service account key path in `application.properties`
- Check if Firestore is enabled in Firebase Console
- Ensure Firebase Admin SDK dependencies are included

## Project Structure

```
src/main/java/com/example/campusaura/
├── config/
│   ├── FirebaseConfig.java          # Firebase initialization
│   └── SecurityConfig.java          # Security & CORS configuration
├── controller/
│   └── AdminController.java         # All admin API endpoints
├── dto/
│   ├── CoordinatorRequestDTO.java   # Coordinator registration
│   ├── CoordinatorResponseDTO.java  # Coordinator response
│   ├── DashboardStatsDTO.java       # Dashboard statistics
│   ├── UserResponseDTO.java         # User data
│   ├── UserStatsDTO.java           # User statistics
│   ├── ProductResponseDTO.java      # Product data
│   ├── TransactionResponseDTO.java  # Transaction data
│   └── PaymentStatsDTO.java        # Payment statistics
├── model/
│   ├── Coordinator.java            # Coordinator entity
│   ├── User.java                   # User entity
│   ├── Product.java                # Product entity
│   └── Transaction.java            # Transaction entity
├── security/
│   └── FirebaseAuthFilter.java     # JWT token validation
└── service/
    ├── CoordinatorService.java     # Coordinator business logic
    ├── UserService.java            # User business logic
    ├── ProductService.java         # Product business logic
    ├── TransactionService.java     # Transaction business logic
    ├── DashboardService.java       # Dashboard statistics
    └── EventService.java           # Event management
```

## Next Steps

1. **Frontend Development:**
   - Create React components for each admin section
   - Implement forms with validation
   - Add data tables with sorting and filtering
   - Create charts for analytics

2. **Additional Features:**
   - Add search functionality
   - Implement pagination for large datasets
   - Add export functionality (CSV, PDF)
   - Create audit logs for admin actions
   - Add email notifications

3. **Security Enhancements:**
   - Implement rate limiting
   - Add request validation
   - Log admin activities
   - Add two-factor authentication

## Support

For issues or questions, please check:
- [API Documentation](ADMIN_API_DOCUMENTATION.md)
- Firebase Console for authentication issues
- Application logs in the console

## License

This project is part of the CampusAura application.
