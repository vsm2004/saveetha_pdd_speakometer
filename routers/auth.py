"""
routers/auth.py - Authentication endpoints (Login & Signup)
Converted from: login.php and signup.php
"""

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from pydantic import BaseModel, EmailStr, validator
from models import User
from passlib.context import CryptContext
from datetime import datetime

router = APIRouter()

# Password hashing setup
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")


# Pydantic schemas for request/response
class UserLogin(BaseModel):
    email: EmailStr
    password: str


class UserSignup(BaseModel):
    email: EmailStr
    password: str
    name: str = None

    @validator('password')
    def validate_password(cls, v):
        if len(v) < 6:
            raise ValueError('Password must be at least 6 characters')
        return v


class UserResponse(BaseModel):
    id: int
    email: str
    name: str = None
    premium_status: bool
    premium_expiry: str = None
    last_login: str = None

    class Config:
        from_attributes = True


# Helper functions
def hash_password(password: str) -> str:
    """Hash a password using bcrypt"""
    return pwd_context.hash(password)


def verify_password(plain_password: str, hashed_password: str) -> bool:
    """Verify a password against its hash"""
    return pwd_context.verify(plain_password, hashed_password)


# =====================
# LOGIN ENDPOINT
# =====================
@router.post("/login", status_code=200)
async def login(data: UserLogin, db: Session = Depends(lambda: __import__('utils').SessionLocal())):
    """
    Login endpoint - Converted from login.php
    
    Required fields:
    - email: User email address
    - password: User password
    
    Returns:
    - success status and user data (without password)
    """
    
    # Find user by email
    user = db.query(User).filter(User.email == data.email).first()

    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid email or password"
        )

    # Verify password
    if not verify_password(data.password, user.password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid email or password"
        )

    # Update last_login timestamp
    user.last_login = datetime.utcnow()
    db.commit()

    # Return user data without password
    return {
        "status": "success",
        "message": "Login successful",
        "user": {
            "id": user.id,
            "email": user.email,
            "name": user.name,
            "premium_status": user.premium_status,
            "premium_expiry": user.premium_expiry.isoformat() if user.premium_expiry else None,
            "last_login": user.last_login.isoformat() if user.last_login else None,
        }
    }


# =====================
# SIGNUP ENDPOINT
# =====================
@router.post("/signup", status_code=201)
async def signup(data: UserSignup, db: Session = Depends(lambda: __import__('utils').SessionLocal())):
    """
    Signup endpoint - Converted from signup.php
    
    Required fields:
    - email: User email address
    - password: User password (minimum 6 characters)
    - name: User full name (optional)
    
    Returns:
    - success status and new user_id
    """
    
    # Check if email already exists
    existing_user = db.query(User).filter(User.email == data.email).first()
    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Email already registered"
        )

    # Hash the password
    hashed_password = hash_password(data.password)

    # Create new user
    new_user = User(
        email=data.email,
        password=hashed_password,
        name=data.name,
        premium_status=False,
        premium_expiry=None
    )

    db.add(new_user)
    db.commit()
    db.refresh(new_user)

    return {
        "status": "success",
        "message": "User registered successfully",
        "user_id": new_user.id
    }
