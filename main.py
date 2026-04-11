"""
main.py - FastAPI backend for Speak-o-Meter
Converted from legacy PHP backend using SQLAlchemy ORM
"""

from fastapi import FastAPI, Depends, Request
from fastapi.responses import JSONResponse
import traceback
import logging
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


# Global Exception Handler to catch 500 errors and log them
@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    # Log the full traceback to the terminal/console
    error_traceback = traceback.format_exc()
    print("\n" + "="*50)
    print(f"❌ CRITICAL SERVER ERROR: {str(exc)}")
    print(error_traceback)
    print("="*50 + "\n")
    
    response_content = {
        "status": "error",
        "message": "Internal Server Error",
    }
    
    # If in debug mode, return the actual error details to the app
    if settings.DEBUG:
        response_content["detail"] = str(exc)
        response_content["traceback"] = error_traceback
        
    return JSONResponse(
        status_code=500,
        content=response_content
    )


# Health check endpoint
@app.get("/health")
def health_check():
    """Check if the API and Database are running"""
    db_ok = test_db_connection()
    return {
        "status": "success" if db_ok else "error",
        "api": "online",
        "database": "connected" if db_ok else "disconnected",
        "message": "Speak-o-Meter Backend is running" if db_ok else "CRITICAL: Database connection failed"
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
