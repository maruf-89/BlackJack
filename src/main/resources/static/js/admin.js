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
            <td>${formatCurrency(u.balance)}</td>
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

function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g, c => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
    }[c]));
}

document.addEventListener("DOMContentLoaded", loadAdminPage);