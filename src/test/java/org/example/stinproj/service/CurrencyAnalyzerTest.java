package org.example.stinproj.service;

import org.example.stinproj.model.ExchangeRateResponse;
import org.example.stinproj.model.ExchangeRateResponseDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CurrencyAnalyzerTest {

    private CurrencyAnalyzer analyzer;

    @BeforeEach
    public void setUp() {
        analyzer = new CurrencyAnalyzer();
    }

    @Test
    public void testFindWeakest() {
        ExchangeRateResponse response = new ExchangeRateResponse();
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.0);
        rates.put("EUR", 0.9);
        rates.put("CZK", 25.0);
        response.setRates(rates);

        Map.Entry<String, Double> weakest = analyzer.findWeakest(response);

        assertNotNull(weakest);
        assertEquals("CZK", weakest.getKey());
        assertEquals(25.0, weakest.getValue());
    }

    @Test
    public void testFindStrongest() {
        ExchangeRateResponse response = new ExchangeRateResponse();
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.0);
        rates.put("EUR", 0.9);
        rates.put("CZK", 25.0);
        response.setRates(rates);

        Map.Entry<String, Double> strongest = analyzer.findStrongest(response);

        assertNotNull(strongest);
        assertEquals("EUR", strongest.getKey());
        assertEquals(0.9, strongest.getValue());
    }

    @Test
    public void testCalculateAverage() {
        ExchangeRateResponseDate responseDate = new ExchangeRateResponseDate();
        Map<String, Map<String, Double>> ratesDate = new HashMap<>();

        Map<String, Double> day1 = new HashMap<>();
        day1.put("USD", 1.0);
        day1.put("EUR", 2.0);
        ratesDate.put("2024-01-01", day1);

        Map<String, Double> day2 = new HashMap<>();
        day2.put("USD", 3.0);
        day2.put("EUR", 4.0);
        ratesDate.put("2024-01-02", day2);

        responseDate.setRates(ratesDate);

        Map<String, Double> average = analyzer.calculateAverage(responseDate);

        assertEquals(2.0, average.get("USD"));
        assertEquals(3.0, average.get("EUR"));
    }

    @Test
    public void testFindWeakestWithEmptyRates() {
        ExchangeRateResponse response = new ExchangeRateResponse();
        response.setRates(new HashMap<>());

        Map.Entry<String, Double> weakest = analyzer.findWeakest(response);

        assertNull(weakest);
    }
}