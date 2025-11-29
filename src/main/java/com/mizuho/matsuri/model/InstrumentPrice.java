package com.mizuho.matsuri.model;

import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

public record InstrumentPrice(String isin,
                              String currency,
                              double price,
                              LocalDateTime priceDate,
                              String providerId,
                              String instrumentName) implements Serializable {
}
