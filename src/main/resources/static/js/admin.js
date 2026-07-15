function loadAdminPage() {
    renderNav("admin");

    if (!isLoggedIn()) {
        showAdminState("loggedOut");
        return;
    }

    showAdminState("loading");

    authFetch("/api/admin/users")
        .then(r => {
            if (r.status === 403) {
                showAdminState("denied");
                return null;
            }
            if (!r.ok) throw new Error(`Server svarade ${r.status}`);
            return r.json();
        })
        .then(users => {
            if (users) {
                renderUserTable(users);
                showAdminState("table");
            }
        })
        .catch(err => {
            console.error("loadAdminPage() misslyckades:", err);
            showAdminState("error");
        });
}

function showAdminState(state) {
    ["loggedOut", "loading", "denied", "error", "table"].forEach(s => {
        const el = document.getElementById(`admin-${s}`);
        if (el) el.style.display = s === state ? "block" : "none";
    });
}

function renderUserTable(users) {
    const rows = users.map(u => `
        <tr>
            <td>${u.id}</td>
            <td>${escapeHtml(u.username)}</td>
            <td>${escapeHtml(u.email)}</td>
            <td>
                <div class="balance-edit">
                    <input type="number" step="0.01" min="0" id="balance-${u.id}" value="${u.balance}">
                    <button class="ghost small" onclick="updateBalance(${u.id})">Uppdatera</button>
                </div>
            </td>
            <td><span class="role-badge ${u.role && u.role.name === "ADMIN" ? "admin" : ""}">${u.role ? u.role.name : "—"}</span></td>
            <td>
                ${u.role && u.role.name === "ADMIN"
        ? '<span class="already-admin">Admin</span>'
        : `<button class="ghost small" onclick="promoteUser(${u.id})">Gör till admin</button>`
    }
            </td>
        </tr>
    `).join("");

    document.getElementById("userTableBody").innerHTML = rows;
}

function promoteUser(id) {
    authFetch(`/api/admin/users/${id}/admin`, { method: "PUT" })
        .then(r => {
            if (!r.ok) throw new Error(`Server svarade ${r.status}`);
            return r.json();
        })
        .then(() => loadAdminPage())
        .catch(err => {
            console.error("promoteUser() misslyckades:", err);
            alert("Kunde inte uppgradera användaren.");
        });
}

function updateBalance(id) {
    const input = document.getElementById(`balance-${id}`);
    const value = Number(input.value);

    if (isNaN(value) || value < 0) {
        alert("Ange ett giltigt saldo (0 eller högre).");
        return;
    }

    authFetch(`/api/admin/users/${id}/balance`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ balance: value })
    })
        .then(r => {
            if (!r.ok) throw new Error(`Server svarade ${r.status}`);
            return r.json();
        })
        .then(() => loadAdminPage())
        .catch(err => {
            console.error("updateBalance() misslyckades:", err);
            alert("Kunde inte uppdatera saldot.");
        });
}

function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g, c => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
    }[c]));
}

document.addEventListener("DOMContentLoaded", loadAdminPage);