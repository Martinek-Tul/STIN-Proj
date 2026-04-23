package org.example.stinproj.controller;

import org.example.stinproj.service.ExchangeRateService;
import org.example.stinproj.service.LoggingService;
import org.example.stinproj.service.UserSettingsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({RatesController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @MockBean
    private UserSettingsService userSettingsService;

    @MockBean
    private LoggingService loggingService;

    @Test
    public void testHandleAllExceptions_ShouldReturnNotFound() throws Exception {
        when(exchangeRateService.getCurrentRates(any()))
                .thenThrow(new RuntimeException("Simulovaná systémová chyba"));

        mockMvc.perform(get("/api/rates"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Tato cesta neexistuje nebo nastala chyba na serveru."));

        verify(loggingService).logError(anyString(), any(Exception.class));
    }
}