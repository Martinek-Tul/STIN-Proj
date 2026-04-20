package org.example.stinproj.controller;

import org.example.stinproj.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private LoggingService loggingService;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAll(Exception ex) {
        loggingService.logError("Chyba aplikace: ", ex);

        Map<String, String> error = new HashMap<>();
        error.put("message", "Tato cesta neexistuje nebo nastala chyba na serveru.");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}