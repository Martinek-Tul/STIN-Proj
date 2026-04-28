package org.example.stinproj.service;

import org.example.stinproj.model.UserSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserSettingsServiceTest {

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private UserSettingsService userSettingsService;

    @BeforeEach
    public void setUp() {
        File file = new File("settings.json");
        if (file.exists()) {
            boolean deleted = file.delete();
        }
    }

    @Test
    public void testSaveAndLoadSettings_Success() {
        UserSettings settings = new UserSettings("USD", List.of("EUR", "CZK"));
        assertDoesNotThrow(() -> userSettingsService.saveSettings(settings));

        UserSettings loaded = userSettingsService.loadSettings();

        assertNotNull(loaded);
        assertEquals("USD", loaded.getBaseCurrency());
        assertEquals(2, loaded.getSelectedCurrencies().size());
        assertTrue(loaded.getSelectedCurrencies().contains("CZK"));

        verify(loggingService).logInfo("Nastavení bylo uloženo");
        verify(loggingService).logInfo("Nastavení načteno");
    }

    @Test
    public void testLoadSettings_FileNotFound_ShouldReturnDefault() {
        File file = new File("settings.json");
        file.delete();

        UserSettings result = userSettingsService.loadSettings();

        assertNotNull(result);
        assertEquals("EUR", result.getBaseCurrency());
        assertTrue(result.getSelectedCurrencies().isEmpty());

        verify(loggingService).logError(anyString());
    }
}