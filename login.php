<?php
require 'db.php';
require 'encrypt_decrypt.php';

// Get JSON input
$data = json_decode(file_get_contents("php://input"), true);

// Extract and sanitize input
$username = isset($data['name']) ? trim($data['name']) : '';
$password = isset($data['password']) ? trim($data['password']) : '';

if (empty($username) || empty($password)) {
    echo json_encode(["status" => "error", "message" => "All fields are required!"]);
    exit;
}

// Fetch user from DB
$stmt = $conn->prepare("SELECT * FROM users WHERE name = ?");
$stmt->bind_param("s", $username);
$stmt->execute();
$result = $stmt->get_result();
$user = $result->fetch_assoc();

if (!$user) {
    echo json_encode(["status" => "error", "message" => "User not found!"]);
    exit;
}

// Decrypt stored encrypted password
$storedEncryptedPassword = $user['password'];
$decryptedPassword = decrypt($storedEncryptedPassword);

// Compare input password with decrypted one
if ($password === $decryptedPassword) {
    echo json_encode(["status" => "success", "message" => "Login successful!"]);
} else {
    echo json_encode(["status" => "error", "message" => "Invalid password!"]);
}
?>
