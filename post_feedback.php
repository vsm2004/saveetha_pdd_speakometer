<?php
header('Content-Type: application/json');
include 'db_connect.php'; // This provides the $pdo variable

// 1. Get data from POST (using ?? to avoid "Undefined Index" warnings)
$user_id   = $_POST['user_id'] ?? null;
$user_name = $_POST['user_name'] ?? 'Guest';
$message   = $_POST['message'] ?? '';
$rating    = $_POST['rating'] ?? 0;

if (!$user_id || !$message) {
    echo json_encode(["status" => "error", "message" => "User ID and Message are required"]);
    exit;
}

// 2. Send the message to your Python AI (Port 8001) for analysis
$ch = curl_init('http://localhost:8001/smart-feedback');
$payload = json_encode([
    "user_id" => (int)$user_id,
    "user_name" => $user_name,
    "message" => $message
]);

curl_setopt($ch, CURLOPT_POSTFIELDS, $payload);
curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type:application/json'));
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

$result = curl_exec($ch);

if ($result === false) {
    echo json_encode(["status" => "error", "message" => "Python Engine is not running on Port 8001"]);
    exit;
}

$ai_data = json_decode($result, true);

// 3. Extract results from Python's analysis
$category  = $ai_data['analysis']['category'] ?? 'General';
$priority  = $ai_data['analysis']['priority'] ?? 'Low';
$sentiment = $ai_data['analysis']['sentiment'] ?? 'Neutral';

try {
    // 4. Save to MySQL using PDO Prepared Statements
    // Notice we don't need mysqli_real_escape_string anymore!
    $sql = "INSERT INTO feedback (user_id, message, category, priority, sentiment, rating) 
            VALUES (:user_id, :message, :category, :priority, :sentiment, :rating)";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        ':user_id'   => $user_id,
        ':message'   => $message,
        ':category'  => $category,
        ':priority'  => $priority,
        ':sentiment' => $sentiment,
        ':rating'    => $rating
    ]);

    echo json_encode([
        "status" => "success",
        "message" => "Feedback saved with AI analysis",
        "ai_results" => $ai_data['analysis']
    ]);

} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Database Error: " . $e->getMessage()]);
}
?>