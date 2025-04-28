<?php 
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require 'vendor/autoload.php';
require 'db.php';

// Get JSON Input
$data = json_decode(file_get_contents("php://input"), true);
$email = isset($data['email']) ? trim($data['email']) : '';

if (empty($email)) {
    echo json_encode(["status" => "error", "message" => "Email is required"]);
    exit;
}

// Validate Email
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo json_encode(["status" => "error", "message" => "Invalid email format"]);
    exit;
}

// Check if email exists in Users table
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

// Remove any old OTPs for this email
$conn->prepare("DELETE FROM otpverification WHERE email = ?")->execute([$email]);

// Insert new OTP into Database
$stmt = $conn->prepare("INSERT INTO otpverification (email, otp, expiry) VALUES (?, ?, ?)");
$stmt->bind_param("sss", $email, $otp, $expiry);
$stmt->execute();

// Send OTP via Email
$mail = new PHPMailer(true);
try {
    $mail->isSMTP();
    $mail->Host = 'smtp.gmail.com';
    $mail->SMTPAuth = true;
    $mail->Username = 'alexander1973leo@gmail.com';  // Change this
    $mail->Password = 'jgwo apnn qcek hkjj';  // Change this
    $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
    $mail->Port = 587;

    $mail->setFrom('alexander1973leo@gmail.com', 'Career Compass');
    $mail->addAddress($email);

    $mail->isHTML(true);
    $mail->Subject = 'Career Compass - Password Reset OTP';
    $mail->Body = "
        <h2>Reset Your Password</h2>
        <p>Your OTP is: <strong style='font-size:18px;'>$otp</strong></p>
        <p><b>Do not share this OTP with anyone.</b></p>
        <p><i>This OTP will expire in 10 minutes.</i></p>
    ";

    $mail->send();
    echo json_encode(["status" => "success", "message" => "OTP sent successfully"]);
} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => "Mailer Error: " . $mail->ErrorInfo]);
}
?>
