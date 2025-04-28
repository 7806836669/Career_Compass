<?php
include 'db_config.php'; // Database connection

header("Content-Type: application/json");

// Get user input
$data = json_decode(file_get_contents("php://input"));

if (!isset($data->field)) {
    echo json_encode(["status" => "error", "message" => "Field is required"]);
    exit;
}

$field = $data->field;

// Fetch colleges based on the field
$query = "SELECT college_name, location, website FROM colleges WHERE field = ?";
$stmt = $conn->prepare($query);
$stmt->bind_param("s", $field);
$stmt->execute();
$result = $stmt->get_result();

$colleges = [];
while ($row = $result->fetch_assoc()) {
    $colleges[] = $row;
}

if (empty($colleges)) {
    echo json_encode(["status" => "error", "message" => "No colleges found for this field."]);
} else {
    echo json_encode(["status" => "success", "colleges" => $colleges]);
}

$stmt->close();
$conn->close();
?>
