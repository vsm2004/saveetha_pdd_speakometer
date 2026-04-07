"""
routers/feedback.py - Feedback submission endpoints
Converted from: post_feedback.php and get_premium_status.php
"""

from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from pydantic import BaseModel
from models import User, Feedback
from config import settings
from datetime import datetime
import httpx

router = APIRouter()


# Pydantic schemas
class FeedbackCreate(BaseModel):
    user_id: int
    user_name: str = "Guest"
    message: str
    rating: int = 0


# =====================
# POST FEEDBACK ENDPOINT
# =====================
@router.post("/feedback", status_code=200)
async def post_feedback(data: FeedbackCreate, db: Session = Depends(lambda: __import__('utils').SessionLocal())):
    """
    Post user feedback - Converted from post_feedback.php
    
    This endpoint:
    1. Sends the message to the Python feedback_engine for AI analysis
    2. Saves the analysis results to the database
    
    Request body:
    - user_id: User ID (required)
    - user_name: User name (optional, default "Guest")
    - message: Feedback message (required)
    - rating: Rating score (optional, default 0)
    
    Returns:
    - success status and AI analysis results
    """
    
    # Verify user exists
    user = db.query(User).filter(User.id == data.user_id).first()
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User not found"
        )

    # Send message to Python feedback_engine for AI analysis
    try:
        async with httpx.AsyncClient() as client:
            response = await client.post(
                f"{settings.FEEDBACK_ENGINE_URL}{settings.FEEDBACK_ENGINE_ENDPOINT}",
                json={
                    "user_id": data.user_id,
                    "user_name": data.user_name,
                    "message": data.message
                },
                timeout=settings.FEEDBACK_ENGINE_TIMEOUT
            )
            response.raise_for_status()
            ai_data = response.json()
    except (httpx.HTTPError, httpx.TimeoutException) as e:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail=f"Feedback engine is not running on {settings.FEEDBACK_ENGINE_URL}"
        )

    # Extract analysis results from AI engine
    analysis = ai_data.get("analysis", {})
    category = analysis.get("category", "General")
    priority = analysis.get("priority", "Low")
    sentiment = analysis.get("sentiment", "Neutral")

    # Save feedback to database with AI analysis
    new_feedback = Feedback(
        user_id=data.user_id,
        message=data.message,
        category=category,
        priority=priority,
        sentiment=sentiment,
        rating=data.rating
    )

    db.add(new_feedback)
    db.commit()
    db.refresh(new_feedback)

    return {
        "status": "success",
        "message": "Feedback saved with AI analysis",
        "ai_results": analysis
    }


# =====================
# GET PREMIUM STATUS ENDPOINT
# =====================
@router.get("/premium-status", status_code=200)
async def get_premium_status(user_id: int = Query(..., description="User ID"), db: Session = Depends(lambda: __import__('utils').SessionLocal())):
    """
    Check premium subscription status - Converted from get_premium_status.php
    
    Query parameters:
    - user_id: The user ID to check (required)
    
    Returns:
    - premium_status: Boolean whether user has premium
    - premium_expiry: Date when premium expires
    - is_active: Boolean whether premium is currently active (not expired)
    """
    
    # Fetch user
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User not found"
        )

    # Logic: Is premium active AND has expiry date NOT passed yet?
    is_premium = user.premium_status
    expiry_date = user.premium_expiry
    current_date = datetime.utcnow().date()

    is_active = False
    if is_premium:
        if expiry_date is None or expiry_date.date() >= current_date:
            is_active = True

    return {
        "status": "success",
        "premium_status": is_premium,
        "premium_expiry": expiry_date.isoformat() if expiry_date else None,
        "is_active": is_active
    }
