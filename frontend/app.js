const apiBaseUrlInput = document.querySelector("#apiBaseUrl");
const userForm = document.querySelector("#userForm");
const analysisForm = document.querySelector("#analysisForm");
const usuarioSelect = document.querySelector("#usuarioSelect");
const statusMessage = document.querySelector("#statusMessage");
const resultPanel = document.querySelector("#resultPanel");
const refreshHistoryButton = document.querySelector("#refreshHistoryButton");
const historyTable = document.querySelector("#historyTable");
const analyzeButton = document.querySelector("#analyzeButton");

const resultExercise = document.querySelector("#resultExercise");
const resultRepetitions = document.querySelector("#resultRepetitions");
const resultDuration = document.querySelector("#resultDuration");
const resultFeedback = document.querySelector("#resultFeedback");

function apiBaseUrl() {
    return apiBaseUrlInput.value.replace(/\/$/, "");
}

function setStatus(message, type = "") {
    statusMessage.textContent = message;
    statusMessage.className = `status ${type}`.trim();
}

async function requestJson(url, options = {}) {
    const response = await fetch(url, options);

    if (!response.ok) {
        const error = await response.json().catch(() => null);
        const message = error?.messages?.join(" ") || `Erro HTTP ${response.status}`;
        throw new Error(message);
    }

    return response.json();
}

async function loadUsers() {
    const users = await requestJson(`${apiBaseUrl()}/usuarios`);

    usuarioSelect.innerHTML = "";

    if (users.length === 0) {
        const option = document.createElement("option");
        option.value = "";
        option.textContent = "Nenhum usuario cadastrado";
        usuarioSelect.appendChild(option);
        return;
    }

    for (const user of users) {
        const option = document.createElement("option");
        option.value = user.id;
        option.textContent = `${user.nome} (${user.email})`;
        usuarioSelect.appendChild(option);
    }
}

async function loadHistory() {
    const selectedUserId = usuarioSelect.value;
    const url = selectedUserId
        ? `${apiBaseUrl()}/treinos/usuario/${selectedUserId}`
        : `${apiBaseUrl()}/treinos`;

    const workouts = await requestJson(url);
    historyTable.innerHTML = "";

    if (workouts.length === 0) {
        const row = document.createElement("tr");
        row.innerHTML = `<td colspan="6">Nenhum treino encontrado.</td>`;
        historyTable.appendChild(row);
        return;
    }

    for (const workout of workouts) {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${formatDate(workout.dataTreino)}</td>
            <td>${escapeHtml(workout.usuarioNome)}</td>
            <td>${escapeHtml(workout.exercicio)}</td>
            <td>${workout.repeticoes}</td>
            <td>${workout.duracao}s</td>
            <td>${escapeHtml(workout.feedback)}</td>
        `;
        historyTable.appendChild(row);
    }
}

function formatDate(value) {
    if (!value) {
        return "-";
    }

    return new Intl.DateTimeFormat("pt-BR", {
        dateStyle: "short",
        timeStyle: "short",
    }).format(new Date(value));
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function renderResult(workout) {
    resultExercise.textContent = workout.exercicio;
    resultRepetitions.textContent = workout.repeticoes;
    resultDuration.textContent = `${workout.duracao}s`;
    resultFeedback.textContent = workout.feedback;
    resultPanel.classList.remove("hidden");
}

userForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const payload = {
        nome: document.querySelector("#nome").value.trim(),
        email: document.querySelector("#email").value.trim(),
        senha: document.querySelector("#senha").value,
    };

    try {
        setStatus("Criando usuario...");
        const user = await requestJson(`${apiBaseUrl()}/usuarios`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(payload),
        });

        userForm.reset();
        await loadUsers();
        usuarioSelect.value = user.id;
        await loadHistory();
        setStatus("Usuario criado.", "success");
    } catch (error) {
        setStatus(error.message, "error");
    }
});

analysisForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const usuarioId = usuarioSelect.value;
    const video = document.querySelector("#video").files[0];

    if (!usuarioId) {
        setStatus("Selecione um usuario antes de analisar o treino.", "error");
        return;
    }

    if (!video) {
        setStatus("Selecione um video.", "error");
        return;
    }

    const formData = new FormData();
    formData.append("usuarioId", usuarioId);
    formData.append("video", video);

    try {
        analyzeButton.disabled = true;
        setStatus("Processando video...");

        const workout = await requestJson(`${apiBaseUrl()}/treinos/analisar`, {
            method: "POST",
            body: formData,
        });

        analysisForm.reset();
        renderResult(workout);
        await loadHistory();
        setStatus("Analise concluida.", "success");
    } catch (error) {
        setStatus(error.message, "error");
    } finally {
        analyzeButton.disabled = false;
    }
});

refreshHistoryButton.addEventListener("click", async () => {
    try {
        await loadHistory();
        setStatus("Historico atualizado.", "success");
    } catch (error) {
        setStatus(error.message, "error");
    }
});

usuarioSelect.addEventListener("change", () => {
    loadHistory().catch((error) => setStatus(error.message, "error"));
});

async function bootstrap() {
    try {
        await loadUsers();
        await loadHistory();
        setStatus("Pronto.");
    } catch (error) {
        setStatus(error.message, "error");
    }
}

bootstrap();
