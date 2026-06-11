<?php
/**
 * Authentication endpoint.
 *
 * Accepts a JSON body { "username": ..., "password": ... }, validates it on
 * the server, checks the credentials against the hashed user store, and on
 * success starts a session. Always responds with JSON.
 */

session_start();
header('Content-Type: application/json');

// Only POST is allowed here.
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['success' => false, 'message' => 'Method not allowed.']);
    exit;
}

// Read the request body. Support both JSON and standard form posts.
$raw = file_get_contents('php://input');
$payload = json_decode($raw, true);
if (!is_array($payload)) {
    $payload = $_POST;
}

$username = trim($payload['username'] ?? '');
$password = $payload['password'] ?? '';

// Server side validation. Never trust the client alone.
$errors = [];
if ($username === '') {
    $errors[] = 'Username is required.';
} elseif (strlen($username) < 3) {
    $errors[] = 'Username must be at least 3 characters.';
}
if ($password === '') {
    $errors[] = 'Password is required.';
} elseif (strlen($password) < 6) {
    $errors[] = 'Password must be at least 6 characters.';
}

if (!empty($errors)) {
    http_response_code(422);
    echo json_encode(['success' => false, 'message' => implode(' ', $errors)]);
    exit;
}

// Look up the user and verify the password against its stored hash.
$users = require __DIR__ . '/users.php';

if (isset($users[$username]) && password_verify($password, $users[$username])) {
    session_regenerate_id(true);
    $_SESSION['authenticated'] = true;
    $_SESSION['username'] = $username;

    echo json_encode([
        'success'  => true,
        'message'  => 'Login successful. Redirecting...',
        'redirect' => 'dashboard.php',
    ]);
    exit;
}

// Generic message so we do not reveal which field was wrong.
http_response_code(401);
echo json_encode(['success' => false, 'message' => 'Invalid username or password.']);
