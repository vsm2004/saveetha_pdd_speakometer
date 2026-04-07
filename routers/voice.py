from __future__ import annotations

import math
import tempfile
from typing import Any, Dict, Optional

from fastapi import APIRouter, File, UploadFile


router = APIRouter()


@router.post("/analyze")
async def analyze_speech(file: UploadFile = File(...)) -> Dict[str, Any]:
    """
    Lightweight `/api/analyze` endpoint for the Android app.

    The previous standalone `voice_engine.py` provided `/analyze`. In the current
    setup you run `uvicorn main:app --reload`, so we expose an equivalent route
    here to avoid 404s during app testing.

    This implementation intentionally avoids loading heavy ML models at startup.
    It returns a minimal, valid `AnalyzeResponse` payload that unblocks the app
    flow and can be upgraded later.
    """
    # Read & discard to ensure request succeeds and file is received.
    data = await file.read()
    size_bytes = len(data)

    # Heuristic placeholders (kept stable and within reasonable ranges).
    # If you later integrate the real AI pipeline, keep the same response shape.
    approx_seconds = max(5.0, min(180.0, size_bytes / 16000.0))  # ~16KB/sec rough guess
    approx_wpm = round(110.0 + (math.log10(max(size_bytes, 1)) * 5.0), 1)

    return {
        "status": "success",
        "message": None,
        "transcription": None,
        "wpm": approx_wpm,
        "tone_analysis": "Steady / Neutral",
        "accent_score": 85.0,
        "filler_count": 0,
        "waveform_url": None,
        "confidence_score": 90,
        "duration_seconds": approx_seconds,
    }

