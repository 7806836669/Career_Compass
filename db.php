<?php
$servername = "localhost";
$username = "root"; // Change if different
$password = ""; // Change if your database has a password
$database = "career_compass1";

$conn = new mysqli($servername, $username, $password, $database);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 
// else {
//     echo "Database connected successfully!";
// }
?>
