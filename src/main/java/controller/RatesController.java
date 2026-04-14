package controller;

import model.ExchangeRateResponse;
import model.ExchangeRateResponseDate;
import model.UserSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.CurrencyAnalyzer;
import service.ExchangeRateService;
import service.LoggingService;
import service.UserSettingsService;

import java.util.List;
import java.util.Map;

@RestController
    @RequestMapping("/api")
    public class RatesController{
    @Autowired
    private ExchangeRateService exchangeRateService;
    private final CurrencyAnalyzer currencyAnalyzer = new CurrencyAnalyzer();
    @Autowired
    private UserSettingsService userSettingsService;
    @Autowired
    private LoggingService loggingService;

    @GetMapping("/rates")
    public ExchangeRateResponse getRates(
            @RequestParam String base,
            @RequestParam List<String> currencies){
        try{
            UserSettings settings = new UserSettings(base, currencies);
            return exchangeRateService.getCurrentRates(settings);
        } catch (Exception e) {
            loggingService.logError("Chyba v /api/rates ",e);
            throw e;
        }
    }

    @GetMapping("/date")
    public Map<String, Double> getRatesWithDates(
            @RequestParam String base,
            @RequestParam List<String> currencies,
            @RequestParam String dateFrom,
            @RequestParam String dateTo
    ){
        try{
            UserSettings settings = new UserSettings(base, currencies);
            ExchangeRateResponseDate responseDate = exchangeRateService.getCurrentRatesDates(settings, dateFrom, dateTo);
            return currencyAnalyzer.calculateAverage(responseDate);
        } catch (Exception e) {
            loggingService.logError("Chyba v /api/date ",e);
            throw e;
        }
    }

}
