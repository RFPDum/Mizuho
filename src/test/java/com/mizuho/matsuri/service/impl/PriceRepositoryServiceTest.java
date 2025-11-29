package com.mizuho.matsuri.service.impl;

import com.mizuho.matsuri.data.IPricePersistenceService;
import com.mizuho.matsuri.model.InstrumentPrice;
import com.mizuho.matsuri.service.IPriceIndexer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;

import java.util.List;

import static com.mizuho.matsuri.testutils.InstrumentPriceUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class PriceRepositoryServiceTest {
    @Mock
    private IPriceIndexer priceCache;
    @Mock
    private IPricePersistenceService pricePersistenceService;

    private static final int RETENTION_PERIOD = 30;

    @Test
    public void should_store_and_index_price_data_when_acceptPriceData_is_called() {

        // Given
        final PriceRepositoryService service = ofPriceRepoService();
        final InstrumentPrice price1 = ofInstrumentPrice(ISIN_1, PRICE_PROVIDER_1, DAY);
        final InstrumentPrice price2 = ofInstrumentPrice(ISIN_1, PRICE_PROVIDER_2, DAY);

        // When
        service.acceptPriceData(price1);
        service.acceptPriceData(price2);

        // Then
        verify(priceCache, twice()).indexPrice(any(InstrumentPrice.class));
        verify(priceCache).indexPrice(price1);
        verify(priceCache).indexPrice(price2);

        verify(pricePersistenceService, twice()).storeInstrumentPrice(any(InstrumentPrice.class));
        verify(pricePersistenceService).storeInstrumentPrice(price1);
        verify(pricePersistenceService).storeInstrumentPrice(price2);
    }

    @Test
    public void should_rebuild_cache_when_rebuildPriceCache_is_called() {
        // Given
        final PriceRepositoryService service = ofPriceRepoService();
        final InstrumentPrice price1 = ofInstrumentPrice(ISIN_2, PRICE_PROVIDER_1, DAY);
        final InstrumentPrice price2 = ofInstrumentPrice(ISIN_3, PRICE_PROVIDER_2, DAY);
        final List<InstrumentPrice> priceList = List.of(price1, price2);
        doReturn(priceList).when(pricePersistenceService).retrieveInstrumentPrices(RETENTION_PERIOD);

        // When
        service.rebuildPriceCache();

        // Then
        verify(pricePersistenceService).retrieveInstrumentPrices(RETENTION_PERIOD);
        priceCache.rebuildIndex(eq(priceList));
    }

    @Test
    public void should_purge_cache_when_purgeCache_is_called() {
        // Given
        final PriceRepositoryService service = ofPriceRepoService();

        // When
        service.purgeCache();

        // Then
        verify(priceCache).purge();
    }

    @Test
    public void should_retrieve_prices_from_cache_when_retrieveProviderPrices_is_called() {
        // Given
        final PriceRepositoryService service = ofPriceRepoService();

        // When
        service.retrieveProviderPrices(PRICE_PROVIDER_1);

        // Then
        verify(priceCache).getProviderPrices(PRICE_PROVIDER_1);
    }

    @Test
    public void should_retrieve_prices_from_cache_when_retrievePricesForIsin_is_called() {
        // Given
        final PriceRepositoryService service = ofPriceRepoService();

        // When
        service.retrievePricesForIsin(ISIN_1);

        // Then
        verify(priceCache).getInstrumentPrices(ISIN_1);
    }


    private static VerificationMode twice() {
        return times(2);
    }

    private PriceRepositoryService ofPriceRepoService() {
        doReturn(RETENTION_PERIOD).when(priceCache).getRetentionPeriod();
        return new PriceRepositoryService(priceCache, pricePersistenceService);
    }
}
