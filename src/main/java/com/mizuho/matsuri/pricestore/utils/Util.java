package com.mizuho.matsuri.pricestore.utils;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;

import java.time.LocalDateTime;

public class Util {
    /**
     * Private constructor to prevent instantiation.
     */
    private Util() {};

    /**
     * Compute data retention cutoff date from the data retention period passed.
     *
     * @param retentionTimeInDays number of days to keep data for
     * @return cut-off time and date
     */
    public static LocalDateTime getCutOffDate(int retentionTimeInDays) {
        return LocalDateTime.now().minusDays(retentionTimeInDays);
    }

    /**
     * Compute whether the passed price has exceeded the passed cut-off date
     * @param price price to check
     * @param cutOff cut-off date
     * @return true if the price date is before the cut-off
     */
    public static boolean isStale(InstrumentPrice price, LocalDateTime cutOff) {
        return price.priceDate().isBefore(cutOff);
    }
}
