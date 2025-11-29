package com.mizuho.matsuri.data.impl;


import com.mizuho.matsuri.model.InstrumentPrice;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.List;

import static com.mizuho.matsuri.testutils.InstrumentPriceUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class PricePersistenceServiceTest {

    @Test
    public void should_only_retrieve_non_stale_prices_when_retrieveInstrumentPrices_is_called() {
        // Given
        final InstrumentPrice price1             = ofInstrumentPrice(ISIN_1, PRICE_PROVIDER_1, DAY);
        final InstrumentPrice price2             = ofInstrumentPrice(ISIN_1, PRICE_PROVIDER_2, DAY);
        final InstrumentPrice price3             = ofInstrumentPrice(ISIN_2, PRICE_PROVIDER_1, DAY_MIN_1);
        final InstrumentPrice price4             = ofInstrumentPrice(ISIN_2, PRICE_PROVIDER_2, DAY_MIN_5);
        final InstrumentPrice price5             = ofInstrumentPrice(ISIN_1, PRICE_PROVIDER_1, DAY_MIN_10);
        final InstrumentPrice price6             = ofInstrumentPrice(ISIN_2, PRICE_PROVIDER_2, DAY_MIN_11);
        final List<InstrumentPrice> prices       = List.of(price1, price2, price3, price4, price5, price6);
        final List<InstrumentPrice> expRetrieved = List.of(price1, price2, price3, price4);
        final PricePersistenceService service        = ofPriceStorageService(prices);

        // When
        final Collection<InstrumentPrice> retrieved = service.retrieveInstrumentPrices(10);

        // Then
        assertThat(retrieved).containsExactlyInAnyOrderElementsOf(expRetrieved);
    }

    @Test
    public void should_store_prices_when_storeInstrumentPrice_is_called() {
        // Given
        final InstrumentPrice price1      = ofInstrumentPrice(ISIN_1, PRICE_PROVIDER_1, DAY);
        final InstrumentPrice price2      = ofInstrumentPrice(ISIN_1, PRICE_PROVIDER_2, DAY);
        final PricePersistenceService service = ofPriceStorageService();

        // When
        service.storeInstrumentPrice(price1);
        service.storeInstrumentPrice(price2);

        // Then
        assertThat(getInternalPriceList(service)).containsExactlyInAnyOrder(price1, price2);
    }

    private PricePersistenceService ofPriceStorageService() {
        return new PricePersistenceService();
    }

    private PricePersistenceService ofPriceStorageService(List<InstrumentPrice> prices) {
        final PricePersistenceService service = ofPriceStorageService();
        getInternalPriceList(service).addAll(prices);
        return service;
    }

    private Collection<InstrumentPrice> getInternalPriceList(PricePersistenceService service) {
        return (Collection<InstrumentPrice>) ReflectionTestUtils.getField(service, "priceStore");
    }
}