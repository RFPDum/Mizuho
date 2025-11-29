package com.mizuho.matsuri.testutils;

import com.mizuho.matsuri.model.InstrumentPrice;

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

    public static final String ISIN_1             = "JP3885780001";
    public static final String ISIN_2             = "GB0030913577";
    public static final String ISIN_3             = "US0378331005";
    public static final String PRICE_PROVIDER_1   = "Bloomberg";
    public static final String PRICE_PROVIDER_2   = "Mizuho";
    public static final String USD_CCY            = "USD";
    public static final String GBP_CCY            = "GBP";

    private static final String[] ISINS           = {ISIN_1, ISIN_2, ISIN_3};
    private static final String[] PROVS           = {PRICE_PROVIDER_1, PRICE_PROVIDER_2};

    private InstrumentPriceUtils() {};

    public static List<InstrumentPrice> ofInstrumentPricesForIsin(String isin) {
        return Arrays.stream(PROVS).map(p -> ofInstrumentPrice(isin, p)).collect(toList());
    }

    public static List<InstrumentPrice> ofInstrumentPricesForProvider(String providerId) {
        return Arrays.stream(ISINS).map(i -> ofInstrumentPrice(i, providerId)).collect(toList());
    }

    public static InstrumentPrice ofInstrumentPrice(String isin, String providerId) {
        return ofInstrumentPrice(isin, providerId, LocalDateTime.now());
    }

    public static InstrumentPrice ofInstrumentPrice(String isin, String providerId, LocalDateTime priceTime) {
        return new InstrumentPrice(isin, ofRandomCCY(), ofRandomPriceAmount(), priceTime, providerId);
    }

    private static double ofRandomPriceAmount() {
        return Math.random() * 1000;
    }

    private static String ofRandomCCY() {
        return Math.random() > .5 ? GBP_CCY : USD_CCY;
    }
}
