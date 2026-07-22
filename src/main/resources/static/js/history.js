function loadHistoryPage() {
    renderNav("history");

    if (!isLoggedIn()) {
        showHistoryState("loggedOut");
        return;
    }

    showHistoryState("loading");

    authFetch("/api/blackjack/history")
        .then(r => {
            if (!r.ok) throw new Error(`Server svarade ${r.status}`);
            return r.json();
        })
        .then(games => {
            if (!games || games.length === 0) {
                showHistoryState("empty");
                return;
            }
            renderHistoryTable(games);
            showHistoryState("table");
        })
        .catch(err => {
            console.error("loadHistoryPage() misslyckades:", err);
            showHistoryState("error");
        });
}

function showHistoryState(state) {
    ["loggedOut", "loading", "empty", "error", "table"].forEach(s => {
        const el = document.getElementById(`history-${s}`);
        if (el) el.style.display = s === state ? "block" : "none";
    });
}

const SUIT_MAP = {
    HEARTS: { symbol: "♥", color: "red" },
    DIAMONDS: { symbol: "♦", color: "red" },
    CLUBS: { symbol: "♣", color: "black" },
    SPADES: { symbol: "♠", color: "black" }
};

function parseMiniCard(raw) {
    const str = String(raw).trim().toUpperCase();
    const m = str.match(/^(10|[2-9]|[AJQK])\s+OF\s+(HEARTS|DIAMONDS|CLUBS|SPADES)$/);
    if (!m) return null;

    const suitInfo = SUIT_MAP[m[2]];
    return { rank: m[1], suit: suitInfo.symbol, color: suitInfo.color };
}

function renderMiniCards(cards) {
    if (!cards || cards.length === 0) {
        return '<span class="no-cards">—</span>';
    }

    return cards.map(raw => {
        const parsed = parseMiniCard(raw);
        if (!parsed) {
            return `<span class="mini-card">${escapeHtml(String(raw))}</span>`;
        }
        return `<span class="mini-card ${parsed.color}">${parsed.rank}${parsed.suit}</span>`;
    }).join(" ");
}

const RESULT_LABELS = {
    PLAYER_WIN: "Vinst",
    DEALER_WIN: "Förlust",
    DRAW: "Oavgjort"
};

const RESULT_CLASSES = {
    PLAYER_WIN: "tx-win",
    DEALER_WIN: "tx-bet",
    DRAW: "tx-push"
};

function renderResultBadge(game) {
    if (game.status !== "FINISHED" || !game.result) {
        return '<span class="type-badge">Pågår</span>';
    }
    return `<span class="type-badge ${RESULT_CLASSES[game.result] || ""}">${RESULT_LABELS[game.result] || game.result}</span>`;
}

function renderHistoryTable(games) {
    const rows = games.map(g => `
        <tr>
            <td>${formatDate(g.createdAt)}</td>
            <td>${formatCurrency(g.betAmount)}</td>
            <td class="cards-cell">${renderMiniCards(g.playerCards)}</td>
            <td class="cards-cell">${renderMiniCards(g.dealerCards)}</td>
            <td>${g.playerScore} — ${g.dealerScore}</td>
            <td>${renderResultBadge(g)}</td>
        </tr>
    `).join("");

    document.getElementById("historyTableBody").innerHTML = rows;
}

function formatDate(isoString) {
    if (!isoString) return "—";
    const date = new Date(isoString);
    return date.toLocaleString("sv-SE", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit"
    });
}

function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g, c => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
    }[c]));
}

document.addEventListener("DOMContentLoaded", loadHistoryPage);