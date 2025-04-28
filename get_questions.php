<?php
header("Content-Type: application/json");
$conn = new mysqli("localhost", "root", "", "career_compass1");

// Get 20 random questions
$result = $conn->query("SELECT id, question, option_a, option_b, option_c, option_d FROM questions ORDER BY id ASC LIMIT 20");

$questions = [];
while ($row = $result->fetch_assoc()) {
    $questions[] = [
        "id" => $row["id"],
        "question" => $row["question"],
        "options" => [
            "a" => $row["option_a"],
            "b" => $row["option_b"],
            "c" => $row["option_c"],
            "d" => $row["option_d"]
        ]
    ];
}
echo json_encode(["questions" => $questions]);
?>
