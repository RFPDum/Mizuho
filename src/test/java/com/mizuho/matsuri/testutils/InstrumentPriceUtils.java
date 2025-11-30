package com.mizuho.matsuri.testutils;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class InstrumentPriceUtils {
    public static final LocalDateTime DAY        = LocalDateTime.now();
    public static final LocalDateTime DAY_MIN_1  = DAY.minusDays(1);
    public static final LocalDateTime DAY_MIN_5  = DAY.minusDays(5);
    public static final LocalDateTime DAY_MIN_10 = DAY.minusDays(10);
    public static final LocalDateTime DAY_MIN_11 = DAY.minusDays(10);

    public static final String ISIN_1            = "JP3885780001";
    public static final String ISIN_2            = "GB0030913577";
    public static final String ISIN_3            = "US0378331005";
    public static final String VENDOR_1          = "Bloomberg";
    public static final String VENDOR_2          = "Mizuho";
    public static final String USD_CCY           = "USD";
    public static final String GBP_CCY           = "GBP";

    private static final String[] ISINS          = {ISIN_1, ISIN_2, ISIN_3};
    private static final String[] VENDORS        = {VENDOR_1, VENDOR_2};

    private InstrumentPriceUtils() {};

    public static List<InstrumentPrice> ofInstrumentPricesForIsin(String isin) {
        return Arrays.stream(VENDORS).map(p -> ofInstrumentPrice(isin, p)).collect(toList());
    }

    public static List<InstrumentPrice> ofInstrumentPricesForVendor(String vendorId) {
        return Arrays.stream(ISINS).map(i -> ofInstrumentPrice(i, vendorId)).collect(toList());
    }

    public static InstrumentPrice ofInstrumentPrice(String isin, String vendorId) {
        return ofInstrumentPrice(isin, vendorId, LocalDateTime.now());
    }

    public static InstrumentPrice ofInstrumentPrice(String isin, String vendorId, LocalDateTime priceTime) {
        return new InstrumentPrice(isin, ofRandomCCY(), ofRandomPriceAmount(), priceTime, vendorId);
    }

    private static double ofRandomPriceAmount() {
        return Math.random() * 1000;
    }

    private static String ofRandomCCY() {
        return Math.random() > .5 ? GBP_CCY : USD_CCY;
    }
}
