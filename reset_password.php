<?php
require 'db.php';
require 'encrypt_decrypt.php'; // Your AES-256-CBC encryption functions

// Get JSON input
$data = json_decode(file_get_contents("php://input"), true);

// Extract and sanitize input
$email = isset($data['email']) ? trim($data['email']) : '';
$new_password = isset($data['new_password']) ? trim($data['new_password']) : '';

if (empty($email) || empty($new_password)) {
    echo json_encode(["status" => "error", "message" => "Email and new password are required!"]);
    exit;
}

// Encrypt the new password using your AES-256-CBC method
$encrypted_password = encrypt($new_password);

// Update the password in the database
$stmt = $conn->prepare("UPDATE users SET password = ? WHERE email = ?");
$stmt->bind_param("ss", $encrypted_password, $email);

if ($stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "Password reset successfully!"]);
} else {
    echo json_encode(["status" => "error", "message" => "Password reset failed!"]);
}

$stmt->close();
$conn->close();
?>
