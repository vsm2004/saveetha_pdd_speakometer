import whisper
import librosa
import librosa.display
import matplotlib.pyplot as plt
import numpy as np
import os
from fastapi import FastAPI, UploadFile, File
from fastapi.responses import FileResponse

# --- FFmpeg Path Configuration ---
# Ensure this points to your specific WinGet/Links folder
ffmpeg_dir = r"C:\Users\DELL'\AppData\Local\Microsoft\WinGet\Links"
os.environ["PATH"] += os.pathsep + ffmpeg_dir

app = FastAPI()

# Load the AI model (using 'base' for a good balance of speed/accuracy)
print("Loading AI Model... please wait.")
model = whisper.load_model("base")

@app.post("/analyze")
async def analyze_speech(file: UploadFile = File(...)):
    file_path = f"temp_{file.filename}"
    waveform_path = f"waveform_{file.filename}.png"
    
    try:
        # Save the incoming file
        contents = await file.read()
        with open(file_path, "wb") as f:
            f.write(contents)

        # 1. Transcription & WPM
        result = model.transcribe(file_path)
        text = result.get('text', "")
        words = text.split()
        
        # 2. Audio Processing with Librosa
        y, sr = librosa.load(file_path)
        
        # --- IMPROVEMENT: Silence Trimming ---
        # Trim leading and trailing silence for more accurate WPM
        y_trimmed, _ = librosa.effects.trim(y, top_db=20)
        duration_sec = librosa.get_duration(y=y_trimmed, sr=sr)
        duration_min = duration_sec / 60
        wpm = len(words) / duration_min if duration_min > 0 else 0

        # 3. Tone & Expressiveness Analysis (Pitch Tracking)
        pitches, magnitudes = librosa.piptrack(y=y, sr=sr)
        valid_pitches = pitches[pitches > 0]
        pitch_std = np.std(valid_pitches) if len(valid_pitches) > 0 else 0
        
        if pitch_std > 50:
            tone = "Confident & Expressive"
        elif pitch_std < 20:
            tone = "Monotone / Nervous"
        else:
            tone = "Steady / Neutral"

        # 4. Visual Waveform Generation (Using full audio y)
        plt.figure(figsize=(10, 4))
        plt.axis('off') 
        librosa.display.waveshow(y, sr=sr, color='#6200EE') 
        plt.savefig(waveform_path, transparent=True, bbox_inches='tight', pad_inches=0)
        plt.close()

        # 5. Accent Comparison (MFCC & Spectral Contrast)
        mfccs = librosa.feature.mfcc(y=y, sr=sr, n_mfcc=13)
        spectral_contrast = librosa.feature.spectral_contrast(y=y, sr=sr)
        clarity_value = np.mean(spectral_contrast)
        accent_score = min(98.5, 70 + (clarity_value * 1.5))

        # 6. Advanced Metrics: Fillers, Variety, and Pacing
        words_lower = [w.lower().strip(",.?!") for w in words]
        
        # Filler detection
        fillers = ["um", "uh", "ah", "like", "basically", "actually"]
        filler_count = sum(1 for word in words_lower if word in fillers)
        
        # Variety detection (Human-like judging)
        unique_words = set(words_lower)
        variety_ratio = len(unique_words) / len(words_lower) if words_lower else 1.0
        
        # --- Scoring Logic (Human-Like Calibration) ---
        score = 100
        
        # Penalty 1: Fillers
        score -= (filler_count * 8)
        
        # Penalty 2: Repetition / Lack of Variety
        if variety_ratio < 0.6 and len(words_lower) > 3:
            variety_penalty = (0.6 - variety_ratio) * 100
            score -= variety_penalty
            
        # Penalty 3: Pacing (Ideal WPM is 110-160)
        if wpm < 100:
            pacing_penalty = min(30, (100 - wpm) * 0.5)
            score -= pacing_penalty
        elif wpm > 180:
            pacing_penalty = min(20, (wpm - 180) * 0.3)
            score -= pacing_penalty
            
        # Penalty 4: Expressiveness
        if pitch_std < 25:
            monotone_penalty = (25 - pitch_std) * 1.5
            score -= monotone_penalty

        final_score = max(0, min(100, round(score)))

        return {
            "status": "success",
            "transcription": text.strip(),
            "wpm": round(wpm, 1),
            "tone_analysis": tone,
            "accent_score": round(accent_score, 1),
            "filler_count": filler_count,
            "variety_score": round(variety_ratio * 100, 1),
            "waveform_url": f"http://localhost:8000/get-waveform/{waveform_path}",
            "confidence_score": final_score
        }

    except Exception as e:
        print(f"Error: {e}")
        return {"status": "error", "message": str(e)}

    finally:
        # Cleanup: Delete the temp audio file (keep the image for the URL to work)
        if os.path.exists(file_path):
            os.remove(file_path)

@app.get("/get-waveform/{filename}")
async def get_waveform(filename: str):
    # Ensure the file exists before sending
    if os.path.exists(filename):
        return FileResponse(filename)
    return {"status": "error", "message": "File not found"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)