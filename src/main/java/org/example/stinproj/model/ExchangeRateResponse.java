package org.example.stinproj.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ExchangeRateResponse {
    private Map<String, Double> rates;
}
