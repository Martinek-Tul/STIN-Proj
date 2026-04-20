package org.example.stinproj.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;

public class UserSettings {
    @Getter
    @Setter
    private String baseCurrency;
    @Getter
    @Setter
    private List<String> selectedCurrencies;

    private static final List<String> availableCurrencies = List.of(
            "AUD", "CAD", "CHF", "CNY", "CZK", "DKK", "EUR", "GBP",
            "HKD", "HUF", "ILS", "JPY", "MXN", "NOK", "NZD", "PHP",
            "PLN", "RON", "SEK", "SGD", "THB", "TRY", "USD", "ZAR"
    );
    public UserSettings(String baseCurrency, List<String> selectedCurrencies){
        if(!availableCurrencies.contains(baseCurrency)){
            throw new IllegalArgumentException("Nepodporovaná měna: "+baseCurrency);
        }
        if(!new HashSet<>(availableCurrencies).containsAll(selectedCurrencies)){
            throw new IllegalArgumentException("Seznam obsahuje nepodporovanou měnu");
        }
        this.baseCurrency = baseCurrency;
        this.selectedCurrencies = selectedCurrencies;
    }

}
