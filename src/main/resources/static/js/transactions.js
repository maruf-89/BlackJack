function loadTransactionsPage() {
    renderNav("transactions");

    if (!isLoggedIn()) {
        showTxState("loggedOut");
        return;
    }

    showTxState("loading");

    authFetch("/api/users/transactions")
        .then(r => {
            if (!r.ok) throw new Error(`Server svarade ${r.status}`);
            return r.json();
        })
        .then(transactions => {
            if (!transactions || transactions.length === 0) {
                showTxState("empty");
                return;
            }
            renderTransactionTable(transactions);
            showTxState("table");
        })
        .catch(err => {
            console.error("loadTransactionsPage() misslyckades:", err);
            showTxState("error");
        });
}

function showTxState(state) {
    ["loggedOut", "loading", "empty", "error", "table"].forEach(s => {
        const el = document.getElementById(`tx-${s}`);
        if (el) el.style.display = s === state ? "block" : "none";
    });
}

const TYPE_LABELS = {
    BET: "Insats",
    WIN: "Vinst",
    PUSH: "Oavgjort",
    ADMIN_ADJUSTMENT: "Admin-justering"
};

const TYPE_CLASSES = {
    BET: "tx-bet",
    WIN: "tx-win",
    PUSH: "tx-push",
    ADMIN_ADJUSTMENT: "tx-admin"
};

function renderTransactionTable(transactions) {
    const rows = transactions.map(t => {
        const positive = Number(t.amount) >= 0;
        return `
            <tr>
                <td>${formatDate(t.createdAt)}</td>
                <td><span class="type-badge ${TYPE_CLASSES[t.type] || ""}">${TYPE_LABELS[t.type] || t.type}</span></td>
                <td class="${positive ? "amount-positive" : "amount-negative"}">
                    ${positive ? "+" : ""}${formatCurrency(t.amount)}
                </td>
            </tr>
        `;
    }).join("");

    document.getElementById("transactionTableBody").innerHTML = rows;
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

document.addEventListener("DOMContentLoaded", loadTransactionsPage);