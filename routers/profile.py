"""
routers/profile.py - User profile endpoints (Get & Update)
Converted from: profile.php and update_profile.php
"""

from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from pydantic import BaseModel, EmailStr, validator
from models import User
from datetime import datetime

router = APIRouter()


# Pydantic schemas
class ProfileUpdate(BaseModel):
    user_id: int
    name: str = None
    email: EmailStr = None


class ProfileResponse(BaseModel):
    id: int
    email: str
    name: str = None
    premium_status: bool
    premium_expiry: str = None
    last_login: str = None

    class Config:
        from_attributes = True


# =====================
# GET PROFILE ENDPOINT
# =====================
@router.get("/profile", status_code=200)
async def get_profile(user_id: int = Query(..., description="User ID"), db: Session = Depends(lambda: __import__('utils').SessionLocal())):
    """
    Get user profile - Converted from profile.php
    
    Query parameters:
    - user_id: The user ID to fetch profile for
    
    Returns:
    - User profile data (without password)
    """
    
    # Fetch user by ID (without password for security)
    user = db.query(User).filter(User.id == user_id).first()

    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User not found"
        )

    return {
        "status": "success",
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
# UPDATE PROFILE ENDPOINT
# =====================
@router.put("/profile", status_code=200)
async def update_profile(data: ProfileUpdate, db: Session = Depends(lambda: __import__('utils').SessionLocal())):
    """
    Update user profile - Converted from update_profile.php
    
    Request body:
    - user_id: The user ID to update (required)
    - name: New name (optional)
    - email: New email (optional)
    
    Returns:
    - success status
    """
    
    # Check if user exists
    user = db.query(User).filter(User.id == data.user_id).first()
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User not found"
        )

    # Update only provided fields
    if data.name is not None:
        user.name = data.name.strip()

    if data.email is not None:
        # Check if email is already taken by another user
        existing_email = db.query(User).filter(
            User.email == data.email,
            User.id != data.user_id
        ).first()
        if existing_email:
            raise HTTPException(
                status_code=status.HTTP_409_CONFLICT,
                detail="Email already in use"
            )
        user.email = data.email.strip()

    db.commit()

    return {
        "status": "success",
        "message": "Profile updated successfully"
    }


# =====================
# UPGRADE PREMIUM ENDPOINT
# =====================
class PremiumUpgradeRequest(BaseModel):
    user_id: int
    plan_type: str  # "monthly" or "yearly"
    is_free_trial: bool = False

@router.post("/upgrade-premium", status_code=200)
async def upgrade_premium(data: PremiumUpgradeRequest, db: Session = Depends(lambda: __import__('utils').SessionLocal())):
    """
    Upgrade User to Premium Status
    """
    from datetime import timedelta
    
    # Check if user exists
    user = db.query(User).filter(User.id == data.user_id).first()
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User not found"
        )
        
    # Set Status to True
    user.premium_status = True
    
    # Calculate Expiry
    now = datetime.utcnow()
    
    # Normalize premium_expiry to datetime for safe comparison (Fixes TypeError between date and datetime)
    expiry = user.premium_expiry
    if expiry and not isinstance(expiry, datetime):
        # If it's a date object (common in some DB configurations), convert to datetime at midnight
        from datetime import time
        expiry = datetime.combine(expiry, time.min)
        
    # If they already had premium, extend from their current expiry (if future), else from now
    base_date = expiry if expiry and expiry > now else now
    
    if data.is_free_trial:
        user.premium_expiry = base_date + timedelta(days=7)
    elif data.plan_type.lower() == "yearly":
        user.premium_expiry = base_date + timedelta(days=365)
    else: # monthly
        user.premium_expiry = base_date + timedelta(days=30)
        
    db.commit()

    return {
        "status": "success",
        "message": f"Successfully upgraded to {data.plan_type} premium",
        "expiry_date": user.premium_expiry.isoformat()
    }
