"""
routers/analyze.py - Integrated voice analysis router
Merged from legacy voice_engine.py
"""

import os
import whisper
import librosa
import librosa.display
import matplotlib.pyplot as plt
import numpy as np
from fastapi import APIRouter, UploadFile, File, HTTPException
from fastapi.responses import FileResponse
from pydantic import BaseModel

router = APIRouter()

# --- FFmpeg Path Configuration ---
# Ensure this points to your specific folder (DELL' path used in voice_engine.py)
ffmpeg_dir = r"C:\Users\DELL'\AppData\Local\Microsoft\WinGet\Links"
if os.path.exists(ffmpeg_dir):
    os.environ["PATH"] += os.pathsep + ffmpeg_dir

# Load the AI model (using 'base' for a good balance of speed/accuracy)
# Note: In a production environment, this should be done once globally.
print("Loading AI Model (Whisper)... please wait.")
try:
    model = whisper.load_model("tiny")
except Exception as e:
    print(f"Warning: Whisper model could not be loaded: {e}")
    model = None

@router.post("/analyze")
async def analyze_speech(file: UploadFile = File(...)):
    """
    Main endpoint for voice analysis.
    Equivalent to the old voice_engine.py /analyze endpoint.
    """
    if model is None:
         raise HTTPException(status_code=503, detail="AI Model not loaded")

    file_path = f"temp_{file.filename}"
    waveform_path = f"waveform_{file.filename}.png"
    
    try:
        print(f"--- Voice Analysis Started for: {file.filename} ---")
        # Save the incoming file
        contents = await file.read()
        with open(file_path, "wb") as f:
            f.write(contents)
        print("  - File saved successfully to disk.")
 
        # 1. Transcription & WPM
        print("  - Starting Transcription (Whisper)... this might take time.")
        result = model.transcribe(file_path)
        text = result.get('text', "")
        words = text.split()
        print(f"  - Transcription complete. Words: {len(words)}")
        
        # 2. Audio Processing with Librosa
        print("  - Loading audio into Librosa...")
        y, sr = librosa.load(file_path)
        duration_sec = librosa.get_duration(y=y, sr=sr)
        duration_min = duration_sec / 60
        wpm = len(words) / duration_min if duration_min > 0 else 0
        print(f"  - Librosa load complete. Duration: {round(duration_sec, 1)}s")
 
        # 3. Tone & Confidence Analysis
        print("  - Analyzing Tone & Pitch...")
        pitches, magnitudes = librosa.piptrack(y=y, sr=sr)
        valid_pitches = pitches[pitches > 0]
        pitch_std = np.std(valid_pitches) if len(valid_pitches) > 0 else 0
        
        if pitch_std > 50:
            tone = "Confident & Expressive"
        elif pitch_std < 15:
            tone = "Monotone / Nervous"
        else:
            tone = "Steady / Neutral"
 
        # 4. Visual Waveform Generation
        print("  - Generating Waveform PNG...")
        plt.figure(figsize=(10, 4))
        plt.axis('off') 
        librosa.display.waveshow(y, sr=sr, color='#6200EE') 
        plt.savefig(waveform_path, transparent=True, bbox_inches='tight', pad_inches=0)
        plt.close()
 
        # 5. Accent Comparison (MFCC & Spectral Contrast)
        print("  - Finalizing Accent Scores...")
        spectral_contrast = librosa.feature.spectral_contrast(y=y, sr=sr)
        clarity_value = np.mean(spectral_contrast)
        accent_score = min(98.5, 70 + (clarity_value * 1.5))
 
        # 6. Filler Word Detection
        fillers = ["um", "uh", "ah", "like", "basically", "actually"]
        filler_count = sum(1 for word in words if word.lower().strip(",.") in fillers)
 
        print(f"--- Analysis SUCCESS for: {file.filename} ---")
        return {
            "status": "success",
            "transcription": text.strip(),
            "wpm": round(wpm, 1),
            "tone_analysis": tone,
            "accent_score": round(accent_score, 1),
            "filler_count": filler_count,
            "waveform_url": f"/api/get-waveform/{waveform_path}",
            "confidence_score": max(0, 100 - (filler_count * 5))
        }
 
    except Exception as e:
        print(f"!!! Error during analysis of {file.filename}: {e} !!!")
        return {"status": "error", "message": str(e)}

    finally:
        # Cleanup
        if os.path.exists(file_path):
            os.remove(file_path)

@router.get("/get-waveform/{filename}")
async def get_waveform(filename: str):
    # Ensure the file exists before sending
    if os.path.exists(filename):
        return FileResponse(filename)
    return {"status": "error", "message": "File not found"}
