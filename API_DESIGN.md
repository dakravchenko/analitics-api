# Analytics API Design

## Overview

A REST API for analytics data that allows clients to submit, query, and manage usage analytics while respecting privacy (GDPR-compliant).

---

## Resources

### Analytics Record

The core resource representing a single analytics event.

**Internal Model:**
```json
{
  "traceId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-06-23T14:30:45Z",
  "eventType": "PAGE_VIEW",
  "eventSource": "WEB",
  "sessionId": "anon-sess-abc123xyz"
}

External Response (sessionId excluded for privacy):
JSON

{
  "traceId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-06-23T14:30:45Z",
  "eventType": "PAGE_VIEW",
  "eventSource": "WEB"
}

Endpoints
1. Create events Record

Endpoint: POST /api/v1/events

Request:
JSON

{
  "eventType": "PAGE_VIEW",
  "eventSource": "WEB",
  "sessionId": "anon-sess-abc123xyz"
}

Response (201 Created):
JSON

{
  "traceId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-06-23T14:30:45Z",
  "eventType": "PAGE_VIEW",
  "eventSource": "WEB"
}

Error Responses:

    400 Bad Request — Missing or invalid fields:

JSON

{
  "errors": [
    "eventType is required",
    "eventSource is required",
    "sessionId is required"
  ]
}

    400 Bad Request — Invalid enum values:

JSON

{
  "errors": [
    "eventType 'INVALID_TYPE' is not valid. Valid values are: PAGE_VIEW, BUTTON_CLICK, FORM_SUBMIT, etc.",
    "eventSource 'INVALID_SOURCE' is not valid. Valid values are: WEB, MOBILE, API"
  ]
}

2. List events Records

Endpoint: GET /api/v1/events

Query Parameters:

    type (optional) — Filter by event type (e.g., PAGE_VIEW)
    source (optional) — Filter by event source (e.g., WEB)
    startTime (optional) — ISO 8601 timestamp. Include records from this time onwards
    endTime (optional) — ISO 8601 timestamp. Include records up to this time

Examples:

    GET /api/v1/events— All records
    GET /api/v1/analytics?type=PAGE_VIEW — All PAGE_VIEW events
    GET /api/v1/analytics?startTime=2026-06-23T00:00:00Z — Records from June 23 onwards
    GET /api/v1/analytics?startTime=2026-06-23T00:00:00Z&endTime=2026-06-23T23:59:59Z — Records within a specific day
    GET /api/v1/analytics?type=BUTTON_CLICK&source=MOBILE — Filtered by both type and source

Response (200 OK):
JSON

[
  {
    "traceId": "550e8400-e29b-41d4-a716-446655440000",
    "timestamp": "2026-06-23T14:30:45Z",
    "eventType": "PAGE_VIEW",
    "eventSource": "WEB"
  },
  {
    "traceId": "550e8400-e29b-41d4-a716-446655440001",
    "timestamp": "2026-06-23T14:31:12Z",
    "eventType": "BUTTON_CLICK",
    "eventSource": "WEB"
  }
]

3. Get Single eventsRecord

Endpoint: GET /api/v1/analytics/{id}

Response (200 OK):
JSON

{
  "traceId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-06-23T14:30:45Z",
  "eventType": "PAGE_VIEW",
  "eventSource": "WEB"
}

Error Response:

    404 Not Found — Record does not exist:

JSON

{
  "error": "eventsrecord with id '550e8400-e29b-41d4-a716-446655440000' not found"
}

4. Update events Record

Endpoint: PUT /api/v1/events/{id}

Request:
JSON

{
  "eventType": "FORM_SUBMIT",
  "eventSource": "MOBILE"
}

Notes:

    The original timestamp is preserved
    Only eventType and eventSource can be updated

Response (200 OK):
JSON

{
  "traceId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-06-23T14:30:45Z",
  "eventType": "FORM_SUBMIT",
  "eventSource": "MOBILE"
}

Error Responses:

    404 Not Found — Record does not exist
    400 Bad Request — Missing or invalid fields (same as POST)

5. Delete events Record

Endpoint: DELETE /api/v1/events/{id}

Response:

    204 No Content — Successfully deleted
    404 Not Found — Record does not exist

6. events Report

Endpoint: GET /api/v1/events/report

Notes:

    Returns a global summary across all records
    No filtering parameters supported

Response (200 OK):
JSON

{
  "totalEntries": 5043,
  "totalUniqueSessions": 287,
  "eventTypeSummary": {
    "PAGE_VIEW": 3200,
    "BUTTON_CLICK": 1500,
    "FORM_SUBMIT": 343
  },
  "eventSourceSummary": {
    "WEB": 4000,
    "MOBILE": 1000,
    "API": 43
  }
}

Data Validation Rules
Field	Required	Type	Rules
eventType	Yes	Enum	Must be one of: PAGE_VIEW, BUTTON_CLICK, FORM_SUBMIT, FORM_ABANDON, ERROR
eventSource	Yes	Enum	Must be one of: WEB, MOBILE, API
sessionId	Yes (internal)	String	Anonymized identifier; never exposed in responses
traceId	No (server-generated)	UUID	Auto-generated on creation; read-only
timestamp	No (server-generated)	ISO 8601	Auto-generated at creation; preserved on updates
Error Handling

All error responses follow this structure:

Single Error:
JSON

{
  "error": "Error message"
}

Multiple Errors:
JSON

{
  "errors": [
    "Error message 1",
    "Error message 2"
  ]
}

HTTP Status Codes
Code	Meaning
200 OK	Request succeeded
201 Created	Record created successfully
204 No Content	Record deleted successfully
400 Bad Request	Validation error or invalid input
404 Not Found	Record not found
500 Internal Server Error	Unexpected server error
Privacy & GDPR Compliance

    ✅ No names, email addresses, real user IDs, or IP addresses stored
    ✅ Session IDs are anonymized and server-side only
    ✅ Session IDs never included in API responses
    ✅ Minimal data collection with clear purpose (events only)


