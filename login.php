<?php
// login.php - User login endpoint for Speak-o-Meter backend

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');           // Allow Android app (restrict in production)
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['status' => 'error', 'message' => 'Method not allowed. Use POST.']);
    exit;
}

include 'db_connect.php';  // Loads $pdo

// Read JSON from request body (Android sends JSON)
$data = json_decode(file_get_contents('php://input'), true);

// Required fields
if (!isset($data['email']) || !isset($data['password'])) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Email and password are required']);
    exit;
}

$email    = trim($data['email']);
$password = $data['password'];

// Basic validation
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Invalid email format']);
    exit;
}

// Find user
try {
    $stmt = $pdo->prepare("SELECT id, email, password, name, premium_status, premium_expiry 
                           FROM users WHERE email = ?");
    $stmt->execute([$email]);
    $user = $stmt->fetch();

    if (!$user) {
        http_response_code(401);
        echo json_encode(['status' => 'error', 'message' => 'Invalid email or password']);
        exit;
    }

    // Verify password
    if (!password_verify($password, $user['password'])) {
        http_response_code(401);
        echo json_encode(['status' => 'error', 'message' => 'Invalid email or password']);
        exit;
    }

    // Remove password from response (security)
    unset($user['password']);

    // Optional: Update last login time
    $stmt = $pdo->prepare("UPDATE users SET last_login = NOW() WHERE id = ?");
    $stmt->execute([$user['id']]);

    http_response_code(200);
    echo json_encode([
        'status'  => 'success',
        'message' => 'Login successful',
        'user'    => $user
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Database error']);
    exit;
}
?>