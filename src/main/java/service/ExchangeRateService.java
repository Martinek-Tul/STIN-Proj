package service;

import model.ExchangeRateResponse;
import model.ExchangeRateResponseDate;
import model.UserSettings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeRateService {
    @Autowired
    private LoggingService loggingService;

    private final RestTemplate restTemplate = new RestTemplate();

    public ExchangeRateResponse getCurrentRates(UserSettings settings){
        String url = "https://api.frankfurter.app/latest?base="
                +settings.getBaseCurrency()
                +"&symbols="
                +String.join(",", settings.getSelectedCurrencies());
        loggingService.logInfo("Načítám kurzy: "+url);
        ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);
        if (response == null || response.getRates() == null) {
            loggingService.logError("API vratílo null pro: "+url);
            throw new RuntimeException("Nepodařilo se načíst kurzy z API");
        }
        return response;
    }
    public ExchangeRateResponseDate getCurrentRatesDates(UserSettings settings, String dateFrom, String dateTo){
        String url = "https://api.frankfurter.app/"
                +dateFrom + ".."+dateTo
                +"?base="
                +settings.getBaseCurrency()
                +"&symbols="
                +String.join(",", settings.getSelectedCurrencies());
        loggingService.logInfo("Načítám kurzy: "+url);
        ExchangeRateResponseDate response = restTemplate.getForObject(url, ExchangeRateResponseDate.class);
        if (response == null || response.getRates() == null) {
            loggingService.logError("API vratílo null pro: "+url);
            throw new RuntimeException("Nepodařilo se načíst kurzy z API");
        }
        return response;
    }
}
