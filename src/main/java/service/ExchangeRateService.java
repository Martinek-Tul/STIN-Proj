package service;

import model.ExchangeRateResponse;
import model.ExchangeRateResponseDate;
import model.UserSettings;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeRateService {
    private final RestTemplate restTemplate = new RestTemplate();

    public ExchangeRateResponse getCurrentRates(UserSettings settings){
        String url = "https://api.frankfurter.app/latest?base="
                +settings.getBaseCurrency()
                +"&symbols="
                +String.join(",", settings.getSelectedCurrencies());
        ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);
        if (response == null || response.getRates() == null) {
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
        ExchangeRateResponseDate response = restTemplate.getForObject(url, ExchangeRateResponseDate.class);
        if (response == null || response.getRates() == null) {
            throw new RuntimeException("Nepodařilo se načíst kurzy z API");
        }
        return response;
    }
}
