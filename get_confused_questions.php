<?php
header("Content-Type: application/json");

$conn = new mysqli("localhost", "root", "", "career_compass1");
if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Database connection failed"]));
}

if (!isset($_GET['field_name'])) {
    echo json_encode(["status" => "error", "message" => "Missing field_name parameter"]);
    exit();
}

$field_name = trim($_GET['field_name']);

$sql = "SELECT id, question, option_a, option_b, option_c, option_d FROM field_questions WHERE field_name = ? LIMIT 10";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $field_name);
$stmt->execute();
$result = $stmt->get_result();

$questions = [];
while ($row = $result->fetch_assoc()) {
    $questions[] = [
        "id" => $row["id"],
        "question" => $row["question"],
        "options" => [
            "A" => $row["option_a"],
            "B" => $row["option_b"],
            "C" => $row["option_c"],
            "D" => $row["option_d"]
        ]
    ];
}

$stmt->close();
$conn->close();

echo empty($questions)
    ? json_encode(["status" => "error", "message" => "No questions found for this field"])
    : json_encode(["status" => "success", "questions" => $questions]);
?>
