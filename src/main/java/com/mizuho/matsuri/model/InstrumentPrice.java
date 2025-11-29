package com.mizuho.matsuri.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public record InstrumentPrice(String isin,
                              String currency,
                              double price,
                              LocalDateTime priceDate,
                              String providerId) implements Serializable {
}
