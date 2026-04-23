package org.example.stinproj.controller;

import org.example.stinproj.model.ExchangeRateResponse;
import org.example.stinproj.model.ExchangeRateResponseDate;
import org.example.stinproj.model.UserSettings;
import org.example.stinproj.service.ExchangeRateService;
import org.example.stinproj.service.LoggingService;
import org.example.stinproj.service.UserSettingsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RatesController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RatesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @MockBean
    private UserSettingsService userSettingsService;

    @MockBean
    private LoggingService loggingService;

    @Test
    public void testGetRatesSuccess() throws Exception {
        ExchangeRateResponse response = new ExchangeRateResponse();
        response.setRates(Map.of("USD", 1.08));

        when(exchangeRateService.getCurrentRates(any(UserSettings.class))).thenReturn(response);

        mockMvc.perform(get("/api/rates")
                        .param("base", "EUR")
                        .param("symbols", "USD"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetDateAverageSuccess() throws Exception {
        ExchangeRateResponseDate responseDate = new ExchangeRateResponseDate();
        responseDate.setRates(new HashMap<>());

        when(exchangeRateService.getCurrentRatesDates(any(UserSettings.class), anyString(), anyString()))
                .thenReturn(responseDate);

        mockMvc.perform(get("/api/date")
                        .param("base", "EUR")
                        .param("symbols", "USD")
                        .param("dateFrom", "2025-01-01")
                        .param("dateTo", "2025-01-02"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetStrongestSuccess() throws Exception {
        ExchangeRateResponse response = new ExchangeRateResponse();
        response.setRates(Map.of("USD", 1.1, "CZK", 25.0));

        when(exchangeRateService.getCurrentRates(any(UserSettings.class))).thenReturn(response);

        mockMvc.perform(get("/api/strongest")
                        .param("base", "EUR")
                        .param("symbols", "USD,CZK"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetSettingsSuccess() throws Exception {
        UserSettings settings = new UserSettings("EUR", List.of("USD"));
        when(userSettingsService.loadSettings()).thenReturn(settings);

        mockMvc.perform(get("/api/settings"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSaveSettingsSuccess() throws Exception {
        mockMvc.perform(get("/api/settings/save")
                        .param("base", "EUR")
                        .param("symbols", "USD,CZK"))
                .andExpect(status().isOk());
    }
}