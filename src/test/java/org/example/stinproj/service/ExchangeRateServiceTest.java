package org.example.stinproj.service;

import org.example.stinproj.model.ExchangeRateResponse;
import org.example.stinproj.model.ExchangeRateResponseDate;
import org.example.stinproj.model.UserSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceTest {

    @Mock
    private LoggingService loggingService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(exchangeRateService, "restTemplate", restTemplate);
    }

    @Test
    public void testGetCurrentRates_Success() {
        UserSettings settings = new UserSettings("CZK", List.of("USD", "EUR"));
        ExchangeRateResponse mockResponse = new ExchangeRateResponse();
        mockResponse.setRates(Map.of("USD", 0.04, "EUR", 0.03));

        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class)))
                .thenReturn(mockResponse);

        ExchangeRateResponse result = exchangeRateService.getCurrentRates(settings);

        assertNotNull(result);
        assertEquals(2, result.getRates().size());
        verify(loggingService).logInfo(anyString());
    }

    @Test
    public void testGetCurrentRates_ThrowsExceptionOnNull() {
        UserSettings settings = new UserSettings("CZK", List.of("USD"));
        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class)))
                .thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            exchangeRateService.getCurrentRates(settings);
        });
        verify(loggingService).logError(anyString());
    }

    @Test
    public void testGetCurrentRatesDates_Success() {
        UserSettings settings = new UserSettings("CZK", List.of("USD"));
        ExchangeRateResponseDate mockResponse = new ExchangeRateResponseDate();
        Map<String, Map<String, Double>> rates = new HashMap<>();
        rates.put("2024-01-01", Map.of("USD", 0.04));
        mockResponse.setRates(rates);

        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponseDate.class)))
                .thenReturn(mockResponse);

        ExchangeRateResponseDate result = exchangeRateService.getCurrentRatesDates(settings, "2024-01-01", "2024-01-02");

        assertNotNull(result);
        assertTrue(result.getRates().containsKey("2024-01-01"));
        verify(loggingService).logInfo(anyString());
    }
}