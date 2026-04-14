package service;

import model.ExchangeRateResponse;
import model.ExchangeRateResponseDate;

import java.util.HashMap;
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

    public Map.Entry<String, Double> findWeakest(ExchangeRateResponse response){
        Map<String, Double> rates = response.getRates();
        Map.Entry<String, Double> weakest = null;
        for (Map.Entry<String, Double> entry : rates.entrySet()){
            if(weakest == null || weakest.getValue() > entry.getValue()){
                weakest = entry;
            }
        }
        return weakest;
    }
    public Map<String, Double> calculateAverage(ExchangeRateResponseDate responseDate){
        Map<String, Map<String, Double>> ratesDate = responseDate.getRates();
        Map<String, Double> average = new HashMap<>();
        for (Map.Entry<String, Map<String, Double>> dateEntry : ratesDate.entrySet()) {
            Map<String, Double> ratesForDate = dateEntry.getValue();

            for (Map.Entry<String, Double> rateEntry : ratesForDate.entrySet()) {
                average.put(rateEntry.getKey(), average.getOrDefault(rateEntry.getKey(), 0.0) + rateEntry.getValue());
            }
        }

        int numberOfDays = ratesDate.size();
        for (Map.Entry<String, Double> entry : average.entrySet()) {
            average.put(entry.getKey(), entry.getValue() / numberOfDays);
        }
        return average;
    }
}
