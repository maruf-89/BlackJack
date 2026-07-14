function loadHighscores() {
    renderNav("highscore");
    showHighscoreState("loading");

    fetch("/api/highscores")
        .then(r => {
            if (!r.ok) throw new Error(`Server svarade ${r.status}`);
            return r.json();
        })
        .then(entries => {
            if (!entries || entries.length === 0) {
                showHighscoreState("empty");
                return;
            }
            renderHighscoreList(entries);
            showHighscoreState("list");
        })
        .catch(err => {
            console.error("loadHighscores() misslyckades:", err);
            showHighscoreState("error");
        });
}

function showHighscoreState(state) {
    ["loading", "empty", "error", "list"].forEach(s => {
        const el = document.getElementById(`highscore-${s}`);
        if (el) el.style.display = s === state ? "block" : "none";
    });
}

function renderHighscoreList(entries) {
    const medals = ["🥇", "🥈", "🥉"];

    const rows = entries.map((entry, i) => `
        <div class="highscore-row ${i < 3 ? "top3" : ""}">
            <span class="rank">${medals[i] || `#${i + 1}`}</span>
            <span class="hs-username">${escapeHtml(entry.username)}</span>
            <span class="hs-balance">${formatCurrency(entry.balance)}</span>
        </div>
    `).join("");

    document.getElementById("highscoreList").innerHTML = rows;
}

function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g, c => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
    }[c]));
}

document.addEventListener("DOMContentLoaded", loadHighscores);