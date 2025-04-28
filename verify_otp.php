<?php
// ✅ Enable error reporting
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

require 'db.php';
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"), true);
$email = isset($data['email']) ? trim($data['email']) : '';
$otp = isset($data['otp']) ? trim($data['otp']) : '';

if (empty($email) || empty($otp)) {
    echo json_encode(["status" => "error", "message" => "Email and OTP are required"]);
    exit;
}

// Fetch OTP from DB
$stmt = $conn->prepare("SELECT otp, expiry FROM otpverification WHERE email = ?");
if (!$stmt) {
    echo json_encode(["status" => "error", "message" => "Prepare failed: " . $conn->error]);
    exit;
}

$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();

if (!$row) {
    echo json_encode(["status" => "error", "message" => "OTP not found"]);
    exit;
}

$storedOtp = $row['otp'];
$expiry = $row['expiry'];

if ($storedOtp != $otp) {
    echo json_encode(["status" => "error", "message" => "Invalid OTP"]);
    exit;
}

if (strtotime($expiry) < time()) {
    echo json_encode(["status" => "error", "message" => "OTP expired"]);
    exit;
}

// OTP is valid, delete it
$delStmt = $conn->prepare("DELETE FROM otpverification WHERE email = ?");
$delStmt->bind_param("s", $email);
$delStmt->execute();

echo json_encode(["status" => "success", "message" => "OTP verified successfully"]);
?>
