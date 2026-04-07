"""
routers/sessions.py - Speech analysis session endpoints
Converted from: save_session.php
"""

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from pydantic import BaseModel, validator
from models import User, Session as SessionModel

router = APIRouter()


# Pydantic schemas
class SessionCreate(BaseModel):
    user_id: int
    score: int
    fillers_count: int = 0
    stretching_level: str = "none"
    confidence: int = 0

    @validator('score')
    def validate_score(cls, v):
        if not isinstance(v, int):
            raise ValueError('score must be an integer')
        return v

    @validator('fillers_count')
    def validate_fillers(cls, v):
        if not isinstance(v, int):
            raise ValueError('fillers_count must be an integer')
        return v

    @validator('confidence')
    def validate_confidence(cls, v):
        if not isinstance(v, int):
            raise ValueError('confidence must be an integer')
        return v

    @validator('stretching_level')
    def validate_stretching(cls, v):
        if isinstance(v, str):
            v = v.strip().lower()
        return v


# =====================
# SAVE SESSION ENDPOINT
# =====================
@router.post("/save-session", status_code=201)
async def save_session(data: SessionCreate, db: Session = Depends(lambda: __import__('utils').SessionLocal())):
    """
    Save speech analysis session - Converted from save_session.php
    
    Request body:
    - user_id: User ID (required)
    - score: Analysis score (required, integer)
    - fillers_count: Number of fillers detected (optional, default 0)
    - stretching_level: Level of vocal stretching (optional, default "none")
    - confidence: Confidence score (optional, default 0)
    
    Returns:
    - success status and session_id
    """
    
    # Verify user exists (Foreign Key safety)
    user = db.query(User).filter(User.id == data.user_id).first()
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User not found"
        )

    # Create new session record
    new_session = SessionModel(
        user_id=data.user_id,
        score=data.score,
        fillers_count=data.fillers_count,
        stretching_level=data.stretching_level,
        confidence=data.confidence
    )

    db.add(new_session)
    db.commit()
    db.refresh(new_session)

    return {
        "status": "success",
        "message": "Session saved successfully",
        "session_id": new_session.id
    }

# =====================
# GET SESSIONS ENDPOINT
# =====================
@router.get("/sessions/{user_id}", status_code=200)
async def get_sessions(user_id: int, db: Session = Depends(lambda: __import__('utils').SessionLocal())):
    """
    Get all speech analysis sessions for a user
    
    Path parameters:
    - user_id: The ID of the user
    
    Returns:
    - List of session records
    """
    
    # Verify user exists
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User not found"
        )

    # Fetch sessions ordered by newest first
    sessions = db.query(SessionModel).filter(SessionModel.user_id == user_id).order_by(SessionModel.id.desc()).all()

    return {
        "status": "success",
        "total_sessions": len(sessions),
        "sessions": [
            {
                "id": s.id,
                "score": s.score,
                "fillers_count": s.fillers_count,
                "stretching_level": s.stretching_level,
                "confidence": s.confidence,
                "created_at": s.created_at.isoformat() if s.created_at else None
            } for s in sessions
        ]
    }

