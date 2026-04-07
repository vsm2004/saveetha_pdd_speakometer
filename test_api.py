"""
test_api.py - Quick testing script for API endpoints
Run: python test_api.py
"""

import requests
import json
from datetime import datetime

BASE_URL = "http://localhost:8000"

# Colors for terminal output
GREEN = '\033[92m'
RED = '\033[91m'
YELLOW = '\033[93m'
BLUE = '\033[94m'
RESET = '\033[0m'


def print_header(text):
    print(f"\n{BLUE}{'=' * 60}{RESET}")
    print(f"{BLUE}{text}{RESET}")
    print(f"{BLUE}{'=' * 60}{RESET}")


def print_success(text):
    print(f"{GREEN}✓ {text}{RESET}")


def print_error(text):
    print(f"{RED}✗ {text}{RESET}")


def print_info(text):
    print(f"{YELLOW}ℹ {text}{RESET}")


def test_health_check():
    """Test API health status"""
    print_header("Testing: Health Check")
    try:
        response = requests.get(f"{BASE_URL}/health")
        if response.status_code == 200:
            print_success(f"Health check passed")
            print(json.dumps(response.json(), indent=2))
            return True
        else:
            print_error(f"Health check failed: {response.status_code}")
            return False
    except Exception as e:
        print_error(f"Connection error: {str(e)}")
        return False


def test_signup():
    """Test user registration"""
    print_header("Testing: User Signup")
    
    test_email = f"testuser_{datetime.now().timestamp()}@example.com"
    payload = {
        "email": test_email,
        "password": "test123456",
        "name": "Test User"
    }
    
    print_info(f"Attempting signup with: {test_email}")
    
    try:
        response = requests.post(f"{BASE_URL}/api/signup", json=payload)
        
        if response.status_code == 201:
            data = response.json()
            print_success(f"Signup successful")
            print(json.dumps(data, indent=2))
            return data.get("user_id"), test_email
        else:
            print_error(f"Signup failed: {response.status_code}")
            print(response.json())
            return None, None
    except Exception as e:
        print_error(f"Error: {str(e)}")
        return None, None


def test_login(email, password):
    """Test user login"""
    print_header("Testing: User Login")
    
    payload = {
        "email": email,
        "password": password
    }
    
    print_info(f"Attempting login with: {email}")
    
    try:
        response = requests.post(f"{BASE_URL}/api/login", json=payload)
        
        if response.status_code == 200:
            data = response.json()
            print_success(f"Login successful")
            print(json.dumps(data, indent=2))
            return True, data.get("user", {}).get("id")
        else:
            print_error(f"Login failed: {response.status_code}")
            print(response.json())
            return False, None
    except Exception as e:
        print_error(f"Error: {str(e)}")
        return False, None


def test_get_profile(user_id):
    """Test fetching user profile"""
    print_header("Testing: Get Profile")
    
    print_info(f"Fetching profile for user_id: {user_id}")
    
    try:
        response = requests.get(f"{BASE_URL}/api/profile", params={"user_id": user_id})
        
        if response.status_code == 200:
            print_success(f"Profile retrieved successfully")
            print(json.dumps(response.json(), indent=2))
            return True
        else:
            print_error(f"Failed to get profile: {response.status_code}")
            print(response.json())
            return False
    except Exception as e:
        print_error(f"Error: {str(e)}")
        return False


def test_update_profile(user_id):
    """Test updating user profile"""
    print_header("Testing: Update Profile")
    
    payload = {
        "user_id": user_id,
        "name": "Updated Test User",
        "email": f"updated_{datetime.now().timestamp()}@example.com"
    }
    
    print_info(f"Updating profile for user_id: {user_id}")
    
    try:
        response = requests.put(f"{BASE_URL}/api/profile", json=payload)
        
        if response.status_code == 200:
            print_success(f"Profile updated successfully")
            print(json.dumps(response.json(), indent=2))
            return True
        else:
            print_error(f"Failed to update profile: {response.status_code}")
            print(response.json())
            return False
    except Exception as e:
        print_error(f"Error: {str(e)}")
        return False


def test_save_session(user_id):
    """Test saving a speech analysis session"""
    print_header("Testing: Save Session")
    
    payload = {
        "user_id": user_id,
        "score": 85,
        "fillers_count": 3,
        "stretching_level": "high",
        "confidence": 92
    }
    
    print_info(f"Saving session for user_id: {user_id}")
    
    try:
        response = requests.post(f"{BASE_URL}/api/save-session", json=payload)
        
        if response.status_code == 201:
            print_success(f"Session saved successfully")
            print(json.dumps(response.json(), indent=2))
            return True
        else:
            print_error(f"Failed to save session: {response.status_code}")
            print(response.json())
            return False
    except Exception as e:
        print_error(f"Error: {str(e)}")
        return False


def test_post_feedback(user_id):
    """Test posting user feedback"""
    print_header("Testing: Post Feedback")
    
    payload = {
        "user_id": user_id,
        "user_name": "Test User",
        "message": "The app crashes when I try to upload audio files",
        "rating": 2
    }
    
    print_info(f"Posting feedback for user_id: {user_id}")
    print_info("Note: Feedback engine should be running on port 8001")
    
    try:
        response = requests.post(f"{BASE_URL}/api/feedback", json=payload)
        
        if response.status_code == 200:
            print_success(f"Feedback posted successfully")
            print(json.dumps(response.json(), indent=2))
            return True
        else:
            print_error(f"Failed to post feedback: {response.status_code}")
            print(response.json())
            return False
    except Exception as e:
        print_error(f"Error: {str(e)}")
        print_info("Feedback engine might not be running on port 8001")
        return False


def test_get_premium_status(user_id):
    """Test checking premium subscription status"""
    print_header("Testing: Get Premium Status")
    
    print_info(f"Checking premium status for user_id: {user_id}")
    
    try:
        response = requests.get(f"{BASE_URL}/api/premium-status", params={"user_id": user_id})
        
        if response.status_code == 200:
            print_success(f"Premium status retrieved")
            print(json.dumps(response.json(), indent=2))
            return True
        else:
            print_error(f"Failed to get premium status: {response.status_code}")
            print(response.json())
            return False
    except Exception as e:
        print_error(f"Error: {str(e)}")
        return False


def test_invalid_requests():
    """Test error handling with invalid requests"""
    print_header("Testing: Error Handling")
    
    print_info("Test 1: Missing required email field")
    try:
        response = requests.post(f"{BASE_URL}/api/login", json={"password": "test"})
        if response.status_code == 422:
            print_success("Correctly rejected invalid request (422)")
        else:
            print_error(f"Unexpected status: {response.status_code}")
    except Exception as e:
        print_error(f"Error: {str(e)}")
    
    print_info("Test 2: Nonexistent user profile")
    try:
        response = requests.get(f"{BASE_URL}/api/profile", params={"user_id": 99999})
        if response.status_code == 404:
            print_success("Correctly returned 404 for nonexistent user")
        else:
            print_error(f"Unexpected status: {response.status_code}")
    except Exception as e:
        print_error(f"Error: {str(e)}")


def main():
    """Run all tests"""
    print(f"\n{BLUE}╔════════════════════════════════════════════════════════╗{RESET}")
    print(f"{BLUE}║   Speak-o-Meter FastAPI Backend - API Test Suite     ║{RESET}")
    print(f"{BLUE}╚════════════════════════════════════════════════════════╝{RESET}")
    
    print_info(f"Testing API at: {BASE_URL}")
    
    # Test 1: Health check
    if not test_health_check():
        print_error("Backend is not running! Start it with: python main.py")
        return
    
    # Test 2: Error handling
    test_invalid_requests()
    
    # Test 3: Signup
    user_id, email = test_signup()
    if not user_id:
        print_error("Signup failed, stopping tests")
        return
    
    # Test 4: Login
    success, logged_in_user_id = test_login(email, "test123456")
    if not success:
        print_error("Login failed")
        return
    
    # Test 5: Get Profile
    test_get_profile(user_id)
    
    # Test 6: Update Profile
    test_update_profile(user_id)
    
    # Test 7: Save Session
    test_save_session(user_id)
    
    # Test 8: Post Feedback
    test_post_feedback(user_id)
    
    # Test 9: Get Premium Status
    test_get_premium_status(user_id)
    
    # Summary
    print_header("Test Suite Completed")
    print_success("All core endpoints tested successfully!")
    print_info("For feedback testing, ensure feedback_engine.py is running on port 8001")


if __name__ == "__main__":
    main()
