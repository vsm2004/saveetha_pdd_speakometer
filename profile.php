<?php
// profile.php - Fetch user profile data for Speak-o-Meter
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');

include 'db_connect.php';

// Check if user_id is provided in the URL (?user_id=1)
if (!isset($_GET['user_id'])) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'user_id is required']);
    exit;
}

$user_id = (int)$_GET['user_id'];

try {
    // Select specific columns (Safety: Never select 'password')
    $stmt = $pdo->prepare("SELECT id, email, name, premium_status, premium_expiry, last_login 
                           FROM users WHERE id = ?");
    $stmt->execute([$user_id]);
    $user = $stmt->fetch();

    if (!$user) {
        http_response_code(404);
        echo json_encode(['status' => 'error', 'message' => 'User not found']);
        exit;
    }

    // Success response
    http_response_code(200);
    echo json_encode([
        'status' => 'success',
        'user' => $user
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Database error']);
}
?>