<?php
header('Content-Type: application/json');

// Connect to database
$conn = new mysqli("localhost", "root", "", "career_compass1");

if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Connection failed: " . $conn->connect_error]));
}

// Get the JSON data from the request
$data = json_decode(file_get_contents("php://input"), true);

// Validate input
if (!isset($data['name']) || !isset($data['gender']) || !isset($data['category'])) {
    echo json_encode(["status" => "error", "message" => "Missing parameters"]);
    exit();
}

$name = $conn->real_escape_string($data['name']);
$gender = $conn->real_escape_string($data['gender']);
$category = $conn->real_escape_string($data['category']);

// Insert into database
$sql = "INSERT INTO user_profile (name, gender, category) VALUES ('$name', '$gender', '$category')";

if ($conn->query($sql) === TRUE) {
    echo json_encode(["status" => "success", "message" => "Profile saved"]);
} else {
    echo json_encode(["status" => "error", "message" => "Database error: " . $conn->error]);
}

$conn->close();
?>
