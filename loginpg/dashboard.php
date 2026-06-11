<?php
session_start();

// keep this page private: bounce anyone without a live session
if (empty($_SESSION['authenticated'])) {
    header('Location: index.html');
    exit;
}

$username = htmlspecialchars($_SESSION['username'] ?? 'user', ENT_QUOTES, 'UTF-8');
?>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>CSC426 Portal | Dashboard</title>
  <link rel="stylesheet" href="style.css" />
  <style>
    .topbar {
      background: var(--teal);
      color: #eef6f5;
      padding: 14px 26px;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .topbar a { color: #cde7e3; text-decoration: none; font-weight: 600; font-size: 0.9rem; }
    .topbar a:hover { color: #fff; }
    .topbar .logo { font-weight: 700; font-size: 1.1rem; color: #fff; }
    .topbar .logo span { color: #b9ded9; font-weight: 400; }
    .welcome {
      max-width: 640px;
      margin: 64px auto;
      background: #fff;
      border: 1px solid var(--border);
      border-top: 4px solid var(--teal);
      border-radius: 8px;
      padding: 34px 32px;
    }
    .welcome h1 { font-size: 1.6rem; font-weight: 600; }
    .welcome p { color: var(--muted); margin-top: 8px; line-height: 1.6; }
  </style>
</head>
<body>
  <header class="topbar">
    <span class="logo">CSC426<span>Portal</span></span>
    <a href="logout.php">Log out</a>
  </header>

  <section class="welcome">
    <h1>Hello, <?= $username ?></h1>
    <p>You are signed in. This page is protected by a server side session, so it can only be reached after a successful login.</p>
  </section>
</body>
</html>
