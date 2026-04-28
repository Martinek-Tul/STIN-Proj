package org.example.stinproj.model;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserSettingsTest {

    @Test
    public void testValidUserSettings() {
        String base = "CZK";
        List<String> selected = List.of("USD", "EUR");

        UserSettings settings = new UserSettings(base, selected);

        assertEquals(base, settings.getBaseCurrency());
        assertEquals(selected, settings.getSelectedCurrencies());
    }

    @Test
    public void testInvalidBaseCurrency() {
        List<String> selected = List.of("USD");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new UserSettings("XYZ", selected);
        });

        assertTrue(exception.getMessage().contains("Nepodporovaná měna"));
    }

    @Test
    public void testInvalidSelectedCurrencies() {
        List<String> selected = List.of("USD", "ABC");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new UserSettings("CZK", selected);
        });

        assertTrue(exception.getMessage().contains("Seznam obsahuje nepodporovanou měnu"));
    }

    @Test
    public void testSetters() {
        UserSettings settings = new UserSettings("CZK", List.of("USD"));

        settings.setBaseCurrency("EUR");
        settings.setSelectedCurrencies(List.of("GBP"));

        assertEquals("EUR", settings.getBaseCurrency());
        assertEquals(List.of("GBP"), settings.getSelectedCurrencies());
    }
}