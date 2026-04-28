package org.example.stinproj.service;

import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class LoggingServiceTest {

    private final LoggingService loggingService = new LoggingService();

    @Test
    public void testLogInfo() {
        assertDoesNotThrow(() -> loggingService.logInfo("Testovací info zpráva"));
        File logFile = new File("logs.txt");
        assertTrue(logFile.exists(), "Soubor logs.txt by měl existovat");
    }

    @Test
    public void testLogError() {
        assertDoesNotThrow(() -> loggingService.logError("Testovací error zpráva"));
    }

    @Test
    public void testLogErrorWithException() {
        Exception testEx = new RuntimeException("Něco se pokazilo");
        assertDoesNotThrow(() -> loggingService.logError("Chyba s výjimkou", testEx));
    }

    @Test
    public void testWriteLogFilePersistence() {
        File logFile = new File("logs.txt");
        long initialSize = logFile.exists() ? logFile.length() : 0;
        loggingService.logInfo("Zápis pro kontrolu velikosti");
        assertTrue(logFile.length() > initialSize, "Velikost logu by se měla po zápisu zvětšit");
    }
}