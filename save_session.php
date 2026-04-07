<?php
// save-session.php - Save speech analysis session to the database
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['status' => 'error', 'message' => 'Method not allowed. Use POST.']);
    exit;
}

include 'db_connect.php';

// Read JSON from the request body
$data = json_decode(file_get_contents('php://input'), true);

// Check for the minimum required fields
if (!isset($data['user_id']) || !isset($data['score'])) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'user_id and score are required']);
    exit;
}

// Map the JSON data to your specific table columns
$user_id          = (int)$data['user_id'];
$score            = (int)$data['score']; // Database uses INT for score
$fillers_count    = isset($data['fillers_count']) ? (int)$data['fillers_count'] : 0;
$stretching_level = isset($data['stretching_level']) ? trim($data['stretching_level']) : 'none';
$confidence       = isset($data['confidence']) ? (int)$data['confidence'] : 0;

try {
    // 1. Verify user exists before inserting (Foreign Key safety)
    $checkUser = $pdo->prepare("SELECT id FROM users WHERE id = ?");
    $checkUser->execute([$user_id]);
    if (!$checkUser->fetch()) {
        http_response_code(404);
        echo json_encode(['status' => 'error', 'message' => 'User not found']);
        exit;
    }

    // 2. Insert into the 'sessions' table using your exact column names
    $sql = "INSERT INTO sessions (user_id, score, fillers_count, stretching_level, confidence) 
            VALUES (?, ?, ?, ?, ?)";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$user_id, $score, $fillers_count, $stretching_level, $confidence]);

    http_response_code(201);
    echo json_encode([
        'status' => 'success',
        'message' => 'Session saved successfully',
        'session_id' => $pdo->lastInsertId()
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    // Showing the full error message for development; hide $e->getMessage() in production
    echo json_encode(['status' => 'error', 'message' => 'Database error: ' . $e->getMessage()]);
}
?>