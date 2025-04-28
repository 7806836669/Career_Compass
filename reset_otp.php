<?php
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require 'vendor/autoload.php';
require 'db.php';

$data = json_decode(file_get_contents("php://input"), true);
$email = isset($data['email']) ? $data['email'] : '';

if (empty($email)) {
    echo json_encode(["status" => "error", "message" => "Email is required"]);
    exit;
}

// Check if email exists
$stmt = $conn->prepare("SELECT id FROM users WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows == 0) {
    echo json_encode(["status" => "error", "message" => "Email not registered"]);
    exit;
}

// Generate OTP
$otp = rand(100000, 999999);
$expiry = date("Y-m-d H:i:s", strtotime("+10 minutes"));

// Insert OTP into database
$stmt = $conn->prepare("INSERT INTO otpverification (email, otp, expiry) VALUES (?, ?, ?)");
$stmt->bind_param("sss", $email, $otp, $expiry);
$stmt->execute();

// Send OTP via Email
$mail = new PHPMailer(true);
try {
    $mail->isSMTP();
    $mail->Host = 'smtp.gmail.com';
    $mail->SMTPAuth = true;
    $mail->Username = 'dsjoker15@gmail.com';
    $mail->Password = 'jhxa ijqe izia tjnn';
    $mail->SMTPSecure = 'tls';
    $mail->Port = 587;

    $mail->setFrom('dsjoker15@gmail.com', 'Career Compass');
    $mail->addAddress($email);
    $mail->isHTML(true);
    $mail->Subject = 'Password Reset OTP';
    $mail->Body = "<p>Your OTP for password reset is <strong>$otp</strong>. It is valid for 10 minutes.</p>";

    $mail->send();
    echo json_encode(["status" => "success", "message" => "OTP sent"]);
} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => "Mailer Error: " . $mail->ErrorInfo]);
}
?>
