@echo off
REM Windows batch script to start the FastAPI backend

echo ========================================
echo Speak-o-Meter FastAPI Backend Startup
echo ========================================
echo.

REM Check if Python is installed
python --version >nul 2>&1
if errorlevel 1 (
    echo Error: Python is not installed or not in PATH
    pause
    exit /b 1
)

echo [1/3] Python found. Checking dependencies...
pip show fastapi >nul 2>&1
if errorlevel 1 (
    echo.
    echo Installing dependencies from requirements.txt...
    pip install -r requirements.txt
    if errorlevel 1 (
        echo Error: Failed to install dependencies
        pause
        exit /b 1
    )
)

echo [2/3] Dependencies verified.
echo [3/3] Starting FastAPI server...
echo.
echo Server will start on: http://localhost:8000
echo API Documentation: http://localhost:8000/docs
echo.

REM Start the FastAPI server
python main.py

pause
