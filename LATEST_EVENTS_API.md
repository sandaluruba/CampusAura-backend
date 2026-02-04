# Latest Events API Documentation

## Get Latest 3 Events

This endpoint returns the 3 most recent published or ongoing events from the database, sorted by event date (most recent first).

### Endpoint
```
GET /api/events/latest
```

### Authentication
**No authentication required** - This is a public endpoint.

### Response
Returns a list of up to 3 events with the following structure:

```json
[
  {
    "eventId": "event123",
    "title": "Tech Innovation Fair",
    "description": "Event description...",
    "venue": "Main Auditorium",
    "dateTime": "2024-05-15T10:00:00Z",
    "eventImageUrls": [
      "https://example.com/image1.jpg",
      "https://example.com/image2.jpg"
    ],
    "organizingDepartment": "Computer Science"
  },
  {
    "eventId": "event124",
    "title": "Music Fest",
    "description": "Event description...",
    "venue": "Open Theater",
    "dateTime": "2024-06-05T18:00:00Z",
    "eventImageUrls": [
      "https://example.com/image3.jpg"
    ],
    "organizingDepartment": "Music Department"
  },
  {
    "eventId": "event125",
    "title": "Career Workshop",
    "description": "Event description...",
    "venue": "Conference Hall",
    "dateTime": "2024-06-12T14:00:00Z",
    "eventImageUrls": [
      "https://example.com/image4.jpg"
    ],
    "organizingDepartment": "Career Services"
  }
]
```

### Frontend Integration

In your React/Angular frontend, you can fetch the latest events like this:

**React Example:**
```javascript
useEffect(() => {
  const fetchLatestEvents = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/events/latest');
      const events = await response.json();
      setLatestEvents(events);
    } catch (error) {
      console.error('Error fetching latest events:', error);
    }
  };

  fetchLatestEvents();
}, []);
```

**Angular Example:**
```typescript
ngOnInit() {
  this.http.get<LandingPageEventDTO[]>('http://localhost:8080/api/events/latest')
    .subscribe({
      next: (events) => {
        this.latestEvents = events;
      },
      error: (error) => {
        console.error('Error fetching latest events:', error);
      }
    });
}
```

### Features
- Returns only **PUBLISHED** or **ONGOING** events
- Sorted by event date (most recent first)
- Limited to 3 events
- Public access (no authentication required)
- Returns event images for display

### Notes
- Events without a `dateTime` field will be filtered out
- If there are fewer than 3 published events, the API will return all available events
- The event date is in ISO 8601 format (e.g., "2024-05-15T10:00:00Z")
