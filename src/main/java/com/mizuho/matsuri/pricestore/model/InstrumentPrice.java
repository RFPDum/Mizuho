package com.mizuho.matsuri.pricestore.model;

import java.time.LocalDateTime;

public record InstrumentPrice(String isin,
                              String currency,
                              double price,
                              LocalDateTime priceDate,
                              String vendorId) implements Comparable<InstrumentPrice> {
    @Override
    public int compareTo(InstrumentPrice instrumentPrice) {
        final int dateCompare = this.priceDate.compareTo(instrumentPrice.priceDate);
        if (dateCompare != 0) {
            return dateCompare;
        }
        final int isinCompare = this.isin.compareTo(instrumentPrice.isin);
        if (isinCompare == 0) {
            return isinCompare;
        }
        int ccyCompare = this.currency.compareTo(instrumentPrice.currency);
        if (ccyCompare == 0) {
            return ccyCompare;
        }
        return this.vendorId.compareTo(instrumentPrice.vendorId);
    }
}
