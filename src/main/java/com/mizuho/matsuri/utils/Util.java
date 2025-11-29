package com.mizuho.matsuri.utils;

import com.mizuho.matsuri.model.InstrumentPrice;

import java.time.LocalDateTime;

public class Util {
    /**
     * Private constructor to prevent instantiation.
     */
    private Util() {};

    public static LocalDateTime getCutOffDate(int ageInDays) {
        return LocalDateTime.now().minusDays(ageInDays);
    }

    public static boolean isStale(InstrumentPrice price, LocalDateTime cutOff) {
        return price.priceDate().isBefore(cutOff);
    }
}
