<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

// Database connection
$conn = new mysqli("localhost", "root", "", "career_compass");

// Check for errors
if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Database connection failed"]));
}

// Get user ID from request (sent from frontend)
$user_id = isset($_GET['user_id']) ? intval($_GET['user_id']) : 0;

if ($user_id <= 0) {
    echo json_encode(["status" => "error", "message" => "Invalid user ID"]);
    exit();
}

// Fetch user's suggested field
$user_query = "SELECT field_suggested FROM user_answers WHERE user_id = ?";
$stmt = $conn->prepare($user_query);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();
$user_data = $result->fetch_assoc();

if (!$user_data) {
    echo json_encode(["status" => "error", "message" => "User field not found"]);
    exit();
}

$field = $user_data['field_suggested'];

// Fetch confusion questions for the suggested field
$sql = "SELECT id, question, option_a, option_b, option_c, option_d FROM field_confusion_questions WHERE field = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $field);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $questions = [];
    while ($row = $result->fetch_assoc()) {
        $questions[] = $row;
    }
    echo json_encode(["status" => "success", "questions" => $questions]);
} else {
    echo json_encode(["status" => "error", "message" => "No confusion questions found for this field"]);
}

// Close connection
$conn->close();
?>
