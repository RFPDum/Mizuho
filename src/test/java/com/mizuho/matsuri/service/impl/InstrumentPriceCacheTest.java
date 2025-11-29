package com.mizuho.matsuri.service.impl;

import com.mizuho.matsuri.model.InstrumentPrice;
import com.mizuho.matsuri.service.impl.InstrumentPriceCache.PriceSet;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mizuho.matsuri.testutils.InstrumentPriceUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class InstrumentPriceCacheTest {

    private static final int DATA_RETENTION_DAYS = 10;

    @Test
    public void should_return_set_retention_period_when_getRetentionPeriod_is_called() {
        // Given
        final int retentionPeriodInDays = 30;
        final InstrumentPriceCache indexer = new InstrumentPriceCache(retentionPeriodInDays);

        // When
        final int returnedRetentionPeriod = indexer.getRetentionPeriod();

        // Then
        assertThat(returnedRetentionPeriod).isEqualTo(retentionPeriodInDays);
    }

    @Test
    public void should_return_an_empty_collection_when_attempting_to_get_prices_from_a_provider_when_none_are_present() {
        // Given
        final InstrumentPriceCache indexer = ofInstrumentPriceCache();
        final String aProviderId   = PRICE_PROVIDER_1;

        // When
        final Collection<InstrumentPrice> providerPrices = indexer.getProviderPrices(aProviderId);

        // Then
        assertThat(providerPrices).isEmpty();
    }

    @Test
    public void should_return_an_empty_collection_when_attempting_to_get_prices_for_an_instrument_when_none_are_present() {
        // Given
        final InstrumentPriceCache indexer = ofInstrumentPriceCache();
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
        final InstrumentPriceCache indexer = ofInstrumentPriceCache(price);

        // When
        final Collection<InstrumentPrice> prices = indexer.getProviderPrices(PRICE_PROVIDER_1);

        // Then
        assertThat(prices).usingFieldByFieldElementComparator().containsOnly(price);
    }

    @Test
    public void should_return_the_instrument_price_for_a_given_isin_when_there_is_only_one_price_for_that_instrument() {
        // Given
        final InstrumentPrice price   = ofInstrumentPrice(ISIN_1, PRICE_PROVIDER_1);
        final InstrumentPriceCache indexer = ofInstrumentPriceCache(price);

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

        final InstrumentPriceCache indexer = ofInstrumentPriceCache();

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

        final InstrumentPriceCache indexer = ofInstrumentPriceCache();

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
        final InstrumentPriceCache indexer = ofInstrumentPriceCache();
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
        final InstrumentPriceCache indexer = ofInstrumentPriceCache();
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
        final InstrumentPriceCache indexer = ofInstrumentPriceCache();
        final List<InstrumentPrice> prices = ofInstrumentPricesForIsin(ISIN_3);
        indexer.rebuildIndex(prices);

        // When
        final Collection<InstrumentPrice> providerPrices = indexer.getInstrumentPrices("NotPresent");

        // Then
        assertThat(providerPrices).isEmpty();
    }

    @Test
    public void should_purge_all_prices_older_than_30_days_when_purge_is_called_check_by_provider() {
        // Given
        final InstrumentPriceCache indexer = ofInstrumentPriceCache();
        final InstrumentPrice price1 = ofProvider2InstrumentPrice(ISIN_1, DAY);
        final InstrumentPrice price2 = ofProvider2InstrumentPrice(ISIN_2, DAY_MIN_1);
        final InstrumentPrice price3 = ofProvider2InstrumentPrice(ISIN_1, DAY_MIN_5);
        final InstrumentPrice price4 = ofProvider2InstrumentPrice(ISIN_1, DAY_MIN_10);
        final InstrumentPrice price5 = ofProvider2InstrumentPrice(ISIN_1, DAY_MIN_11);
        final List<InstrumentPrice> prices = List.of(
                price1,
                price2,
                price3,
                price4,
                price5);
        indexer.rebuildIndex(prices);
        assertThat(indexer.getProviderPrices(PRICE_PROVIDER_2)).containsExactlyInAnyOrderElementsOf(prices);

        final List<InstrumentPrice> expectedRetained = List.of(price1, price2, price3);

        // When
        indexer.purge();

        // Then
        assertThat(indexer.getProviderPrices(PRICE_PROVIDER_2)).containsExactlyInAnyOrderElementsOf(expectedRetained);
    }


    @Test
    public void should_purge_all_prices_older_than_30_days_when_purge_is_called_check_by_isin() {
        // Given
        final InstrumentPriceCache indexer = ofInstrumentPriceCache();
        final InstrumentPrice price1 = ofIsin1InstrumentPrice(PRICE_PROVIDER_1, DAY);
        final InstrumentPrice price2 = ofIsin1InstrumentPrice(PRICE_PROVIDER_2, DAY_MIN_1);
        final InstrumentPrice price3 = ofIsin1InstrumentPrice(PRICE_PROVIDER_1, DAY_MIN_5);
        final InstrumentPrice price4 = ofIsin1InstrumentPrice(PRICE_PROVIDER_2, DAY_MIN_10);
        final InstrumentPrice price5 = ofIsin1InstrumentPrice(PRICE_PROVIDER_1, DAY_MIN_11);
        final List<InstrumentPrice> prices = List.of(
                price1,
                price2,
                price3,
                price4,
                price5);
        indexer.rebuildIndex(prices);
        assertThat(indexer.getInstrumentPrices(ISIN_1)).containsExactlyInAnyOrderElementsOf(prices);

        final List<InstrumentPrice> expectedRetained = List.of(price1, price2, price3);

        // When
        indexer.purge();

        // Then
        assertThat(indexer.getInstrumentPrices(ISIN_1)).containsExactlyInAnyOrderElementsOf(expectedRetained);
    }

    private InstrumentPrice ofProvider2InstrumentPrice(String isin, LocalDateTime priceTime) {
        return ofInstrumentPrice(isin, PRICE_PROVIDER_2, priceTime);
    }

    private InstrumentPrice ofIsin1InstrumentPrice(String priceProviderId, LocalDateTime priceTime) {
        return ofInstrumentPrice(ISIN_1, priceProviderId, priceTime);
    }
    private InstrumentPriceCache ofInstrumentPriceCache() {
        return ofInstrumentPriceCache(null);
    }

    private InstrumentPriceCache ofInstrumentPriceCache(InstrumentPrice price) {
        final InstrumentPriceCache indexer = new InstrumentPriceCache(DATA_RETENTION_DAYS);

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

    private Map<String, PriceSet> getPriceMap(InstrumentPriceCache indexer, String mapName) {
        return (Map<String, PriceSet>) ReflectionTestUtils.getField(indexer, mapName);
    }
}