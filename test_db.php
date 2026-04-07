<?php
// test_db.php - Quick test for database connection

include 'db_connect.php';  // This loads the connection

echo "If you see this, the file is running!<br>";

try {
    // Simple query to confirm connection
    $stmt = $pdo->query("SELECT DATABASE() AS current_db");
    $row = $stmt->fetch();
    
    echo "Connected to database: <strong>" . $row['current_db'] . "</strong><br>";
    echo "Connection is working perfectly!";
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>