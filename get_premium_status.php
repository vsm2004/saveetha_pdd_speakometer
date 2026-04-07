<?php
// get_premium_status.php - Check if a user has an active premium subscription
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');

include 'db_connect.php';

if (!isset($_GET['user_id'])) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'user_id is required']);
    exit;
}

$user_id = (int)$_GET['user_id'];

try {
    // Fetch only the columns needed for subscription logic
    $stmt = $pdo->prepare("SELECT premium_status, premium_expiry FROM users WHERE id = ?");
    $stmt->execute([$user_id]);
    $user = $stmt->fetch();

    if (!$user) {
        http_response_code(404);
        echo json_encode(['status' => 'error', 'message' => 'User not found']);
        exit;
    }

    // Logic: Is premium active AND has the expiry date NOT passed yet?
    $is_premium = (bool)$user['premium_status'];
    $expiry_date = $user['premium_expiry'];
    $current_date = date('Y-m-d');

    $is_active = false;
    if ($is_premium) {
        if ($expiry_date === null || $expiry_date >= $current_date) {
            $is_active = true;
        }
    }

    http_response_code(200);
    echo json_encode([
        'status' => 'success',
        'premium_status' => $is_premium,
        'premium_expiry' => $expiry_date,
        'is_active' => $is_active // This tells the Android app "Yes, let them in"
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Database error']);
}
?>