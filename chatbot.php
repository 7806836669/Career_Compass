<?php
header("Content-Type: application/json");

// 1. Get the question from POST (form-data)
$question = $_POST["question"] ?? "";

if (!$question) {
    echo json_encode(["reply" => "Please enter a question."]);
    exit();
}

// 2. Use your real Cohere API key here from https://dashboard.cohere.com/api-keys
$apiKey = "3MRiDx3kVdFiJWuWOsMJNGUvqKaej9CYFNvhPtkZ"; // Replace this!

// 3. Setup cURL request to Cohere
$ch = curl_init();
$url = "https://api.cohere.ai/v1/generate";

$postData = json_encode([
    "model" => "command-nightly",
    "prompt" => $question,
    "max_tokens" => 100,
    "temperature" => 0.7
]);

curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, $postData);
curl_setopt($ch, CURLOPT_HTTPHEADER, [
    "Content-Type: application/json",
    "Authorization: Bearer $apiKey"
]);

$response = curl_exec($ch);

// 4. Error handling if cURL fails
if (curl_errno($ch)) {
    echo json_encode(["reply" => "Error: " . curl_error($ch)]);
    curl_close($ch);
    exit();
}
curl_close($ch);

// 5. Decode JSON and extract generated response
$result = json_decode($response, true);
$reply = $result["generations"][0]["text"] ?? "Sorry, no response from Cohere.";

// 6. Return the reply
echo json_encode(["reply" => trim($reply)]);
?>
