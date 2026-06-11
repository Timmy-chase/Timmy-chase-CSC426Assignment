"use strict";

const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");
const loginBtn = document.getElementById("loginBtn");
const resetBtn = document.getElementById("resetBtn");
const toggleBtn = document.getElementById("togglePassword");
const alertBox = document.getElementById("alert");

// show/hide the password
toggleBtn.addEventListener("click", () => {
  const showing = passwordInput.type === "text";
  passwordInput.type = showing ? "password" : "text";
  toggleBtn.textContent = showing ? "Show" : "Hide";
});

resetBtn.addEventListener("click", () => {
  usernameInput.value = "";
  passwordInput.value = "";
  clearFieldErrors();
  hideAlert();
  usernameInput.focus();
});

// let Enter submit from either box
[usernameInput, passwordInput].forEach((el) =>
  el.addEventListener("keydown", (e) => {
    if (e.key === "Enter") handleLogin();
  })
);

loginBtn.addEventListener("click", handleLogin);

function showFieldError(field, message) {
  const input = document.getElementById(field);
  const slot = document.querySelector(`[data-error-for="${field}"]`);
  input.classList.add("invalid");
  if (slot) slot.textContent = message;
}

function clearFieldErrors() {
  document.querySelectorAll(".error").forEach((s) => (s.textContent = ""));
  document.querySelectorAll("input").forEach((i) => i.classList.remove("invalid"));
}

function showAlert(message, type) {
  alertBox.textContent = message;
  alertBox.className = `alert alert--${type}`;
  alertBox.hidden = false;
}

function hideAlert() {
  alertBox.hidden = true;
  alertBox.className = "alert";
}

function validate() {
  clearFieldErrors();
  let valid = true;
  const username = usernameInput.value.trim();
  const password = passwordInput.value;

  if (username === "") {
    showFieldError("username", "Username is required.");
    valid = false;
  } else if (username.length < 3) {
    showFieldError("username", "Username must be at least 3 characters.");
    valid = false;
  }

  if (password === "") {
    showFieldError("password", "Password is required.");
    valid = false;
  } else if (password.length < 6) {
    showFieldError("password", "Password must be at least 6 characters.");
    valid = false;
  }

  return valid;
}

async function handleLogin() {
  hideAlert();
  if (!validate()) return;

  loginBtn.disabled = true;
  loginBtn.textContent = "Signing in...";

  try {
    const res = await fetch("login.php", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        username: usernameInput.value.trim(),
        password: passwordInput.value,
      }),
    });

    const data = await res.json();

    if (data.success) {
      showAlert(data.message || "Login successful.", "success");
      setTimeout(() => (window.location.href = data.redirect || "dashboard.php"), 800);
    } else {
      showAlert(data.message || "Invalid username or password.", "error");
    }
  } catch (err) {
    // network or server is down
    showAlert("Could not reach the server. Please try again.", "error");
  } finally {
    loginBtn.disabled = false;
    loginBtn.textContent = "Log in";
  }
}
