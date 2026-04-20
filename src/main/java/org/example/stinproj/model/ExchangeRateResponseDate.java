package org.example.stinproj.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ExchangeRateResponseDate {
    private Map<String, Map<String, Double>> rates;

}
