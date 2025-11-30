package com.mizuho.matsuri.rest.feed.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class InstrumentPriceInfo {
    private final String        vendorId;
    private final String        isin;
    private final String        currency;
    private final Double        price;
    private final LocalDateTime priceDate;
}
