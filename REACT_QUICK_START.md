# Quick Start Guide for React Frontend

## 1. Setup Firebase Authentication

```javascript
// firebase.js
import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';

const firebaseConfig = {
  // Your Firebase config
};

const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
```

## 2. Create API Service

```javascript
// services/adminAPI.js
import { auth } from '../firebase';

const API_BASE_URL = 'http://localhost:8080/api/admin';

const getAuthToken = async () => {
  const user = auth.currentUser;
  if (!user) throw new Error('Not authenticated');
  return await user.getIdToken();
};

const apiCall = async (endpoint, options = {}) => {
  const token = await getAuthToken();
  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
      ...options.headers,
    },
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'Request failed');
  }
  
  return await response.json();
};

export const adminAPI = {
  // Dashboard
  getDashboardStats: () => apiCall('/dashboard/stats'),
  
  // Coordinators
  getCoordinators: () => apiCall('/coordinators'),
  registerCoordinator: (data) => apiCall('/coordinators', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  updateCoordinatorStatus: (id, active) => apiCall(`/coordinators/${id}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ active }),
  }),
  deleteCoordinator: (id) => apiCall(`/coordinators/${id}`, {
    method: 'DELETE',
  }),
  getDegreeProgrammes: () => apiCall('/coordinators/degree-programmes'),
  
  // Events
  getEvents: () => apiCall('/events'),
  getEventById: (id) => apiCall(`/events/${id}`),
  deleteEvent: (id) => apiCall(`/events/${id}`, {
    method: 'DELETE',
  }),
  filterEventsByCategory: (category) => apiCall(`/events/filter?category=${category}`),
  
  // Users
  getUsers: () => apiCall('/users'),
  getUniversityStudents: () => apiCall('/users/university-students'),
  getExternalUsers: () => apiCall('/users/external-users'),
  getPendingVerification: () => apiCall('/users/pending-verification'),
  getUserStats: () => apiCall('/users/stats'),
  updateUserStatus: (id, active) => apiCall(`/users/${id}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ active }),
  }),
  verifyStudent: (id, status) => apiCall(`/users/${id}/verify`, {
    method: 'PATCH',
    body: JSON.stringify({ status }),
  }),
  deleteUser: (id) => apiCall(`/users/${id}`, {
    method: 'DELETE',
  }),
  
  // Products
  getProducts: () => apiCall('/products'),
  getProductById: (id) => apiCall(`/products/${id}`),
  deleteProduct: (id) => apiCall(`/products/${id}`, {
    method: 'DELETE',
  }),
  
  // Payments
  getPaymentStats: () => apiCall('/payments/stats'),
  getTransactions: () => apiCall('/payments/transactions'),
  getRecentTransactions: (limit = 10) => apiCall(`/payments/transactions/recent?limit=${limit}`),
};
```

## 3. Create Custom Hook

```javascript
// hooks/useAdmin.js
import { useState, useEffect } from 'react';
import { adminAPI } from '../services/adminAPI';

export const useAdmin = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const execute = async (apiFunction, ...args) => {
    setLoading(true);
    setError(null);
    try {
      const result = await apiFunction(...args);
      setLoading(false);
      return result;
    } catch (err) {
      setError(err.message);
      setLoading(false);
      throw err;
    }
  };

  return { execute, loading, error };
};
```

## 4. Example Components

### Dashboard Component

```javascript
// components/admin/Dashboard.jsx
import React, { useState, useEffect } from 'react';
import { adminAPI } from '../../services/adminAPI';
import { useAdmin } from '../../hooks/useAdmin';

const Dashboard = () => {
  const [stats, setStats] = useState(null);
  const { execute, loading, error } = useAdmin();

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    const data = await execute(adminAPI.getDashboardStats);
    setStats(data);
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;
  if (!stats) return null;

  return (
    <div className="dashboard">
      <h1>Admin Dashboard</h1>
      
      <div className="stats-grid">
        <div className="stat-card">
          <h3>Total Events</h3>
          <p>{stats.totalEvents}</p>
        </div>
        <div className="stat-card">
          <h3>Total Users</h3>
          <p>{stats.totalUsers}</p>
        </div>
        <div className="stat-card">
          <h3>Total Products</h3>
          <p>{stats.totalProducts}</p>
        </div>
        <div className="stat-card">
          <h3>Products Sold</h3>
          <p>{stats.productsSold}</p>
        </div>
      </div>

      <div className="recent-events">
        <h2>Recent Events</h2>
        {stats.recentEvents.map(event => (
          <div key={event.eventId} className="event-card">
            <h3>{event.title}</h3>
            <p>{event.venue} - {event.dateTime}</p>
            <span>{event.status}</span>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Dashboard;
```

### Coordinator Form Component

```javascript
// components/admin/CoordinatorForm.jsx
import React, { useState, useEffect } from 'react';
import { adminAPI } from '../../services/adminAPI';
import { useAdmin } from '../../hooks/useAdmin';

const CoordinatorForm = ({ onSuccess }) => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    phoneNumber: '',
    email: '',
    degreeProgramme: '',
  });
  const [programmes, setProgrammes] = useState([]);
  const { execute, loading, error } = useAdmin();

  useEffect(() => {
    loadProgrammes();
  }, []);

  const loadProgrammes = async () => {
    const data = await execute(adminAPI.getDegreeProgrammes);
    setProgrammes(data);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    await execute(adminAPI.registerCoordinator, formData);
    onSuccess?.();
    // Reset form
    setFormData({
      firstName: '',
      lastName: '',
      phoneNumber: '',
      email: '',
      degreeProgramme: '',
    });
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Register Coordinator</h2>
      
      <input
        type="text"
        name="firstName"
        placeholder="First Name"
        value={formData.firstName}
        onChange={handleChange}
        required
      />
      
      <input
        type="text"
        name="lastName"
        placeholder="Last Name"
        value={formData.lastName}
        onChange={handleChange}
        required
      />
      
      <input
        type="tel"
        name="phoneNumber"
        placeholder="Phone Number"
        value={formData.phoneNumber}
        onChange={handleChange}
        required
      />
      
      <input
        type="email"
        name="email"
        placeholder="Email"
        value={formData.email}
        onChange={handleChange}
        required
      />
      
      <select
        name="degreeProgramme"
        value={formData.degreeProgramme}
        onChange={handleChange}
        required
      >
        <option value="">Select Degree Programme</option>
        {programmes.map(programme => (
          <option key={programme} value={programme}>
            {programme}
          </option>
        ))}
      </select>
      
      <button type="submit" disabled={loading}>
        {loading ? 'Registering...' : 'Register Coordinator'}
      </button>
      
      {error && <div className="error">{error}</div>}
    </form>
  );
};

export default CoordinatorForm;
```

### User Verification Component

```javascript
// components/admin/UserVerification.jsx
import React, { useState, useEffect } from 'react';
import { adminAPI } from '../../services/adminAPI';
import { useAdmin } from '../../hooks/useAdmin';

const UserVerification = () => {
  const [users, setUsers] = useState([]);
  const { execute, loading, error } = useAdmin();

  useEffect(() => {
    loadPendingUsers();
  }, []);

  const loadPendingUsers = async () => {
    const data = await execute(adminAPI.getPendingVerification);
    setUsers(data);
  };

  const handleVerify = async (userId, status) => {
    await execute(adminAPI.verifyStudent, userId, status);
    // Reload the list
    loadPendingUsers();
  };

  return (
    <div className="user-verification">
      <h2>Pending Verification ({users.length})</h2>
      
      {loading && <div>Loading...</div>}
      {error && <div className="error">{error}</div>}
      
      <div className="verification-list">
        {users.map(user => (
          <div key={user.id} className="verification-card">
            <div className="user-info">
              <h3>{user.firstName} {user.lastName}</h3>
              <p>Email: {user.email}</p>
              <p>Student ID: {user.studentId}</p>
              <p>User Type: {user.userType}</p>
            </div>
            
            {user.idImageUrl && (
              <div className="id-image">
                <img src={user.idImageUrl} alt="ID" />
              </div>
            )}
            
            <div className="actions">
              <button 
                onClick={() => handleVerify(user.id, 'VERIFIED')}
                className="btn-approve"
              >
                Approve
              </button>
              <button 
                onClick={() => handleVerify(user.id, 'REJECTED')}
                className="btn-reject"
              >
                Reject
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default UserVerification;
```

### Payment Stats Component

```javascript
// components/admin/PaymentStats.jsx
import React, { useState, useEffect } from 'react';
import { adminAPI } from '../../services/adminAPI';
import { useAdmin } from '../../hooks/useAdmin';

const PaymentStats = () => {
  const [stats, setStats] = useState(null);
  const { execute, loading, error } = useAdmin();

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    const data = await execute(adminAPI.getPaymentStats);
    setStats(data);
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;
  if (!stats) return null;

  return (
    <div className="payment-stats">
      <h2>Revenue Statistics</h2>
      
      <div className="revenue-grid">
        <div className="revenue-card">
          <h3>Ticket Revenue</h3>
          <p className="amount">LKR {stats.ticketRevenue.toLocaleString()}</p>
        </div>
        <div className="revenue-card">
          <h3>Marketplace Revenue</h3>
          <p className="amount">LKR {stats.marketplaceRevenue.toLocaleString()}</p>
        </div>
        <div className="revenue-card total">
          <h3>Total Revenue</h3>
          <p className="amount">LKR {stats.totalRevenue.toLocaleString()}</p>
        </div>
      </div>

      <div className="recent-transactions">
        <h3>Recent Transactions</h3>
        <table>
          <thead>
            <tr>
              <th>Date</th>
              <th>User</th>
              <th>Type</th>
              <th>Amount</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {stats.recentTransactions.map(transaction => (
              <tr key={transaction.id}>
                <td>{new Date(transaction.createdAt).toLocaleDateString()}</td>
                <td>{transaction.userName}</td>
                <td>{transaction.type}</td>
                <td>LKR {transaction.amount.toLocaleString()}</td>
                <td>
                  <span className={`status-${transaction.status.toLowerCase()}`}>
                    {transaction.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default PaymentStats;
```

## 5. Protected Route

```javascript
// components/ProtectedRoute.jsx
import { Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

const ProtectedRoute = ({ children, requireAdmin = false }) => {
  const { user, loading, isAdmin } = useAuth();

  if (loading) return <div>Loading...</div>;
  
  if (!user) return <Navigate to="/login" />;
  
  if (requireAdmin && !isAdmin) {
    return <Navigate to="/unauthorized" />;
  }

  return children;
};

export default ProtectedRoute;
```

## 6. Set Admin Custom Claims

```javascript
// Use Firebase Admin SDK (Node.js) to set admin role
const admin = require('firebase-admin');

async function makeUserAdmin(userId) {
  try {
    await admin.auth().setCustomUserClaims(userId, { 
      admin: true, 
      role: 'admin' 
    });
    console.log('Admin role set successfully');
  } catch (error) {
    console.error('Error:', error);
  }
}

// Usage
makeUserAdmin('USER_UID_HERE');
```

## 7. Check if User is Admin

```javascript
// In your React app
import { auth } from './firebase';

const checkAdminStatus = async () => {
  const user = auth.currentUser;
  if (!user) return false;
  
  const token = await user.getIdTokenResult();
  return token.claims.admin === true;
};
```

## Common Patterns

### Loading State
```javascript
const [loading, setLoading] = useState(false);

const handleAction = async () => {
  setLoading(true);
  try {
    await adminAPI.someAction();
  } catch (error) {
    console.error(error);
  } finally {
    setLoading(false);
  }
};
```

### Error Handling
```javascript
const [error, setError] = useState(null);

try {
  const result = await adminAPI.someAction();
} catch (err) {
  setError(err.message);
  // Show toast notification
}
```

### Confirmation Dialog
```javascript
const handleDelete = async (id) => {
  if (window.confirm('Are you sure you want to delete this item?')) {
    await adminAPI.deleteItem(id);
    // Reload list
  }
};
```

## Testing Your Implementation

1. **Test Authentication:**
   - Login with Firebase
   - Check if token is generated
   - Verify admin claims are set

2. **Test Each Endpoint:**
   - Dashboard stats loading
   - Coordinator registration
   - User verification
   - Product management
   - Payment statistics

3. **Test Error Handling:**
   - Invalid token
   - Network errors
   - Permission denied

## Recommended Libraries

- **UI Framework:** Material-UI, Ant Design, or Chakra UI
- **State Management:** Redux Toolkit or Zustand
- **Data Tables:** React Table or AG Grid
- **Charts:** Recharts or Chart.js
- **Forms:** React Hook Form or Formik
- **Notifications:** React Toastify

## Tips

1. Always check if user is authenticated before API calls
2. Handle token refresh when it expires
3. Show loading states for better UX
4. Implement proper error messages
5. Add confirmation dialogs for destructive actions
6. Use debouncing for search/filter inputs
7. Implement pagination for large lists
8. Cache data when appropriate
9. Add real-time updates using Firestore listeners
10. Test on different screen sizes

Happy coding! 🚀
