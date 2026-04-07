"""
config.py - Configuration management for the FastAPI backend
"""

import os
from typing import Optional

class Settings:
    """Application settings"""
    
    # Database Configuration
    DB_HOST: str = os.getenv("DB_HOST", "localhost")
    DB_PORT: int = int(os.getenv("DB_PORT", 3306))
    DB_USER: str = os.getenv("DB_USER", "root")
    DB_PASSWORD: str = os.getenv("DB_PASSWORD", "")
    DB_NAME: str = os.getenv("DB_NAME", "speakometer_backend")
    
    # Construct database URL
    DATABASE_URL: str = f"mysql+pymysql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"
    
    # API Configuration
    API_TITLE: str = "Speak-o-Meter Backend"
    API_VERSION: str = "1.0.0"
    API_DESCRIPTION: str = "Converted from PHP to FastAPI with SQLAlchemy ORM"
    
    # CORS Configuration - Set to "*" for flexible Dev Tunnel use
    CORS_ORIGINS: list = ["*"]
    CORS_CREDENTIALS: bool = True
    CORS_METHODS: list = ["GET", "POST", "PUT", "DELETE", "OPTIONS"]
    CORS_HEADERS: list = ["*"]
    
    # External Services
    FEEDBACK_ENGINE_URL: str = os.getenv("FEEDBACK_ENGINE_URL", "http://localhost:8001")
    FEEDBACK_ENGINE_ENDPOINT: str = "/smart-feedback"
    FEEDBACK_ENGINE_TIMEOUT: int = 10
    
    # Server Configuration
    SERVER_HOST: str = os.getenv("SERVER_HOST", "0.0.0.0")
    SERVER_PORT: int = int(os.getenv("SERVER_PORT", 8000))
    RELOAD: bool = os.getenv("RELOAD", "true").lower() == "true"
    
    # Security
    PASSWORD_HASH_SCHEME: str = "bcrypt"
    PASSWORD_MIN_LENGTH: int = 6
    
    # Logging
    LOG_LEVEL: str = os.getenv("LOG_LEVEL", "INFO")


settings = Settings()
