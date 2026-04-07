"""
main.py - FastAPI backend for Speak-o-Meter
Converted from legacy PHP backend using SQLAlchemy ORM
"""

from fastapi import FastAPI, Depends
from fastapi.middleware.cors import CORSMiddleware
from config import settings
from utils import SessionLocal, init_db, test_db_connection

# Initialize database on startup
init_db()

# Initialize FastAPI app
app = FastAPI(
    title=settings.API_TITLE,
    description=settings.API_DESCRIPTION,
    version=settings.API_VERSION
)

# Add CORS middleware (allow Android app - restrict in production)
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.CORS_ORIGINS,
    allow_credentials=settings.CORS_CREDENTIALS,
    allow_methods=settings.CORS_METHODS,
    allow_headers=settings.CORS_HEADERS,
)


# Dependency to get database session
def get_db():
    """Dependency injection for database session"""
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


# Health check endpoint
@app.get("/health")
def health_check():
    """Check if the API is running"""
    return {
        "status": "success",
        "message": "Speak-o-Meter Backend is running"
    }


# Import routers after app initialization
from routers import auth, profile, sessions, feedback, analyze

# Include routers
app.include_router(auth.router, prefix="/api", tags=["Authentication"])
app.include_router(profile.router, prefix="/api", tags=["Profile"])
app.include_router(sessions.router, prefix="/api", tags=["Sessions"])
app.include_router(feedback.router, prefix="/api", tags=["Feedback"])
app.include_router(analyze.router, prefix="/api", tags=["Voice Analysis"])


@app.on_event("startup")
async def startup_event():
    """Test database connection on startup"""
    test_db_connection()


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
