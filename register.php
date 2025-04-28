<?php
require 'db.php';
require 'encrypt_decrypt.php';

// Get JSON input
$data = json_decode(file_get_contents("php://input"), true);

// Sanitize inputs
$name = isset($data['name']) ? trim($data['name']) : '';
$age = isset($data['age']) ? (int)$data['age'] : 0;
$email = isset($data['email']) ? trim($data['email']) : '';
$password = isset($data['password']) ? trim($data['password']) : '';

// Validate required fields
if (empty($name) || empty($age) || empty($email) || empty($password)) {
    echo json_encode(["status" => "error", "message" => "All fields are required!"]);
    exit;
}

// Encrypt password
$encryptedPassword = encrypt($password);

// Insert into database
$stmt = $conn->prepare("INSERT INTO users (name, age, email, password) VALUES (?, ?, ?, ?)");
$stmt->bind_param("siss", $name, $age, $email, $encryptedPassword);

if ($stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "User registered successfully!"]);
} else {
    echo json_encode(["status" => "error", "message" => "Registration failed! Email or name may already exist."]);
}
?>
