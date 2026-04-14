package service;

import model.ExchangeRateResponse;

import java.util.Map;

public class CurrencyAnalyzer {
    public Map.Entry<String, Double> findStrongest(ExchangeRateResponse response){
        Map<String, Double> rates = response.getRates();
        Map.Entry<String, Double> strongest = null;
        for (Map.Entry<String, Double> entry : rates.entrySet()){
            if(strongest == null || strongest.getValue() < entry.getValue()){
                strongest = entry;
            }
        }
        return strongest;
    }
}
