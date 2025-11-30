package com.mizuho.matsuri.pricestore.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public record InstrumentPrice(String isin,
                              String currency,
                              double price,
                              LocalDateTime priceDate,
                              String vendorId) implements Serializable {
}
