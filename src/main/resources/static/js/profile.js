function loadProfilePage() {
    renderNav("profile");

    if (!isLoggedIn()) {
        document.getElementById("profile-loggedOut").style.display = "block";
        document.getElementById("profile-content").style.display = "none";
        return;
    }

    document.getElementById("profile-loggedOut").style.display = "none";
    document.getElementById("profile-content").style.display = "block";

    authFetch("/api/users/me")
        .then(r => {
            if (!r.ok) throw new Error(`Server svarade ${r.status}`);
            return r.json();
        })
        .then(me => {
            document.getElementById("profileUsername").innerText = me.username;
            document.getElementById("profileEmail").innerText = me.email;
            document.getElementById("profileBalance").innerText = formatCurrency(me.balance);
            document.getElementById("profileRole").innerText = me.role ? me.role.name : "—";
        })
        .catch(err => {
            console.error("loadProfilePage() misslyckades:", err);
        });
}

function clearPasswordMessages() {
    const errorEl = document.getElementById("passwordError");
    const successEl = document.getElementById("passwordSuccess");
    errorEl.innerText = "";
    errorEl.style.display = "none";
    successEl.innerText = "";
    successEl.style.display = "none";
}

function showPasswordError(message) {
    const el = document.getElementById("passwordError");
    el.innerText = message;
    el.style.display = "block";
}

function showPasswordSuccess(message) {
    const el = document.getElementById("passwordSuccess");
    el.innerText = message;
    el.style.display = "block";
}

function submitPasswordChange() {
    clearPasswordMessages();

    const currentPassword = document.getElementById("currentPassword").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    if (!currentPassword || !newPassword || !confirmPassword) {
        showPasswordError("Fyll i alla fält.");
        return;
    }

    if (newPassword.length < 4) {
        showPasswordError("Nytt lösenord måste vara minst 4 tecken.");
        return;
    }

    if (newPassword !== confirmPassword) {
        showPasswordError("Lösenorden matchar inte.");
        return;
    }

    authFetch("/api/users/password", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ currentPassword, newPassword })
    })
        .then(r => {
            if (!r.ok) throw new Error(r.status === 400 ? "Fel nuvarande lösenord." : `Server svarade ${r.status}`);
            showPasswordSuccess("Lösenordet är uppdaterat.");
            document.getElementById("currentPassword").value = "";
            document.getElementById("newPassword").value = "";
            document.getElementById("confirmPassword").value = "";
        })
        .catch(err => {
            console.error("submitPasswordChange() misslyckades:", err);
            showPasswordError(err.message);
        });
}

document.addEventListener("DOMContentLoaded", loadProfilePage);