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
        tableDate: "Průměr za dobu",
        saveSuccess: "Nastavení uloženo!",
        saveError: "Chyba při ukládání.",
        selectError: "Vyber alespoň jednu měnu.",
        apiError: "Nepodařilo se načíst data z API.",
        rateLabel: "kurz",
        loadError: "Nepodařilo se načíst uložené nastavení.",
        dateErr: "Datum Od musí dříve než Do"
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
        tableDate: "Avarage for period",
        saveSuccess: "Settings saved!",
        saveError: "Error while saving.",
        selectError: "Select at least one currency.",
        apiError: "Unable to fetch data from API.",
        rateLabel: "rate",
        loadError: "Error while loading saved settings.",
        dateErr: "Date From has be earlier then To"
    }
};

document.addEventListener("DOMContentLoaded", function() {
    const chipsDiv = document.getElementById("chips");

    if (chipsDiv) {
        for (let i = 0; i < CURRENCIES.length; i++) {
            let currency = CURRENCIES[i];

            let chip = document.createElement("div");
            chip.className = "chip";
            chip.id = "chip-" + currency;
            chip.textContent = currency;

            chip.onclick = function() {
                if (selected.has(currency)) {
                    selected.delete(currency);
                    chip.classList.remove("on");
                } else {
                    selected.add(currency);
                    chip.classList.add("on");
                }
            };

            chipsDiv.appendChild(chip);
        }
    }

    const savedLang = localStorage.getItem("preferredLang") || "cs";
    document.getElementById("langSwitch").value = savedLang;
    changeLanguage(savedLang);
    loadSettings();
});

function changeLanguage(lang) {
    currentLang = lang;
    localStorage.setItem("preferredLang", lang);
    document.querySelectorAll("[data-i18n]").forEach(element => {
        element.textContent = translations[lang][element.dataset.i18n] || element.textContent;
    });
}

async function loadSettings() {
    try {
        const res = await fetch("/api/settings");
        if (!res.ok) return;
        const s = await res.json();
        if (s.baseCurrency) {
            document.getElementById("base").value = s.baseCurrency;
        }
        const savedCurr = s.selectedCurrencies || [];
        for (let i = 0; i < savedCurr.length; i++) {
            let curr = savedCurr[i];
            selected.add(curr);
            let chip = document.getElementById("chip-" + curr);
            if (chip) {
                chip.classList.add("on");
            }
        }
    } catch(e) {
        console.log(translations[currentLang].loadError);
        const errorBox = document.getElementById("errorBox");
        errorBox.textContent = translations[currentLang].loadError;
        errorBox.style.display = "block";
    }
}

async function saveSettings() {
    const base = document.getElementById("base").value;
    const symbols = Array.from(selected).join(",");
    if (symbols === "") {
        alert(translations[currentLang].selectError);
        return;
    }
    try {
        const response = await fetch(`/api/settings/save?base=${base}&symbols=${symbols}`);
        if (response.ok) {
            alert(translations[currentLang].saveSuccess);
        } else {
            alert(translations[currentLang].saveError);
        }
    } catch(e) {
        alert(translations[currentLang].saveError);
    }
}

async function loadData() {
    const base = document.getElementById("base").value;
    const from = document.getElementById("dateFrom").value;
    const to = document.getElementById("dateTo").value;
    const helpArr = Array.from(selected);
    const symbols = helpArr.join(",");

    if (!symbols) return alert(translations[currentLang].selectError);

    if(from > to){
        document.getElementById("errorBox").textContent = translations[currentLang].dateErr;
        document.getElementById("errorBox").style.display = "block";
        return;
    }
    try {
        const responses = await Promise.all([
            fetch(`/api/rates?base=${base}&symbols=${symbols}`),
            fetch(`/api/strongest?base=${base}&symbols=${symbols}`),
            fetch(`/api/weakest?base=${base}&symbols=${symbols}`),
            fetch(`/api/date?base=${base}&symbols=${symbols}&dateFrom=${from}&dateTo=${to}`)
        ]);

        const [ratesData, strongData, weakData, avgsData] = await Promise.all(responses.map(r => r.json()));

        const fillBox = (id, rateId, data) => {
            const [currency] = Object.keys(data);
            if (currency) {
                document.getElementById(id).textContent = currency;
                document.getElementById(rateId).textContent = translations[currentLang].rateLabel + ": " + data[currency].toFixed(5);            }
        };

        fillBox("strongest", "strongestRate", strongData);
        fillBox("weakest", "weakestRate", weakData);

        const actualRates = ratesData.rates || {};
        const tableBody = document.getElementById("tableBody");
        tableBody.innerHTML = "";
        const keys = Object.keys(actualRates);
        for (let i = 0; i < keys.length; i++) {
            let curr = keys[i];
            let rate = actualRates[curr];
            let avg = avgsData[curr] || 0;
            let row = `
                <tr>
                    <td><strong>${curr}</strong></td>
                    <td>${rate.toFixed(5)}</td>
                    <td class="text-info">${avg.toFixed(5)}</td>
                </tr>
            `;

            tableBody.innerHTML += row;
        }

        document.getElementById("average").textContent = from && to ? `${from} - ${to}` : "–";
        document.getElementById("results").style.display = "block";
        document.getElementById("errorBox").style.display = "none";

    } catch(e) {
        document.getElementById("errorBox").textContent = translations[currentLang].apiError;
        document.getElementById("errorBox").style.display = "block";
    }
}