<?php
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require "C:\xampp\htdocs\career_compass\vendor\phpmailer\phpmailer";

$mail = new PHPMailer(true);

try {
    // Server settings
    $mail->isSMTP();
    $mail->Host       = 'smtp.example.com'; // Replace with your SMTP server
    $mail->SMTPAuth   = true;
    $mail->Username   = 'alexander1973leo@gmail.com'; // Replace with your email
    $mail->Password   = 'alexander1973'; // Replace with your email password
    $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
    $mail->Port       = 587;

    // Recipients
    $mail->setFrom('alexander1973leo@gmail.com', 'Your Name');
    $mail->addAddress('sruthirosilinea1015.sse@saveetha.com'); // Recipient email

    // Content
    $mail->isHTML(true);
    $mail->Subject = 'Test Email from PHPMailer';
    $mail->Body    = '<h3>This is a test email sent using PHPMailer.</h3>';

    $mail->send();
    echo 'Email has been sent successfully!';
} catch (Exception $e) {
    echo "Email could not be sent. Mailer Error: {$mail->ErrorInfo}";
}
?>
