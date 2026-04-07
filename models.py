"""
models.py - SQLAlchemy ORM models for Speak-o-Meter database
Maps to existing MySQL tables: users, sessions, feedback
"""

from sqlalchemy import Column, Integer, String, DateTime, Boolean, Float, Text, ForeignKey
from sqlalchemy.ext.declarative import declarative_base
from datetime import datetime

Base = declarative_base()
class User(Base):
    """User table - stores registration and account information"""
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    email = Column(String(255), unique=True, index=True, nullable=False)
    password = Column(String(255), nullable=False)
    name = Column(String(255), nullable=True)
    premium_status = Column(Boolean, default=False)
    premium_expiry = Column(DateTime, nullable=True)
    last_login = Column(DateTime, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)


class Session(Base):
    """Session table - stores speech analysis results"""
    __tablename__ = "sessions"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    score = Column(Integer, nullable=False)
    fillers_count = Column(Integer, default=0)
    stretching_level = Column(String(50), default="none")
    confidence = Column(Integer, default=0)
    created_at = Column(DateTime, default=datetime.utcnow)


class Feedback(Base):
    """Feedback table - stores user feedback with AI analysis"""
    __tablename__ = "feedback"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    message = Column(Text, nullable=False)
    category = Column(String(100), default="General")
    priority = Column(String(50), default="Low")
    sentiment = Column(String(50), default="Neutral")
    rating = Column(Integer, default=0)
    created_at = Column(DateTime, default=datetime.utcnow)
