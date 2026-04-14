package service;

import model.UserSettings;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserSettingsService {
    @Autowired
    private LoggingService loggingService;

    private final String file = "settings.json";

    public UserSettings loadSettings(){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String baseCurrency = reader.readLine();
            String currenciesLine = reader.readLine();
            reader.close();

            loggingService.logInfo("Nastavení načteno");
            List<String> selectedCurrencies = List.of(currenciesLine.split(","));
            return new UserSettings(baseCurrency, selectedCurrencies);
        } catch (FileNotFoundException e) {
            loggingService.logError("Soubor settings.json nebyl vytvořen, použito vychozí nastavení");
            return new UserSettings("EUR", List.of());
        } catch (IOException e) {
            loggingService.logError("Chyba při načítaní nastavení",e);
            throw new RuntimeException(e);
        }
    }

    public void saveSettings(UserSettings settings){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(settings.getBaseCurrency());
            writer.newLine();
            writer.write(String.join(",", settings.getSelectedCurrencies()));
            writer.close();
            loggingService.logInfo("Nastavení bylo uloženo");
        } catch (IOException e) {
            loggingService.logError("Chyba při ukládání nastavení",e);
            throw new RuntimeException(e);
        }
    }
}
