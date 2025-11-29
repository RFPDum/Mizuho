package com.mizuho.matsuri.service.impl;


import com.mizuho.matsuri.model.InstrumentPrice;
import com.mizuho.matsuri.service.impl.PriceIndexer.PriceSet;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class PriceIndexerTest {
    private static final String ISIN_1            = "JP3885780001";
    private static final String ISIN_2            = "GB0030913577";
    private static final String ISIN_3            = "US0378331005";
    private static final String PRICE_PROVIDER_1  = "Bloomberg";
    private static final String PRICE_PROVIDER_2  = "Mizuho";
    private static final String USD_CCY           = "USD";
    private static final String GBP_CCY           = "GBP";

    private static final String[] ISINS           = {ISIN_1, ISIN_2, ISIN_3};
    private static final String[] PROVS           = {PRICE_PROVIDER_1, PRICE_PROVIDER_2};

    private static final LocalDateTime DAY        = LocalDateTime.now();
    private static final LocalDateTime DAY_MIN_1  = DAY.minusDays(1);
    private static final LocalDateTime DAY_MIN_5  = DAY.minusDays(5);
    private static final LocalDateTime DAY_MIN_10 = DAY.minusDays(10);
    private static final LocalDateTime DAY_MIN_11 = DAY.minusDays(10);

    private static final int DATA_RETENTION_DAYS = 10;

    @Test
    public void should_return_an_empty_collection_when_attempting_to_get_prices_from_a_provider_when_none_are_present() {
        // Given
        final PriceIndexer indexer = ofPriceIndexer();
        final String aProviderId   = PRICE_PROVIDER_1;

        // When
        final Collection<InstrumentPrice> providerPrices = indexer.getProviderPrices(aProviderId);

        // Then
        assertThat(providerPrices).isEmpty();
    }

    @Test
    public void should_return_an_empty_collection_when_attempting_to_get_prices_for_an_instrument_when_none_are_present() {
        // Given
        final PriceIndexer indexer = ofPriceIndexer();
        final String aProviderId   = "Bloomberg";

        // When
        final Collection<InstrumentPrice> providerPrices = indexer.getProviderPrices(aProviderId);

        // Then
        assertThat(providerPrices).isEmpty();
    }

    @Test
    public void should_return_the_instrument_price_from_a_provider_when_there_is_only_one_price_for_that_provider() {
        // Given
        final InstrumentPrice price   = ofInstrumentPrice(ISIN_1, PRICE_PROVIDER_1);
        final PriceIndexer    indexer = ofPriceIndexer(price);

        // When
        final Collection<InstrumentPrice> prices = indexer.getProviderPrices(PRICE_PROVIDER_1);

        // Then
        assertThat(prices).usingFieldByFieldElementComparator().containsOnly(price);
    }

    @Test
    public void should_return_the_instrument_price_for_a_given_isin_when_there_is_only_one_price_for_that_instrument() {
        // Given
        final InstrumentPrice price   = ofInstrumentPrice(ISIN_1, PRICE_PROVIDER_1);
        final PriceIndexer    indexer = ofPriceIndexer(price);

        // When
        final Collection<InstrumentPrice> prices = indexer.getInstrumentPrices(ISIN_1);

        // Then
        assertThat(prices).containsOnly(price);
    }

    @Test
    public void should_store_all_prices_for_a_given_instrument_id() {
        // Give
        final List<InstrumentPrice> isin1Prices = ofInstrumentPricesForIsin(ISIN_1);
        final List<InstrumentPrice> isin2Prices = ofInstrumentPricesForIsin(ISIN_2);

        final PriceIndexer indexer = ofPriceIndexer();

        // When
        for(InstrumentPrice price : isin1Prices) {
            indexer.indexPrice(price);
        }
        for(InstrumentPrice price : isin2Prices) {
            indexer.indexPrice(price);
        }

        // Then
        assertThat(indexer.getInstrumentPrices(ISIN_1)).containsExactlyInAnyOrderElementsOf(isin1Prices);
        assertThat(indexer.getInstrumentPrices(ISIN_2)).containsExactlyInAnyOrderElementsOf(isin2Prices);
    }

    @Test
    public void should_store_all_prices_for_a_given_price_provider() {
        // Give
        final List<InstrumentPrice> pr1Prices = ofInstrumentPricesForProvider(PRICE_PROVIDER_1);
        final List<InstrumentPrice> pr3Prices = ofInstrumentPricesForProvider(PRICE_PROVIDER_2);

        final PriceIndexer indexer = ofPriceIndexer();

        // When
        for(InstrumentPrice price : pr1Prices) {
            indexer.indexPrice(price);
        }
        for(InstrumentPrice price : pr3Prices) {
            indexer.indexPrice(price);
        }

        // Then
        assertThat(indexer.getProviderPrices(PRICE_PROVIDER_1)).containsExactlyInAnyOrderElementsOf(pr1Prices);
        assertThat(indexer.getProviderPrices(PRICE_PROVIDER_2)).containsExactlyInAnyOrderElementsOf(pr3Prices);
    }

    @Test
    public void should_rebuild_index_with_passed_prices_when_rebuild_index_is_called() {
        // Given
        final PriceIndexer indexer = ofPriceIndexer();
        final List<InstrumentPrice> prices = ofInstrumentPricesForProvider(PRICE_PROVIDER_1);
        final Map<String, List<InstrumentPrice>> pricesByIsin = prices.stream().collect(Collectors.groupingBy(InstrumentPrice::isin));
        assertThat(indexer.isEmpty()).isTrue();

        // When
        indexer.rebuildIndex(prices);

        // Then
        assertThat(indexer.getProviderPrices(PRICE_PROVIDER_1)).containsExactlyInAnyOrderElementsOf(prices);
        pricesByIsin.forEach((key, value) -> assertThat(indexer.getInstrumentPrices(key)).containsExactlyInAnyOrderElementsOf(value));
    }

    @Test
    public void should_return_an_empty_collection_when_calling_getProviderPrices_for_a_non_existent_provider() {
        // Given
        final PriceIndexer indexer = ofPriceIndexer();
        final List<InstrumentPrice> prices = ofInstrumentPricesForProvider(PRICE_PROVIDER_1);
        indexer.rebuildIndex(prices);

        // When
        final Collection<InstrumentPrice> providerPrices = indexer.getProviderPrices("NotPresent");

        // Then
        assertThat(providerPrices).isEmpty();
    }

    @Test
    public void should_return_an_empty_collection_when_calling_getInstrumentPrices_for_a_non_existent_provider() {
        // Given
        final PriceIndexer indexer = ofPriceIndexer();
        final List<InstrumentPrice> prices = ofInstrumentPricesForIsin(ISIN_3);
        indexer.rebuildIndex(prices);

        // When
        final Collection<InstrumentPrice> providerPrices = indexer.getInstrumentPrices("NotPresent");

        // Then
        assertThat(providerPrices).isEmpty();
    }

    @Test
    public void should_purge_all_prices_older_than_30_days_when_purge_is_called() {
        // Given
        final PriceIndexer indexer = ofPriceIndexer();
        final InstrumentPrice price1 = ofProvider2InstrumentPrice(DAY, ISIN_1);
        final InstrumentPrice price2 = ofProvider2InstrumentPrice(DAY_MIN_1, ISIN_2);
        final InstrumentPrice price3 = ofProvider2InstrumentPrice(DAY_MIN_5, ISIN_1);
        final InstrumentPrice price4 = ofProvider2InstrumentPrice(DAY_MIN_10, ISIN_1);
        final InstrumentPrice price5 = ofProvider2InstrumentPrice(DAY_MIN_11, ISIN_1);
        final List<InstrumentPrice> prices = List.of(
                price1,
                price2,
                price3,
                price4,
                price5);
        indexer.rebuildIndex(prices);
        assertThat(indexer.getProviderPrices(PRICE_PROVIDER_2)).containsExactlyInAnyOrderElementsOf(prices);

        final List<InstrumentPrice> expectedLeftOver = List.of(price1, price2, price3);

        // When
        indexer.purge();

        // Then
        assertThat(indexer.getProviderPrices(PRICE_PROVIDER_2)).containsExactlyInAnyOrderElementsOf(expectedLeftOver);
    }

    private double ofRandomPrice() {
        return Math.random() * 1000;
    }

    private List<InstrumentPrice> ofInstrumentPricesForIsin(String isin) {
        return Arrays.stream(PROVS).map(p -> ofInstrumentPrice(isin, p)).collect(toList());
    }

    private List<InstrumentPrice> ofInstrumentPricesForProvider(String providerId) {
        return Arrays.stream(ISINS).map(i -> ofInstrumentPrice(i, providerId)).collect(toList());
    }

    private InstrumentPrice ofInstrumentPrice(String isin, String providerId) {
        return new InstrumentPrice(isin, USD_CCY, ofRandomPrice(), LocalDateTime.now(), providerId,isin + " instrument");
    }

    private InstrumentPrice ofProvider2InstrumentPrice(LocalDateTime priceTime, String isin) {
        return new InstrumentPrice(isin, GBP_CCY, ofRandomPrice(), priceTime, PRICE_PROVIDER_2,"Equity " + isin);
    }

    private PriceIndexer ofPriceIndexer() {
        return ofPriceIndexer(null);
    }

    private PriceIndexer ofPriceIndexer(InstrumentPrice price) {
        final PriceIndexer indexer = new PriceIndexer(DATA_RETENTION_DAYS);

        if (price != null) {
            final PriceSet priceSet = ofPriceSet(price);

            final Map<String, PriceSet> priceByVendor     = getPriceMap(indexer, "priceByProvider");
            final Map<String, PriceSet> priceByInstrument = getPriceMap(indexer, "priceByInstrument");

            priceByVendor.put(price.providerId(), priceSet);
            priceByInstrument.put(price.isin(), priceSet);
        }
        return indexer;
    }

    public static PriceSet ofPriceSet(InstrumentPrice price) {
        final PriceSet index = new PriceSet();
        index.add(price);
        return index;
    }

    private Map<String, PriceSet> getPriceMap(PriceIndexer indexer, String mapName) {
        return (Map<String, PriceSet>) ReflectionTestUtils.getField(indexer, mapName);
    }
}