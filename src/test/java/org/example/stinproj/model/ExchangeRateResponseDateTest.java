package org.example.stinproj.model;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExchangeRateResponseDateTest {

    @Test
    public void testGetterSetter() {
        ExchangeRateResponseDate response = new ExchangeRateResponseDate();
        Map<String, Map<String, Double>> testRates = new HashMap<>();

        Map<String, Double> dailyRates = new HashMap<>();
        dailyRates.put("USD", 1.08);
        testRates.put("2024-01-01", dailyRates);

        response.setRates(testRates);

        assertNotNull(response.getRates());
        assertEquals(1, response.getRates().size());
        assertTrue(response.getRates().containsKey("2024-01-01"));
        assertEquals(1.08, response.getRates().get("2024-01-01").get("USD"));
    }
}