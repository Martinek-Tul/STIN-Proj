package org.example.stinproj.model;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExchangeRateResponseTest {

    @Test
    public void testGetterSetter() {
        ExchangeRateResponse response = new ExchangeRateResponse();
        Map<String, Double> testRates = new HashMap<>();
        testRates.put("USD", 1.08);
        testRates.put("EUR", 1.0);
        testRates.put("CZK", 25.30);

        response.setRates(testRates);

        assertNotNull(response.getRates());
        assertEquals(3, response.getRates().size());
        assertEquals(25.30, response.getRates().get("CZK"));
        assertTrue(response.getRates().containsKey("USD"));
    }
}