package com.mizuho.matsuri.rest.util;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;
import com.mizuho.matsuri.rest.feed.exception.PriceValidationException;
import com.mizuho.matsuri.rest.feed.model.InstrumentPriceInfo;
import com.mizuho.matsuri.rest.query.model.InstrumentPriceUpdateRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InstrumentPriceAdapterTest {
    private static final String        ISIN       = "US456456465";
    private static final String        VENDOR     = "Bloomberg";
    private static final String        USD        = "USD";
    private static final String        GBP        = "USD";
    private static final Double        PRICE      = 159d;
    private static final LocalDateTime DATE       = LocalDateTime.now();
    private static final String        PRICE_DATE = DATE.toString();

    private final InstrumentPriceAdapter instrumentPriceAdapter = new InstrumentPriceAdapter();

    @Test
    void should_convert_InstrumentPrice_to_InstrumentPriceInfo() {
        // Given
        final InstrumentPrice     instrumentPrice   = new InstrumentPrice(ISIN, USD, PRICE, DATE, VENDOR);
        final InstrumentPriceInfo expectedPriceInfo = new InstrumentPriceInfo(VENDOR, ISIN, USD, PRICE, DATE);

        // When
        final InstrumentPriceInfo instrumentPriceInfo = instrumentPriceAdapter.toInstrumentPriceInfo(instrumentPrice);

        // Then
        assertThat(instrumentPriceInfo).isEqualTo(expectedPriceInfo);
    }

    @Test
    void should_convert_a_collection_of_InstrumentPrice_to_an_array_of_InstrumentPriceInfo() {
        // Given
        final List<InstrumentPrice> instrumentPrices = List.of(new InstrumentPrice(ISIN, USD, PRICE, DATE, VENDOR),
                                                               new InstrumentPrice(ISIN, GBP, PRICE, DATE, VENDOR));

        final InstrumentPriceInfo[] expectedPriceInfos = {new InstrumentPriceInfo(VENDOR, ISIN, USD, PRICE, DATE),
                                                          new InstrumentPriceInfo(VENDOR, ISIN, GBP, PRICE, DATE)};

        // When
        final InstrumentPriceInfo[] instrumentPriceInfos = instrumentPriceAdapter.toInstrumentPriceInfos(instrumentPrices);

        // Then
        assertThat(instrumentPriceInfos).isEqualTo(expectedPriceInfos);
    }

    @Test
    void should_convert_price_update_request_to_InstrumentPrice() throws PriceValidationException {
        // Given
        final InstrumentPriceUpdateRequest priceUpdateRequest
                = new InstrumentPriceUpdateRequest(VENDOR, ISIN, USD, PRICE, PRICE_DATE);
        final InstrumentPrice expected
                = new InstrumentPrice(ISIN, USD, PRICE, DATE, VENDOR);

        // When
        final InstrumentPrice instrumentPrice = instrumentPriceAdapter.toInstrumentPrice(priceUpdateRequest);

        // Then
        assertThat(instrumentPrice).isEqualTo(expected);
    }
}