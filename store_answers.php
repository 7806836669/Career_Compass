<?php
error_reporting(0); // Hide notices in production
header("Content-Type: application/json");

require "db.php"; // ✅ Use your existing DB connection file

$data = json_decode(file_get_contents("php://input"), true);
if (!isset($data["answers"])) {
    echo json_encode(["error" => "Invalid input"]);
    exit;
}

$answers = $data["answers"];
$score = [];

foreach ($answers as $qid => $selected_option) {
    $qid = (int)$qid;
    $selected_option = strtolower($selected_option); // Convert to lowercase

    // Map options to field columns
    $field_column_map = [
        'a' => 'option_a_field',
        'b' => 'option_b_field',
        'c' => 'option_c_field',
        'd' => 'option_d_field'
    ];

    // Check if the selected_option is valid
    if (!isset($field_column_map[$selected_option])) {
        echo json_encode(["error" => "Invalid option selected"]);
        exit;
    }

    // Get the field column based on selected_option
    $field_column = $field_column_map[$selected_option];

    // SQL query to fetch the field based on selected option
    $sql = "SELECT $field_column AS field FROM questions WHERE id = $qid";
    $result = $conn->query($sql);

    if (!$result) {
        echo json_encode(["error" => "SQL Error", "details" => $conn->error]);
        exit;
    }

    if ($row = $result->fetch_assoc()) {
        $field = $row["field"];
        if (!empty($field)) {
            // Increment score for the selected field
            $score[$field] = isset($score[$field]) ? $score[$field] + 1 : 1;
        }
    }
}

// Sort scores to find the top field
arsort($score);
$top_field = key($score) ?? "General"; // Default to "General" if no field found

// Dummy college mapping
$college = match($top_field) {
    "Engineering" => "IIT Bombay",
    "Medicine" => "AIIMS Delhi",
    "Arts" => "NID Ahmedabad",
    "Commerce" => "IIM Ahmedabad",
    default => "General University"
};

// Return the response as JSON
echo json_encode([
    "message" => "Answers submitted successfully!",
    "recommended_field" => $top_field,
    "recommended_college" => $college
]);
?>
