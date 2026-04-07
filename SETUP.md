# Speak-o-Meter Backend - Setup & Installation Guide

## Quick Start (5 minutes)

### Windows
1. Double-click `start.bat`
2. Wait for the server to start
3. Open browser: `http://localhost:8000/docs`

### Linux/Mac
```bash
chmod +x start.sh
./start.sh
```

---

## Full Installation Guide

### Prerequisites
- **Python 3.8+** ([Download](https://www.python.org/downloads/))
- **MySQL 5.7+** (Local installation or remote server)
- **pip** (Comes with Python)

### Step 1: Set Up Python Environment

#### Windows CMD:
```cmd
cd c:\xampp2\htdocs\speakometer_backend
python -m venv venv
venv\Scripts\activate
```

#### Linux/Mac Terminal:
```bash
cd ~/xampp/htdocs/speakometer_backend
python3 -m venv venv
source venv/bin/activate
```

### Step 2: Install Dependencies
```bash
pip install -r requirements.txt
```

### Step 3: Configure Database

#### Option A: Update config.py (Recommended)
Edit `config.py` with your database details:
```python
DB_HOST = "localhost"
DB_USER = "root"
DB_PASSWORD = ""  # Your MySQL password
DB_NAME = "speakometer_backend"
```

#### Option B: Use Environment Variables
Create `.env` file:
```env
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=yourpassword
DB_NAME=speakometer_backend
```

### Step 4: Verify Database Connection
```bash
python -c "from utils import test_db_connection; test_db_connection()"
```

Expected output:
```
✅ Database connection successful
```

### Step 5: Start the Backend Server

**Windows:**
```cmd
python main.py
```

**Linux/Mac:**
```bash
python3 main.py
```

Or with auto-reload:
```bash
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

### Step 6: Access the API

- **Swagger UI Docs**: http://localhost:8000/docs
- **ReDoc Documentation**: http://localhost:8000/redoc
- **Health Check**: http://localhost:8000/health

---

## Database Setup

### Create Tables Automatically
The first run will automatically create all tables. If tables don't exist:

```bash
python
>>> from utils import init_db
>>> init_db()
>>> exit()
```

### Manual Table Creation (If Needed)

```sql
-- Users table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    premium_status BOOLEAN DEFAULT FALSE,
    premium_expiry DATETIME,
    last_login DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Sessions table
CREATE TABLE sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    score INT NOT NULL,
    fillers_count INT DEFAULT 0,
    stretching_level VARCHAR(50) DEFAULT 'none',
    confidence INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Feedback table
CREATE TABLE feedback (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    category VARCHAR(100) DEFAULT 'General',
    priority VARCHAR(50) DEFAULT 'Low',
    sentiment VARCHAR(50) DEFAULT 'Neutral',
    rating INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## Running Multiple Services

### Terminal 1: Main FastAPI Backend
```bash
python main.py
# or
uvicorn main:app --host 0.0.0.0 --port 8000
```

### Terminal 2: Feedback Engine
```bash
python feedback_engine.py
# Listens on: http://localhost:8001
```

### Terminal 3: Voice Engine (Optional)
```bash
python voice_engine.py
# Handles speech recognition
```

---

## Troubleshooting

### "ModuleNotFoundError: No module named 'fastapi'"
**Solution:**
```bash
pip install -r requirements.txt
```

### "Database connection failed"
1. Verify MySQL is running
2. Check credentials in `config.py`
3. Ensure database exists:
   ```sql
   CREATE DATABASE IF NOT EXISTS speakometer_backend;
   ```

### "Connection refused on localhost:3306"
1. Start MySQL service
   - **Windows**: Services → MySQL80 → Start
   - **Linux**: `sudo systemctl start mysql`
   - **Mac**: `brew services start mysql`

### "Port 8000 already in use"
Change port in `config.py`:
```python
SERVER_PORT = 8001
```

Or kill process on port 8000:
- **Windows**: `netstat -ano | findstr 8000` then `taskkill /PID {pid}`
- **Linux/Mac**: `lsof -i :8000` then `kill -9 {pid}`

### "Feedback engine not running on Port 8001"
When posting feedback, ensure `feedback_engine.py` is running in a separate terminal.

---

## Testing the API

### Using curl (Command Line)
```bash
# Health check
curl http://localhost:8000/health

# Signup
curl -X POST http://localhost:8000/api/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123","name":"Test User"}'

# Login
curl -X POST http://localhost:8000/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'
```

### Using Postman
1. Import or manually create requests
2. Use endpoints from MIGRATION.md
3. Set `Content-Type: application/json` header

### Using Python
```python
import requests

# Signup
response = requests.post(
    "http://localhost:8000/api/signup",
    json={
        "email": "test@example.com",
        "password": "test123",
        "name": "Test User"
    }
)
print(response.json())

# Login
response = requests.post(
    "http://localhost:8000/api/login",
    json={
        "email": "test@example.com",
        "password": "test123"
    }
)
print(response.json())
```

---

## Project Structure After Setup

```
speakometer_backend/
├── main.py                 # FastAPI app entry point
├── models.py              # SQLAlchemy ORM models
├── config.py              # Configuration settings
├── utils.py               # Database utilities
├── requirements.txt       # Python dependencies
├── .env.example          # Environment variables template
├── MIGRATION.md          # Migration documentation
├── SETUP.md             # This file
├── start.bat            # Windows startup script
├── start.sh             # Linux/Mac startup script
├── routers/             # API route modules
│   ├── auth.py          # Login/Signup endpoints
│   ├── profile.py       # Profile endpoints
│   ├── sessions.py      # Session endpoints
│   └── feedback.py      # Feedback endpoints
├── feedback_engine.py   # AI feedback analysis
├── voice_engine.py      # Speech recognition
└── venv/               # Python virtual environment (after setup)
```

---

## Environment Variables

Create `.env` file in root directory:

```env
# Database
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=yourpassword
DB_NAME=speakometer_backend

# Server
SERVER_HOST=0.0.0.0
SERVER_PORT=8000
RELOAD=true

# External Services
FEEDBACK_ENGINE_URL=http://localhost:8001

# Logging
LOG_LEVEL=INFO
```

---

## Deployment to Production

### 1. Install on Production Server
```bash
git clone <your-repo> speakometer_backend
cd speakometer_backend
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

### 2. Update Configuration
```bash
cp .env.example .env
# Edit .env with production values
```

### 3. Create Database on Production
```sql
CREATE DATABASE speakometer_backend CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON speakometer_backend.* TO 'backend_user'@'localhost' IDENTIFIED BY 'strong_password';
FLUSH PRIVILEGES;
```

### 4. Run with Gunicorn (Production WSGI Server)
```bash
pip install gunicorn
gunicorn -w 4 -b 0.0.0.0:8000 main:app
```

### 5. Use Supervisor or Systemd for Auto-restart

**Systemd Service File** (`/etc/systemd/system/speakometer.service`):
```ini
[Unit]
Description=Speak-o-Meter Backend
After=network.target

[Service]
Type=notify
User=www-data
WorkingDirectory=/var/www/speakometer_backend
Environment="PATH=/var/www/speakometer_backend/venv/bin"
ExecStart=/var/www/speakometer_backend/venv/bin/gunicorn -w 4 -b 0.0.0.0:8000 main:app

[Install]
WantedBy=multi-user.target
```

Start service:
```bash
sudo systemctl enable speakometer
sudo systemctl start speakometer
```

### 6. Configure Reverse Proxy (Nginx)
```nginx
server {
    listen 80;
    server_name api.speakometer.com;

    location / {
        proxy_pass http://127.0.0.1:8000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

---

## Performance Optimization

### Database Connection Pooling
Already configured in `utils.py`:
```python
pool_recycle=3600  # Recycle connections every hour
pool_pre_ping=True  # Verify connections before using
```

### Enable Query Caching (Optional)
Edit MySQL config:
```ini
query_cache_size = 268435456
query_cache_type = 1
```

### Monitor API Performance
```python
# Add to main.py for timing
from time import time

@app.middleware("http")
async def add_process_time_header(request, call_next):
    start_time = time()
    response = await call_next(request)
    process_time = time() - start_time
    response.headers["X-Process-Time"] = str(process_time)
    return response
```

---

## Maintenance

### Backup Database
```bash
mysqldump -u root -p speakometer_backend > backup.sql
```

### Restore Database
```bash
mysql -u root -p speakometer_backend < backup.sql
```

### Check Logs
View application logs (if using Systemd):
```bash
sudo journalctl -u speakometer -f
```

### Update Dependencies
```bash
pip install --upgrade -r requirements.txt
```

---

## Support & Documentation

- **FastAPI Docs**: https://fastapi.tiangolo.com/
- **SQLAlchemy**: https://docs.sqlalchemy.org/
- **Pydantic**: https://docs.pydantic.dev/
- **Uvicorn**: https://www.uvicorn.org/

---

**Status**: ✅ Ready for Development & Production  
**Last Updated**: March 9, 2026  
**Version**: 1.0.0
