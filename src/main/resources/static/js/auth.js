const TOKEN_KEY = "blackjack_token";
const USERNAME_KEY = "blackjack_username";

function saveSession(token, username) {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USERNAME_KEY, username);
}

function clearSession() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USERNAME_KEY);
}

function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function getUsername() {
    return localStorage.getItem(USERNAME_KEY);
}

function isLoggedIn() {
    return !!getToken();
}

function authFetch(url, options = {}) {
    const headers = Object.assign({}, options.headers, {
        Authorization: `Bearer ${getToken()}`
    });
    return fetch(url, Object.assign({}, options, { headers }));
}

function formatCurrency(amount) {
    return `${Number(amount).toLocaleString("sv-SE", { minimumFractionDigits: 2, maximumFractionDigits: 2 })} kr`;
}

function renderNav(activePage) {
    const nav = document.getElementById("mainNav");
    if (!nav) return;

    const loggedIn = isLoggedIn();

    nav.innerHTML = `
        <a href="/" class="nav-link ${activePage === "game" ? "active" : ""}">Spel</a>
        <a href="/highscore.html" class="nav-link ${activePage === "highscore" ? "active" : ""}">Highscore</a>
        ${loggedIn ? `<a href="/admin.html" class="nav-link ${activePage === "admin" ? "active" : ""}">Admin</a>` : ""}
        <span class="nav-spacer"></span>
        ${loggedIn
        ? `<span class="nav-user">${getUsername()}</span><button class="ghost small" onclick="handleNavLogout()">Logga ut</button>`
        : `<a href="/" class="nav-link">Logga in</a>`
    }
    `;
}

function handleNavLogout() {
    clearSession();
    window.location.href = "/";
}