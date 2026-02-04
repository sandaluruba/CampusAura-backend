# Public Events API Documentation

## Overview
This API allows you to load all published events for the public events page with category filtering and sorting options.

## Endpoint

### Get All Public Events

**URL:** `GET /api/events/public`

**Authentication:** Not required (PUBLIC endpoint)

**Query Parameters:**
- `category` (optional) - Filter events by category
  - Allowed values: `All`, `Technology`, `Career`, `Culture`, `Sports`
  - Default: `All`
  
- `sortBy` (optional) - Sort order for the events
  - `upcoming` - Sort by event date/time ascending (soonest events first)
  - `latest` - Sort by creation date descending (newest created events first)
  - `popular` - Sort by attendee count descending (most popular events first)
  - Default: `upcoming`

## Response Format

Returns an array of event objects with the following structure:

```json
[
  {
    "eventId": "string",
    "title": "string",
    "description": "string",
    "venue": "string",
    "dateTime": "ISO 8601 format string",
    "eventImageUrls": ["string"],
    "organizingDepartment": "string",
    "category": "string",
    "attendeeCount": integer
  }
]
```

## Example Requests

### 1. Get All Events (Default - Upcoming First)
```
GET http://localhost:8080/api/events/public
```

### 2. Get Technology Events Sorted by Upcoming
```
GET http://localhost:8080/api/events/public?category=Technology&sortBy=upcoming
```

### 3. Get All Events Sorted by Popularity
```
GET http://localhost:8080/api/events/public?sortBy=popular
```

### 4. Get Career Events Sorted by Latest Created
```
GET http://localhost:8080/api/events/public?category=Career&sortBy=latest
```

### 5. Get Culture Events
```
GET http://localhost:8080/api/events/public?category=Culture
```

### 6. Get Sports Events
```
GET http://localhost:8080/api/events/public?category=Sports
```

## Example Response

```json
[
  {
    "eventId": "abc123",
    "title": "Tech Innovation Summit 2025",
    "description": "Join us for the biggest tech event of the year...",
    "venue": "Main Campus Auditorium",
    "dateTime": "2025-03-15T18:00:00Z",
    "eventImageUrls": [
      "https://example.com/image1.jpg",
      "https://example.com/image2.jpg"
    ],
    "organizingDepartment": "Computer Science",
    "category": "Technology",
    "attendeeCount": 152
  },
  {
    "eventId": "def456",
    "title": "Spring Career Fair",
    "description": "Meet top employers and explore career opportunities...",
    "venue": "Student Center",
    "dateTime": "2025-03-18T10:00:00Z",
    "eventImageUrls": [
      "https://example.com/career-fair.jpg"
    ],
    "organizingDepartment": "Career Services",
    "category": "Career",
    "attendeeCount": 310
  },
  {
    "eventId": "ghi789",
    "title": "Cultural Festival Night",
    "description": "Experience diverse cultures through music, dance, and food...",
    "venue": "Open Grounds",
    "dateTime": "2025-03-20T19:00:00Z",
    "eventImageUrls": [
      "https://example.com/cultural-fest.jpg"
    ],
    "organizingDepartment": "Student Affairs",
    "category": "Culture",
    "attendeeCount": 220
  }
]
```

## Integration with Frontend

### React Example

```javascript
// Fetch all events
const fetchEvents = async (category = 'All', sortBy = 'upcoming') => {
  try {
    const response = await fetch(
      `http://localhost:8080/api/events/public?category=${category}&sortBy=${sortBy}`
    );
    const events = await response.json();
    return events;
  } catch (error) {
    console.error('Error fetching events:', error);
    return [];
  }
};

// Usage in component
const [events, setEvents] = useState([]);
const [category, setCategory] = useState('All');
const [sortBy, setSortBy] = useState('upcoming');

useEffect(() => {
  const loadEvents = async () => {
    const data = await fetchEvents(category, sortBy);
    setEvents(data);
  };
  loadEvents();
}, [category, sortBy]);
```

### Display Event Card

```javascript
const EventCard = ({ event }) => (
  <div className="event-card">
    <span className="category-badge">{event.category}</span>
    <h3>{event.title}</h3>
    <p className="date-time">{new Date(event.dateTime).toLocaleString()}</p>
    <p className="venue">{event.venue}</p>
    <p className="attendees">{event.attendeeCount} attending</p>
    <button>View Details</button>
  </div>
);
```

## Status Codes

- `200 OK` - Successfully retrieved events
- `500 Internal Server Error` - Server error occurred

## Notes

1. Only events with status `PUBLISHED` or `ONGOING` are returned
2. Events without a dateTime field are automatically filtered out
3. The endpoint does not require authentication
4. Category filtering is case-insensitive
5. Default sorting is by upcoming events (dateTime ascending)

## Creating Events with Categories

When creating or updating events through the coordinator endpoints, make sure to include the `category` field:

```json
{
  "title": "Event Title",
  "venue": "Event Location",
  "dateTime": "2025-03-15T18:00:00Z",
  "category": "Technology",
  "description": "Event description...",
  "organizingDepartment": "Department Name",
  "status": "PUBLISHED",
  // ... other fields
}
```

Allowed category values:
- `Technology`
- `Career`
- `Culture`
- `Sports`
