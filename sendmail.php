<?php
// ✅ Enable error reporting for debugging
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require 'vendor/autoload.php';
require 'db.php';

header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"), true);
$email = isset($data['email']) ? trim($data['email']) : '';

if (empty($email)) {
    echo json_encode(["status" => "error", "message" => "Email is required"]);
    exit;
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo json_encode(["status" => "error", "message" => "Invalid email format"]);
    exit;
}

// Generate OTP
$otp = rand(100000, 999999);
$expiry = date("Y-m-d H:i:s", strtotime("+10 minutes"));

// Remove existing OTPs
$deleteStmt = $conn->prepare("DELETE FROM otpverification WHERE email = ?");
$deleteStmt->bind_param("s", $email);
$deleteStmt->execute();

// Insert new OTP
$stmt = $conn->prepare("INSERT INTO otpverification (email, otp, expiry) VALUES (?, ?, ?)");
if (!$stmt) {
    echo json_encode(["status" => "error", "message" => "Prepare failed: " . $conn->error]);
    exit;
}
$stmt->bind_param("sss", $email, $otp, $expiry);
$stmt->execute();

// Send OTP via Email
$mail = new PHPMailer(true);

try {
    $mail->isSMTP();
    $mail->Host = 'smtp.gmail.com';
    $mail->SMTPAuth = true;
    $mail->Username = 'alexander1973leo@gmail.com';  // ✅ Your Gmail address
    $mail->Password = 'jgwo apnn qcek hkjj';         // ✅ Gmail App Password
    $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
    $mail->Port = 587;

    $mail->setFrom('alexander1973leo@gmail.com', 'Career Compass');
    $mail->addAddress($email);
    $mail->isHTML(true);
    $mail->Subject = 'Your OTP for Career Compass Verification';
    $mail->Body = "<p>Your OTP is: <strong>$otp</strong></p><p>Do not share this OTP.</p>";

    $mail->send();
    echo json_encode(["status" => "success", "message" => "OTP sent successfully"]);
} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => "Mailer Error: " . $mail->ErrorInfo]);
}
?>
