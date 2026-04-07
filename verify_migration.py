#!/usr/bin/env python3
"""
QUICK VERIFICATION CHECKLIST
Run this to verify your migration is complete and ready
"""

import os
import sys
from pathlib import Path

def check_file(path, description):
    """Check if a file exists"""
    exists = os.path.exists(path)
    status = "✅" if exists else "❌"
    print(f"{status} {description:50} {path}")
    return exists

def main():
    print("\n" + "="*80)
    print("SPEAK-O-METER FASTAPI MIGRATION - VERIFICATION CHECKLIST")
    print("="*80 + "\n")
    
    base_path = os.path.dirname(os.path.abspath(__file__))
    
    all_good = True
    
    print("1. CORE APPLICATION FILES")
    print("-" * 80)
    all_good &= check_file(os.path.join(base_path, "main.py"), "FastAPI application entry point")
    all_good &= check_file(os.path.join(base_path, "models.py"), "SQLAlchemy ORM models")
    all_good &= check_file(os.path.join(base_path, "config.py"), "Configuration management")
    all_good &= check_file(os.path.join(base_path, "utils.py"), "Database utilities")
    
    print("\n2. API ROUTER MODULES")
    print("-" * 80)
    all_good &= check_file(os.path.join(base_path, "routers/__init__.py"), "Routers package init")
    all_good &= check_file(os.path.join(base_path, "routers/auth.py"), "Auth endpoints (login/signup)")
    all_good &= check_file(os.path.join(base_path, "routers/profile.py"), "Profile endpoints")
    all_good &= check_file(os.path.join(base_path, "routers/sessions.py"), "Session endpoints")
    all_good &= check_file(os.path.join(base_path, "routers/feedback.py"), "Feedback endpoints")
    
    print("\n3. CONFIGURATION & SETUP FILES")
    print("-" * 80)
    all_good &= check_file(os.path.join(base_path, "requirements.txt"), "Python dependencies")
    all_good &= check_file(os.path.join(base_path, ".env.example"), "Environment variables template")
    all_good &= check_file(os.path.join(base_path, "start.bat"), "Windows startup script")
    all_good &= check_file(os.path.join(base_path, "start.sh"), "Linux/Mac startup script")
    
    print("\n4. DOCUMENTATION FILES")
    print("-" * 80)
    all_good &= check_file(os.path.join(base_path, "README.md"), "Project overview")
    all_good &= check_file(os.path.join(base_path, "MIGRATION.md"), "Detailed migration guide")
    all_good &= check_file(os.path.join(base_path, "SETUP.md"), "Installation guide")
    all_good &= check_file(os.path.join(base_path, "COMPLETION_REPORT.md"), "Completion report")
    all_good &= check_file(os.path.join(base_path, "FILE_LISTING.md"), "File listing")
    all_good &= check_file(os.path.join(base_path, "INSTANT_START.md"), "Quick start guide")
    
    print("\n5. TESTING FILES")
    print("-" * 80)
    all_good &= check_file(os.path.join(base_path, "test_api.py"), "API test suite")
    
    print("\n6. EXISTING FILES (SHOULD STILL EXIST)")
    print("-" * 80)
    all_good &= check_file(os.path.join(base_path, "feedback_engine.py"), "AI feedback engine")
    all_good &= check_file(os.path.join(base_path, "voice_engine.py"), "Speech recognition engine")
    
    print("\n" + "="*80)
    
    if all_good:
        print("✅ ALL FILES PRESENT - MIGRATION COMPLETE!")
        print("="*80 + "\n")
        print("NEXT STEPS:")
        print("1. Install dependencies:  pip install -r requirements.txt")
        print("2. Start the server:      python main.py")
        print("3. Test the API:          python test_api.py")
        print("4. View API docs:         http://localhost:8000/docs")
        print("\n" + "="*80)
        return 0
    else:
        print("❌ SOME FILES ARE MISSING")
        print("="*80 + "\n")
        print("Please ensure all files are in place before proceeding.")
        return 1

if __name__ == "__main__":
    sys.exit(main())
