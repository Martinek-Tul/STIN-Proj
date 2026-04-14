package service;

import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class LoggingService {

    private static final String LOG_FILE = "logs.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void logInfo(String message) {
        writeLog("INFO", message);
    }

    public void logError(String message) {
        writeLog("ERROR", message);
    }

    public void logError(String message, Throwable e) {
        writeLog("ERROR", message + " | Exception: " + e.getClass().getSimpleName() + ": " + e.getMessage());
    }

    private void writeLog(String level, String message) {
        String line = "[" + LocalDateTime.now().format(FORMATTER) + "] [" + level + "] " + message;
        System.out.println(line);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException ex) {
            System.err.println("Nepodařilo se zapsat log do souboru: " + ex.getMessage());
        }
    }
}