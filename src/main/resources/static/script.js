const CURRENCIES = ["AUD","CAD","CHF","CNY","CZK","DKK","EUR","GBP","HKD","HUF","ILS","JPY","MXN","NOK","NZD","PHP","PLN","RON","SEK","SGD","THB","TRY","USD","ZAR"];
const selected = new Set();

document.addEventListener("DOMContentLoaded", () => {
    const chipsEl = document.getElementById("chips");
    if (chipsEl) {
        chipsEl.innerHTML = "";
        CURRENCIES.forEach(c => {
            const el = document.createElement("div");
            el.className = "chip";
            el.textContent = c;
            el.onclick = () => {
                if (selected.has(c)) {
                    selected.delete(c);
                    el.classList.remove("on");
                } else {
                    selected.add(c);
                    el.classList.add("on");
                }
            };
            chipsEl.appendChild(el);
        });
    }
    loadSettings();
});

async function loadSettings() {
    try {
        const res = await fetch("/api/settings");
        if (!res.ok) return;
        const s = await res.json();
        const baseVal = s.base || s.baseCurrency;
        if (baseVal) document.getElementById("base").value = baseVal;

        const syms = s.symbols || s.selectedCurrencies || [];
        syms.forEach(c => {
            selected.add(c);
            document.querySelectorAll(".chip").forEach(el => {
                if (el.textContent === c) el.classList.add("on");
            });
        });
    } catch(e) { console.log("Žádné uložené nastavení."); }
}

async function saveSettings() {
    const base = document.getElementById("base").value;
    const symbols = [...selected].join(",");
    if (!symbols) return alert("Vyber měny!");
    try {
        await fetch(`/api/settings/save?base=${base}&symbols=${symbols}`);
        alert("Nastavení uloženo!");
    } catch(e) { alert("Chyba při ukládání."); }
}

async function loadData() {
    if (selected.size === 0) { alert("Vyber alespoň jednu měnu."); return; }

    const base = document.getElementById("base").value;
    const from = document.getElementById("dateFrom").value;
    const to = document.getElementById("dateTo").value;
    const symbols = [...selected].join(",");

    document.getElementById("errorBox").style.display = "none";
    const resultsDiv = document.getElementById("results");

    try {
        const [ratesRes, strongRes, weakRes, avgRes] = await Promise.all([
            fetch(`/api/rates?base=${base}&symbols=${symbols}`),
            fetch(`/api/strongest?base=${base}&symbols=${symbols}`),
            fetch(`/api/weakest?base=${base}&symbols=${symbols}`),
            fetch(`/api/date?base=${base}&symbols=${symbols}&dateFrom=${from}&dateTo=${to}`)
        ]);

        const ratesData = await ratesRes.json();
        const strongData = await strongRes.json();
        const weakData = await weakRes.json();
        const avgsData = await avgRes.json();

        const sKey = Object.keys(strongData)[0];
        const wKey = Object.keys(weakData)[0];

        if (sKey) {
            document.getElementById("strongest").textContent = sKey;
            document.getElementById("strongestRate").textContent = `kurz ${parseFloat(strongData[sKey]).toFixed(5)}`;
        }
        if (wKey) {
            document.getElementById("weakest").textContent = wKey;
            document.getElementById("weakestRate").textContent = `kurz ${parseFloat(weakData[wKey]).toFixed(5)}`;
        }

        const tbody = document.getElementById("tableBody");
        tbody.innerHTML = "";

        const actualRates = ratesData.rates || {};

        Object.entries(actualRates).forEach(([c, r]) => {
            tbody.innerHTML += `<tr><td><strong>${c}</strong></td><td>${parseFloat(r).toFixed(5)}</td></tr>`;
        });

        resultsDiv.style.display = "block";

        // 3. PRŮMĚR
        const avgVals = Object.values(avgsData);
        document.getElementById("average").textContent = avgVals.length > 0
            ? (avgVals.reduce((a, b) => a + b, 0) / avgVals.length).toFixed(5)
            : "–";

    } catch(e) {
        console.error("CHYBA:", e);
        document.getElementById("errorBox").style.display = "block";
        resultsDiv.style.display = "none";
    }
}
