<?php
// update_profile.php - Update user profile data
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: PUT');
header('Access-Control-Allow-Headers: Content-Type');

include 'db_connect.php';

// Only allow PUT requests
if ($_SERVER['REQUEST_METHOD'] !== 'PUT') {
    http_response_code(405);
    echo json_encode(['status' => 'error', 'message' => 'Method not allowed. Use PUT.']);
    exit;
}

// Read the JSON body
$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['user_id'])) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'user_id is required']);
    exit;
}

$user_id = (int)$data['user_id'];
$new_name = isset($data['name']) ? trim($data['name']) : null;
$new_email = isset($data['email']) ? trim($data['email']) : null;

try {
    // 1. Check if user exists
    $check = $pdo->prepare("SELECT id FROM users WHERE id = ?");
    $check->execute([$user_id]);
    if (!$check->fetch()) {
        http_response_code(404);
        echo json_encode(['status' => 'error', 'message' => 'User not found']);
        exit;
    }

    // 2. Update the record (Only update fields that are provided)
    $sql = "UPDATE users SET name = COALESCE(?, name), email = COALESCE(?, email) WHERE id = ?";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$new_name, $new_email, $user_id]);

    http_response_code(200);
    echo json_encode([
        'status' => 'success',
        'message' => 'Profile updated successfully'
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Database error: ' . $e->getMessage()]);
}
?>