<?php
header("Content-Type: application/json");

$conn = new mysqli("localhost", "root", "", "career_compass1");
if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Database connection failed"]));
}

$data = json_decode(file_get_contents("php://input"), true);

if (!isset($data["user_name"]) || !isset($data["field_name"]) || !isset($data["answers"])) {
    echo json_encode(["status" => "error", "message" => "Missing user_name, field_name, or answers"]);
    exit();
}

$user_id = trim($data["user_name"]);
$field_name = trim($data["field_name"]);
$user_answers = $data["answers"]; // Associative array: question_id => selected_option

$sql = "SELECT id, correct_option FROM field_questions WHERE field_name = ? LIMIT 10";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $field_name);
$stmt->execute();
$result = $stmt->get_result();

$correct_answers = [];
while ($row = $result->fetch_assoc()) {
    $correct_answers[$row["id"]] = strtoupper($row["correct_option"]);
}
$stmt->close();

$correct_count = 0;

// Prepare statement to insert individual answers
$insert_answer_sql = "INSERT INTO user_field_answers (user_name, field_name, question_id, selected_option) VALUES (?, ?, ?, ?)";
$insert_stmt = $conn->prepare($insert_answer_sql);

// Save each answer
foreach ($user_answers as $question_id => $selected_option) {
    $selected_option_upper = strtoupper(trim($selected_option));
    
    if (isset($correct_answers[$question_id]) && $selected_option_upper === $correct_answers[$question_id]) {
        $correct_count++;
    }

    // Insert answer into DB
    $insert_stmt->bind_param("ssis", $user_id, $field_name, $question_id, $selected_option_upper);
    $insert_stmt->execute();
}

$insert_stmt->close();

$total_questions = count($correct_answers);
$accuracy = ($total_questions > 0) ? ($correct_count / $total_questions) * 100 : 0;
$rounded_accuracy = round($accuracy, 2);
$message = ($accuracy >= 75) ? "You are truly interested in this field!" : "You might need to explore further.";

// Return result
echo json_encode([
    "status" => "success",
    "user_name" => $user_id,
    "field_name" => $field_name,
    "correct_answers" => $correct_count,
    "total_questions" => $total_questions,
    "accuracy" => $rounded_accuracy . "%",
    "message" => $message
]);

$conn->close();
?>
