package com.mizuho.matsuri.rest.feed.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class InstrumentPriceInfo {
    private final String isin;
    private final String currency;
    private final Double price;
    private final LocalDateTime priceDate;
    private final String providerId;
}
