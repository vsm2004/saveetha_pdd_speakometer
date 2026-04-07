from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import uvicorn

app = FastAPI(title="Speak-o-Meter Feedback AI")

# Data model for the incoming request
class FeedbackRequest(BaseModel):
    user_id: int
    user_name: str
    message: str

@app.post("/smart-feedback")
async def analyze_feedback(data: FeedbackRequest):
    msg = data.message.lower()
    
    # 1. NLP Keyword Extraction (Filtering out "noise" words)
    # We grab words longer than 3 letters to find the "meat" of the feedback
    words = [word.strip(",.!") for word in data.message.split() if len(word) > 3]
    
    # 2. Automated Categorization Logic
    category = "General/Suggestion"
    priority = "Low"
    
    # Technical Logic
    if any(word in msg for word in ["slow", "lag", "crash", "bug", "hang", "freeze"]):
        category = "Technical Issue"
        priority = "High"
    
    # AI Accuracy Logic
    elif any(word in msg for word in ["accurate", "wrong", "text", "mistake", "transcription"]):
        category = "AI Accuracy"
        priority = "Medium"
        
    # UI/UX Logic
    elif any(word in msg for word in ["color", "button", "look", "ui", "design"]):
        category = "UI/UX Design"
        priority = "Low"

    # 3. Sentiment Detection (Basic Heuristics)
    is_negative = any(word in msg for word in ["bad", "worst", "hate", "poor", "difficult"])
    sentiment = "Negative" if is_negative else "Positive/Neutral"

    # This is the "Developer Report" that would eventually go to your PHP/MySQL
    return {
        "status": "success",
        "analysis": {
            "user_id": data.user_id,
            "user_name": data.user_name,
            "category": category,
            "priority": priority,
            "sentiment": sentiment,
            "key_keywords": words[:5], # The 5 most important words
            "internal_note": f"System flag: {priority} priority {category} reported by {data.user_name}."
        }
    }

if __name__ == "__main__":
    # We run this on Port 8001 so it doesn't conflict with the Speech Engine (Port 8000)
    uvicorn.run(app, host="0.0.0.0", port=8001)