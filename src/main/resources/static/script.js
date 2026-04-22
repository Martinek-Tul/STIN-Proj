const CURRENCIES = ["AUD","CAD","CHF","CNY","CZK","DKK","EUR","GBP","HKD","HUF","ILS","JPY","MXN","NOK","NZD","PHP","PLN","RON","SEK","SGD","THB","TRY","USD","ZAR"];
const selected = new Set();
let currentLang = 'cs';

const translations = {
    cs: {
        title: "Currency Analyzer",
        logout: "Odhlásit",
        settings: "Nastavení",
        baseCurrency: "Základní měna",
        dateRange: "Časové období",
        trackedCurrencies: "Sledované měny (klikni pro výběr)",
        loadBtn: "Načíst data",
        saveBtn: "Uložit nastavení",
        strongest: "Nejsilnější měna",
        weakest: "Nejslabší měna",
        average: "Průměr za období",
        tableTitle: "Kurzy",
        tableCurrency: "Měna",
        tableRate: "Kurz vůči základní",
        saveSuccess: "Nastavení uloženo!",
        saveError: "Chyba při ukládání.",
        selectError: "Vyber alespoň jednu měnu.",
        apiError: "Nepodařilo se načíst data z API.",
        rateLabel: "kurz"
    },
    en: {
        title: "Currency Analyzer",
        logout: "Logout",
        settings: "Settings",
        baseCurrency: "Base Currency",
        dateRange: "Date Range",
        trackedCurrencies: "Tracked Currencies (click to select)",
        loadBtn: "Load Data",
        saveBtn: "Save Settings",
        strongest: "Strongest Currency",
        weakest: "Weakest Currency",
        average: "Period Average",
        tableTitle: "Exchange Rates",
        tableCurrency: "Currency",
        tableRate: "Rate against base",
        saveSuccess: "Settings saved!",
        saveError: "Error while saving.",
        selectError: "Select at least one currency.",
        apiError: "Unable to fetch data from API.",
        rateLabel: "rate"
    }
};

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

    const savedLang = localStorage.getItem("preferredLang") || "cs";
    document.getElementById("langSwitch").value = savedLang;
    changeLanguage(savedLang);
    loadSettings();
});

function changeLanguage(lang) {
    currentLang = lang;
    localStorage.setItem("preferredLang", lang);
    document.querySelectorAll("[data-i18n]").forEach(el => {
        const key = el.getAttribute("data-i18n");
        if (translations[lang][key]) {
            el.textContent = translations[lang][key];
        }
    });
}

async function loadSettings() {
    try {
        const res = await fetch("/api/settings");
        if (!res.ok) return;
        const s = await res.json();

        const baseVal = s.baseCurrency;
        if (baseVal) document.getElementById("base").value = baseVal;

        const syms = s.selectedCurrencies || [];
        syms.forEach(c => {
            selected.add(c);
            document.querySelectorAll(".chip").forEach(el => {
                if (el.textContent === c) el.classList.add("on");
            });
        });
    } catch(e) {
        console.log("Použito výchozí nastavení (soubor nenalezen).");
    }
}

async function saveSettings() {
    const base = document.getElementById("base").value;
    const symbols = [...selected].join(",");
    if (!symbols) return alert(translations[currentLang].selectError);

    try {
        await fetch(`/api/settings/save?base=${base}&symbols=${symbols}`);
        alert(translations[currentLang].saveSuccess);
    } catch(e) {
        alert(translations[currentLang].saveError);
    }
}

async function loadData() {
    if (selected.size === 0) {
        alert(translations[currentLang].selectError);
        return;
    }

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

        const rateLabel = translations[currentLang].rateLabel;

        const sKey = Object.keys(strongData)[0];
        if (sKey) {
            document.getElementById("strongest").textContent = sKey;
            document.getElementById("strongestRate").textContent = `${rateLabel}: ${parseFloat(strongData[sKey]).toFixed(5)}`;
        }

        const wKey = Object.keys(weakData)[0];
        if (wKey) {
            document.getElementById("weakest").textContent = wKey;
            document.getElementById("weakestRate").textContent = `${rateLabel}: ${parseFloat(weakData[wKey]).toFixed(5)}`;
        }

        const tbody = document.getElementById("tableBody");
        tbody.innerHTML = "";
        const actualRates = ratesData.rates || {};
        Object.entries(actualRates).forEach(([c, r]) => {
            tbody.innerHTML += `<tr><td><strong>${c}</strong></td><td>${parseFloat(r).toFixed(5)}</td></tr>`;
        });

        const avgVals = Object.values(avgsData);
        document.getElementById("average").textContent = avgVals.length > 0
            ? (avgVals.reduce((a, b) => a + b, 0) / avgVals.length).toFixed(5)
            : "–";

        resultsDiv.style.display = "block";

    } catch(e) {
        console.error("API Error:", e);
        const errBox = document.getElementById("errorBox");
        errBox.textContent = translations[currentLang].apiError;
        errBox.style.display = "block";
        resultsDiv.style.display = "none";
    }
}