package org.example.stinproj.controller;

import org.example.stinproj.model.ExchangeRateResponse;
import org.example.stinproj.model.ExchangeRateResponseDate;
import org.example.stinproj.model.UserSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.example.stinproj.service.CurrencyAnalyzer;
import org.example.stinproj.service.ExchangeRateService;
import org.example.stinproj.service.LoggingService;
import org.example.stinproj.service.UserSettingsService;

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
            @RequestParam List<String> symbols){
        try{
            UserSettings settings = new UserSettings(base, symbols);
            return exchangeRateService.getCurrentRates(settings);
        } catch (Exception e) {
            loggingService.logError("Chyba v /api/rates ",e);
            throw e;
        }
    }

    @GetMapping("/date")
    public Map<String, Double> getRatesWithDates(
            @RequestParam String base,
            @RequestParam List<String> symbols,
            @RequestParam String dateFrom,
            @RequestParam String dateTo
    ){
        try{
            UserSettings settings = new UserSettings(base, symbols);
            ExchangeRateResponseDate responseDate = exchangeRateService.getCurrentRatesDates(settings, dateFrom, dateTo);
            return currencyAnalyzer.calculateAverage(responseDate);
        } catch (Exception e) {
            loggingService.logError("Chyba v /api/date ",e);
            throw e;
        }
    }

    @GetMapping("/strongest")
    public Map.Entry<String, Double> getStrongest(
            @RequestParam String base,
            @RequestParam List<String> symbols) {
        try {
            UserSettings settings = new UserSettings(base, symbols);
            ExchangeRateResponse response = exchangeRateService.getCurrentRates(settings);
            return currencyAnalyzer.findStrongest(response);
        } catch (Exception e) {
            loggingService.logError("Chyba v /api/strongest", e);
            throw e;
        }
    }

    @GetMapping("/weakest")
    public Map.Entry<String, Double> getWeakest(
            @RequestParam String base,
            @RequestParam List<String> symbols){
        try{
            UserSettings settings = new UserSettings(base,symbols);
            ExchangeRateResponse response = exchangeRateService.getCurrentRates(settings);
            return currencyAnalyzer.findWeakest(response);
        }catch (Exception e){
            loggingService.logError("Cyhba v /api/weakest", e);
            throw e;
        }
    }

    @GetMapping("/settings")
    public UserSettings getSettings(){
        try{
            return userSettingsService.loadSettings();
        } catch (Exception e) {
            loggingService.logError("Chyba v /api/settings", e);
            throw e;
        }
    }

    @GetMapping("/settings/save")
    public void saveSettings(@RequestParam String base,
                             @RequestParam List<String> symbols){
        try {
            UserSettings settings = new UserSettings(base, symbols);
            userSettingsService.saveSettings(settings);
        }catch (Exception e){
            loggingService.logError("Chyba v /api/settings", e);
            throw e;
        }
    }
}
