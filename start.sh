#!/bin/bash
# Unix/Linux startup script for the FastAPI backend

echo "========================================"
echo "Speak-o-Meter FastAPI Backend Startup"
echo "========================================"
echo ""

# Check if Python is installed
if ! command -v python3 &> /dev/null; then
    echo "Error: Python 3 is not installed"
    exit 1
fi

echo "[1/3] Python found. Checking dependencies..."

# Check if requirements are installed
if ! pip3 show fastapi &> /dev/null; then
    echo ""
    echo "Installing dependencies from requirements.txt..."
    pip3 install -r requirements.txt
    if [ $? -ne 0 ]; then
        echo "Error: Failed to install dependencies"
        exit 1
    fi
fi

echo "[2/3] Dependencies verified."
echo "[3/3] Starting FastAPI server..."
echo ""
echo "Server will start on: http://localhost:8000"
echo "API Documentation: http://localhost:8000/docs"
echo ""

# Start the FastAPI server
python3 main.py
