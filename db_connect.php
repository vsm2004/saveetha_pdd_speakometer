<?php
// db_connect.php - Simplified for Speak-o-Meter
$host     = 'localhost';
$dbname   = 'speakometer_backend'; 
$username = 'root';
$password = ''; 

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    
    // Set error mode to exception
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $pdo->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);

} catch (PDOException $e) {
    // Return a JSON error so Postman can read it
    header('Content-Type: application/json');
    die(json_encode(["status" => "error", "message" => "Database connection failed"]));
}
?>