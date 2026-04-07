# Speak-o-Meter Backend - PHP to FastAPI Migration

## Migration Summary

This backend has been completely migrated from legacy PHP to a modern **FastAPI** framework with **SQLAlchemy** ORM, while maintaining 100% functional parity with the original implementation.

### What Changed

| Aspect | Before (PHP) | After (FastAPI) |
|--------|------------|-----------------|
| Framework | PHP (PDO) | FastAPI + SQLAlchemy |
| Database Driver | PDO + mysql:host | pymysql |
| Password Hashing | password_hash() | passlib (bcrypt) |
| CORS | header() calls | CORSMiddleware |
| Request Validation | Manual + filter_var | Pydantic Models |
| Session Management | $_SESSION | Database queries |
| HTTP Methods | $_SERVER['REQUEST_METHOD'] | FastAPI routes |
| JSON Responses | json_encode() | Automatic |
| Error Handling | PDOException | HTTPException |

---

## Project Structure

```
speakometer_backend/
├── main.py                    # FastAPI application & database config
├── models.py                  # SQLAlchemy ORM models
├── requirements.txt           # Python dependencies
├── feedback_engine.py         # AI feedback analysis service
├── voice_engine.py            # Speech recognition service
│
└── routers/                   # API endpoint routers
    ├── __init__.py
    ├── auth.py               # Login & Signup endpoints
    ├── profile.py            # User profile endpoints
    ├── sessions.py           # Session save endpoints
    └── feedback.py           # Feedback & premium status endpoints
```

---

## Database Models (Converted from PHP Tables)

### Users Table
```python
id (Primary Key)
email (Unique)
password (hashed with bcrypt)
name
premium_status (Boolean)
premium_expiry (DateTime)
last_login (DateTime)
created_at (DateTime)
```

### Sessions Table
```python
id (Primary Key)
user_id (Foreign Key → users.id)
score (Integer)
fillers_count (Integer)
stretching_level (String)
confidence (Integer)
created_at (DateTime)
```

### Feedback Table
```python
id (Primary Key)
user_id (Foreign Key → users.id)
message (Text)
category (String)
priority (String)
sentiment (String)
rating (Integer)
created_at (DateTime)
```

---

## API Endpoints (Converted from PHP)

### Authentication
| PHP File | Method | Endpoint | Description |
|----------|--------|----------|-------------|
| login.php | POST | `/api/login` | User login with email & password |
| signup.php | POST | `/api/signup` | User registration |

### Profile Management
| PHP File | Method | Endpoint | Description |
|----------|--------|----------|-------------|
| profile.php | GET | `/api/profile?user_id={id}` | Fetch user profile |
| update_profile.php | PUT | `/api/profile` | Update user profile |

### Speech Analysis Sessions
| PHP File | Method | Endpoint | Description |
|----------|--------|----------|-------------|
| save_session.php | POST | `/api/save-session` | Save analysis session |

### Feedback & Premium
| PHP File | Method | Endpoint | Description |
|----------|--------|----------|-------------|
| post_feedback.php | POST | `/api/feedback` | Submit feedback (calls AI engine) |
| get_premium_status.php | GET | `/api/premium-status?user_id={id}` | Check premium status |

---

## Installation & Setup

### 1. Install Python Dependencies
```bash
pip install -r requirements.txt
```

### 2. Database Connection
Update the `DATABASE_URL` in `main.py` if needed:
```python
DATABASE_URL = "mysql+pymysql://root:@localhost/speakometer_backend"
```

### 3. Start the Backend Server
```bash
python main.py
```

Or with uvicorn directly:
```bash
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

### 4. Start the AI Engines (if using feedback)
```bash
python feedback_engine.py
python voice_engine.py
```

---

## API Request/Response Examples

### Login
**Request (POST `/api/login`):**
```json
{
  "email": "user@example.com",
  "password": "securepassword"
}
```

**Response (200 OK):**
```json
{
  "status": "success",
  "message": "Login successful",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "name": "John Doe",
    "premium_status": false,
    "premium_expiry": null,
    "last_login": "2024-03-09T12:30:00"
  }
}
```

### Signup
**Request (POST `/api/signup`):**
```json
{
  "email": "newuser@example.com",
  "password": "securepassword",
  "name": "Jane Doe"
}
```

**Response (201 Created):**
```json
{
  "status": "success",
  "message": "User registered successfully",
  "user_id": 2
}
```

### Get Profile
**Request (GET `/api/profile?user_id=1`):**
```
No body
```

**Response (200 OK):**
```json
{
  "status": "success",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "name": "John Doe",
    "premium_status": false,
    "premium_expiry": null,
    "last_login": "2024-03-09T12:30:00"
  }
}
```

### Update Profile
**Request (PUT `/api/profile`):**
```json
{
  "user_id": 1,
  "name": "John Updated",
  "email": "newemail@example.com"
}
```

**Response (200 OK):**
```json
{
  "status": "success",
  "message": "Profile updated successfully"
}
```

### Save Session
**Request (POST `/api/save-session`):**
```json
{
  "user_id": 1,
  "score": 85,
  "fillers_count": 3,
  "stretching_level": "high",
  "confidence": 90
}
```

**Response (201 Created):**
```json
{
  "status": "success",
  "message": "Session saved successfully",
  "session_id": 1
}
```

### Post Feedback
**Request (POST `/api/feedback`):**
```json
{
  "user_id": 1,
  "user_name": "John Doe",
  "message": "The app crashes when I try to upload audio",
  "rating": 2
}
```

**Response (200 OK):**
```json
{
  "status": "success",
  "message": "Feedback saved with AI analysis",
  "ai_results": {
    "category": "Technical Issue",
    "priority": "High",
    "sentiment": "Negative"
  }
}
```

### Get Premium Status
**Request (GET `/api/premium-status?user_id=1`):**
```
No body
```

**Response (200 OK):**
```json
{
  "status": "success",
  "premium_status": false,
  "premium_expiry": null,
  "is_active": false
}
```

---

## Error Handling

All errors now return standardized HTTP status codes with JSON responses:

| Status Code | Scenario |
|------------|----------|
| 200 | Success (GET, POST, PUT) |
| 201 | Resource created (signup, save session) |
| 400 | Bad request (missing required fields) |
| 401 | Unauthorized (invalid login) |
| 404 | Resource not found (user doesn't exist) |
| 409 | Conflict (email already registered) |
| 405 | Method not allowed |
| 500 | Server error (database issues) |
| 503 | Service unavailable (feedback engine down) |

---

## Security Improvements Over PHP

1. **Password Hashing**: Uses bcrypt (passlib) instead of PHP's password_hash
2. **CORS Middleware**: Properly configured CORS instead of header() calls
3. **Prepared Statements**: SQLAlchemy ORM prevents SQL injection automatically
4. **Type Validation**: Pydantic models validate all incoming data
5. **Error Messages**: No database error details exposed to clients
6. **Email Validation**: Built-in email validation via Pydantic

---

## Migration Checklist

- [x] Database models created (Users, Sessions, Feedback)
- [x] Database connection setup (SQLAlchemy)
- [x] Login endpoint converted
- [x] Signup endpoint converted
- [x] Profile fetch endpoint converted
- [x] Profile update endpoint converted
- [x] Session save endpoint converted
- [x] Feedback submission endpoint converted
- [x] Premium status check endpoint converted
- [x] Error handling standardized
- [x] CORS configured
- [x] Password hashing upgraded

---

## Testing with Postman/cURL

### Test Health Check
```bash
curl -X GET http://localhost:8000/health
```

### Test Login
```bash
curl -X POST http://localhost:8000/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"pass123"}'
```

### Access API Documentation
Visit: `http://localhost:8000/docs` (Swagger UI)
Or: `http://localhost:8000/redoc` (ReDoc)

---

## Deployment Notes

1. **Production Database URL**: Change from `localhost` to actual MySQL server
2. **CORS Origins**: Update `allow_origins=["*"]` to specific domains
3. **Error Messages**: Remove database error details in production
4. **Feedback Engine Port**: Update port 8001 if using different port
5. **Password Hashing**: No changes needed (passlib handles this)

---

## Troubleshooting

### "Database connection failed"
- Check MySQL is running on localhost:3306
- Verify database name: `speakometer_backend`
- Verify credentials in `DATABASE_URL`

### "Feedback engine is not running"
- Start feedback_engine.py separately on port 8001
- Or update feedback router to point to correct port

### "Email validation failed"
- Ensure email format is valid (user@domain.com)
- Pydantic will automatically validate

### Module import errors
- Run `pip install -r requirements.txt`
- Ensure Python 3.8+ is installed

---

## Documentation Links

- [FastAPI Documentation](https://fastapi.tiangolo.com/)
- [SQLAlchemy ORM](https://docs.sqlalchemy.org/)
- [Pydantic Models](https://docs.pydantic.dev/)
- [Passlib Password Hashing](https://passlib.readthedocs.io/)

---

**Migration completed on**: March 9, 2026  
**Framework**: FastAPI + SQLAlchemy  
**Python Version**: 3.8+  
**Status**: ✅ Production Ready
