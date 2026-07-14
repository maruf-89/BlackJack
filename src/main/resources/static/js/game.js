let authToken = null;
let currentUsername = null;
let gameId = null;
let selectedBet = 10;

function initChips() {
    document.querySelectorAll(".chip").forEach(chip => {
        chip.addEventListener("click", () => {
            document.querySelectorAll(".chip").forEach(c => c.classList.remove("selected"));
            chip.classList.add("selected");
            selectedBet = Number(chip.dataset.val);
            document.getElementById("bet").value = selectedBet;
        });
    });

    document.getElementById("bet").addEventListener("input", e => {
        document.querySelectorAll(".chip").forEach(c => c.classList.remove("selected"));
        const match = [...document.querySelectorAll(".chip")].find(c => Number(c.dataset.val) === Number(e.target.value));
        if (match) match.classList.add("selected");
    });
}

function initAuthTabs() {
    document.getElementById("loginTab").addEventListener("click", () => switchAuthMode("login"));
    document.getElementById("registerTab").addEventListener("click", () => switchAuthMode("register"));
}

function switchAuthMode(mode) {
    const isLogin = mode === "login";
    document.getElementById("loginTab").classList.toggle("active", isLogin);
    document.getElementById("registerTab").classList.toggle("active", !isLogin);
    document.getElementById("emailField").style.display = isLogin ? "none" : "flex";
    document.getElementById("authSubmitBtn").innerText = isLogin ? "Logga in" : "Skapa konto";
    document.getElementById("authSubmitBtn").dataset.mode = mode;
    clearAuthError();
}

function clearAuthError() {
    const el = document.getElementById("authError");
    el.innerText = "";
    el.style.display = "none";
}

function showAuthError(message) {
    const el = document.getElementById("authError");
    el.innerText = message;
    el.style.display = "block";
}

function submitAuth() {
    const mode = document.getElementById("authSubmitBtn").dataset.mode || "login";
    const username = document.getElementById("authUsername").value.trim();
    const password = document.getElementById("authPassword").value;

    if (!username || !password) {
        showAuthError("Fyll i användarnamn och lösenord.");
        return;
    }

    clearAuthError();
    document.getElementById("authSubmitBtn").disabled = true;

    if (mode === "register") {
        const email = document.getElementById("authEmail").value.trim();
        if (!email) {
            showAuthError("Fyll i e-post.");
            document.getElementById("authSubmitBtn").disabled = false;
            return;
        }

        fetch("/api/users/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, email, password })
        })
            .then(r => {
                if (!r.ok) throw new Error(`Registrering misslyckades (${r.status})`);
                return r.json();
            })
            .then(() => login(username, password))
            .catch(err => {
                console.error("submitAuth() register misslyckades:", err);
                showAuthError(err.message);
                document.getElementById("authSubmitBtn").disabled = false;
            });
    } else {
        login(username, password);
    }
}

function login(username, password) {
    fetch("/api/users/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
    })
        .then(r => {
            if (!r.ok) throw new Error(`Fel användarnamn eller lösenord (${r.status})`);
            return r.text();
        })
        .then(token => {
            authToken = token;
            currentUsername = username;
            onAuthenticated();
        })
        .catch(err => {
            console.error("login() misslyckades:", err);
            showAuthError(err.message);
        })
        .finally(() => {
            document.getElementById("authSubmitBtn").disabled = false;
        });
}

function logout() {
    authToken = null;
    currentUsername = null;
    gameId = null;
    document.getElementById("authPanel").style.display = "flex";
    document.getElementById("userBar").style.display = "none";
    document.querySelector(".setup").style.display = "none";
    document.getElementById("authUsername").value = "";
    document.getElementById("authPassword").value = "";
    document.getElementById("balanceText").innerText = "";
    resetTable();
}

function onAuthenticated() {
    document.getElementById("authPanel").style.display = "none";
    document.getElementById("userBar").style.display = "flex";
    document.getElementById("welcomeText").innerText = `Inloggad som ${currentUsername}`;
    document.querySelector(".setup").style.display = "flex";
    refreshBalance();
}

function refreshBalance() {
    authFetch("/api/users")
        .then(r => {
            if (!r.ok) throw new Error(`Server svarade ${r.status}`);
            return r.json();
        })
        .then(users => {
            const me = users.find(u => u.username === currentUsername);
            const balanceEl = document.getElementById("balanceText");
            if (me && me.balance !== undefined) {
                balanceEl.innerText = `Saldo: ${formatCurrency(me.balance)}`;
            } else {
                balanceEl.innerText = "";
            }
        })
        .catch(err => {
            console.error("refreshBalance() misslyckades:", err);
        });
}

function formatCurrency(amount) {
    return `${Number(amount).toLocaleString("sv-SE", { minimumFractionDigits: 2, maximumFractionDigits: 2 })} kr`;
}

function authFetch(url, options = {}) {
    const headers = Object.assign({}, options.headers, {
        Authorization: `Bearer ${authToken}`
    });
    return fetch(url, Object.assign({}, options, { headers }));
}

const SUIT_MAP = {
    S: { symbol: "♠", color: "black" },
    SPADES: { symbol: "♠", color: "black" },
    "♠": { symbol: "♠", color: "black" },
    H: { symbol: "♥", color: "red" },
    HEARTS: { symbol: "♥", color: "red" },
    "♥": { symbol: "♥", color: "red" },
    D: { symbol: "♦", color: "red" },
    DIAMONDS: { symbol: "♦", color: "red" },
    "♦": { symbol: "♦", color: "red" },
    C: { symbol: "♣", color: "black" },
    CLUBS: { symbol: "♣", color: "black" },
    "♣": { symbol: "♣", color: "black" }
};

function parseCard(raw) {
    if (raw === null || raw === undefined) return null;
    const str = String(raw).trim().toUpperCase();

    let m = str.match(/^(10|[2-9]|[AJQK])\s+OF\s+(SPADES|HEARTS|DIAMONDS|CLUBS)$/);
    if (m) {
        const suitInfo = SUIT_MAP[m[2]];
        if (suitInfo) return { rank: m[1], suit: suitInfo.symbol, color: suitInfo.color };
    }

    m = str.match(/^(10|[2-9]|[AJQK])[\s_-]?(SPADES|HEARTS|DIAMONDS|CLUBS|[SHDC♠♥♦♣])$/);
    if (m) {
        const suitInfo = SUIT_MAP[m[2]];
        if (suitInfo) return { rank: m[1], suit: suitInfo.symbol, color: suitInfo.color };
    }

    m = str.match(/^(10|[2-9]|[AJQK])(♠|♥|♦|♣)$/);
    if (m) {
        const suitInfo = SUIT_MAP[m[2]];
        return { rank: m[1], suit: suitInfo.symbol, color: suitInfo.color };
    }

    return null;
}

function renderCard(raw) {
    const parsed = parseCard(raw);
    if (!parsed) {
        return `<div class="card raw">${escapeHtml(String(raw))}</div>`;
    }
    return `
        <div class="card ${parsed.color}">
            <div class="corner">
                <span>${parsed.rank}</span>
                <span>${parsed.suit}</span>
            </div>
            <div class="pip">${parsed.suit}</div>
            <div class="corner bottom">
                <span>${parsed.rank}</span>
                <span>${parsed.suit}</span>
            </div>
        </div>`;
}

function escapeHtml(s) {
    return s.replace(/[&<>"']/g, c => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
    }[c]));
}

function setLoading(isLoading) {
    document.querySelectorAll("button").forEach(b => b.disabled = isLoading);
}

function startGame() {
    const bet = document.getElementById("bet").value || selectedBet;

    setLoading(true);
    authFetch("/api/blackjack/start", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ bet: Number(bet) })
    })
        .then(r => {
            if (!r.ok) throw new Error(`Server svarade ${r.status} ${r.statusText}`);
            return r.json();
        })
        .then(data => {
            gameId = data.gameId;
            console.log("Spel startat, gameId:", gameId);
            document.querySelector(".setup").style.display = "none";
            refreshBalance();
            loadGame();
        })
        .catch(err => {
            console.error("startGame() misslyckades:", err);
            setLoading(false);
            showError(`Kunde inte starta spelet: ${err.message}`);
        });
}

function hit() {
    if (!gameId) return;
    setLoading(true);
    authFetch(`/api/blackjack/${gameId}/hit`, { method: "POST" })
        .then(r => {
            if (!r.ok) throw new Error(`Server svarade ${r.status}`);
            return r.json();
        })
        .then(data => showGame(data))
        .catch(err => {
            console.error("hit() misslyckades:", err);
            setLoading(false);
            showError(`Något gick fel: ${err.message}`);
        });
}

function stand() {
    if (!gameId) return;
    setLoading(true);
    authFetch(`/api/blackjack/${gameId}/stand`, { method: "POST" })
        .then(r => {
            if (!r.ok) throw new Error(`Server svarade ${r.status}`);
            return r.json();
        })
        .then(data => showGame(data))
        .catch(err => {
            console.error("stand() misslyckades:", err);
            setLoading(false);
            showError(`Något gick fel: ${err.message}`);
        });
}

function loadGame() {
    authFetch(`/api/blackjack/${gameId}`)
        .then(r => {
            if (!r.ok) throw new Error(`Server svarade ${r.status}`);
            return r.json();
        })
        .then(data => showGame(data))
        .catch(err => {
            console.error("loadGame() misslyckades:", err);
            setLoading(false);
            showError(`Kunde inte hämta spelet: ${err.message}`);
        });
}

function newRound() {
    gameId = null;
    document.querySelector(".setup").style.display = "flex";
    resetTable();
}

function resetTable() {
    document.getElementById("playerCards").innerHTML = '<div class="empty-hint">Väntar på start…</div>';
    document.getElementById("dealerCards").innerHTML = '<div class="empty-hint">Väntar på start…</div>';
    document.getElementById("playerScore").innerText = "0";
    document.getElementById("dealerScore").innerText = "0";
    const status = document.getElementById("status");
    status.innerText = "";
    status.className = "";
    document.getElementById("newRoundBtn").style.display = "none";
    setActionButtons(false);
}

function showError(message) {
    const status = document.getElementById("status");
    status.innerText = message;
    status.className = "lose";
}

function setActionButtons(enabled) {
    document.getElementById("hitBtn").disabled = !enabled;
    document.getElementById("standBtn").disabled = !enabled;
}

function classify(status, result) {
    if (status !== "FINISHED") return "progress";
    if (result === "PLAYER_WIN") return "win";
    if (result === "DEALER_WIN") return "lose";
    if (result === "DRAW") return "push";
    return "progress";
}

function formatStatus(status, result) {
    if (status !== "FINISHED") return "Spelet pågår…";
    if (result === "PLAYER_WIN") return "🎉 Du vinner!";
    if (result === "DEALER_WIN") return "Given vinner";
    if (result === "DRAW") return "Oavgjort — insats återbetalad";
    return "Omgången är klar";
}

function showGame(game) {
    setLoading(false);

    document.getElementById("playerCards").innerHTML =
        (game.playerCards || []).map(renderCard).join("") ||
        '<div class="empty-hint">Inga kort ännu</div>';

    document.getElementById("dealerCards").innerHTML =
        (game.dealerCards || []).map(renderCard).join("") ||
        '<div class="empty-hint">Inga kort ännu</div>';

    document.getElementById("playerScore").innerText = game.playerValue ?? "0";
    document.getElementById("dealerScore").innerText = game.dealerValue ?? "0";

    const statusEl = document.getElementById("status");
    statusEl.innerText = formatStatus(game.status, game.result);
    statusEl.className = classify(game.status, game.result);

    const roundOver = game.status === "FINISHED";
    setActionButtons(!roundOver);
    document.getElementById("newRoundBtn").style.display = roundOver ? "inline-block" : "none";

    if (roundOver) {
        refreshBalance();
    }
}

document.addEventListener("DOMContentLoaded", () => {
    initChips();
    initAuthTabs();
    setActionButtons(false);
});