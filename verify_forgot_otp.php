<?php 
require 'db.php';

$data = json_decode(file_get_contents("php://input"), true);
$email = isset($data['email']) ? trim($data['email']) : '';
$otp = isset($data['otp']) ? trim($data['otp']) : '';

if (empty($email) || empty($otp)) {
    echo json_encode(["status" => "error", "message" => "All fields are required"]);
    exit;
}

// Check OTP in the database
$stmt = $conn->prepare("SELECT otp, expiry FROM otpverification WHERE email = ? ORDER BY id DESC LIMIT 1");
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();

if (!$row) {
    echo json_encode(["status" => "error", "message" => "OTP not found"]);
    exit;
}

// Validate OTP and Expiry
$stored_otp = $row['otp'];
$expiry_time = strtotime($row['expiry']);
$current_time = time();

if ($otp == $stored_otp && $current_time <= $expiry_time) {
    echo json_encode(["status" => "success", "message" => "OTP verified"]);
} else {
    echo json_encode(["status" => "error", "message" => "Invalid OTP or OTP expired"]);
}
?>
