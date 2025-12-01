package com.mizuho.matsuri.service.impl;

import com.mizuho.matsuri.pricestore.data.IPricePersistenceService;
import com.mizuho.matsuri.pricestore.model.InstrumentPrice;
import com.mizuho.matsuri.pricestore.service.IDataCache;
import com.mizuho.matsuri.pricestore.service.impl.PriceRepositoryService;
import org.junit.jupiter.api.Test;
import org.mockito.verification.VerificationMode;

import java.util.Collection;
import java.util.List;

import static com.mizuho.matsuri.pricestore.service.impl.InstrumentDataCache.IndexType.ISIN;
import static com.mizuho.matsuri.pricestore.service.impl.InstrumentDataCache.IndexType.VENDOR;
import static com.mizuho.matsuri.testutils.InstrumentPriceUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class PriceRepositoryServiceTest {
    private final IDataCache priceCache              = mock(IDataCache.class);
    private final IPricePersistenceService pricePersistenceService = mock(IPricePersistenceService.class);

    private static final int RETENTION_PERIOD = 30;

    @Test
    public void should_store_and_index_price_data_when_acceptPriceData_is_called() {

        // Given
        final PriceRepositoryService service = ofPriceRepoService();
        final InstrumentPrice price1 = ofInstrumentPrice(ISIN_1, VENDOR_1, DAY);
        final InstrumentPrice price2 = ofInstrumentPrice(ISIN_1, VENDOR_2, DAY);

        // When
        service.acceptPriceData(price1);
        service.acceptPriceData(price2);

        // Then
        verify(priceCache, twice()).indexData(any(InstrumentPrice.class));
        verify(priceCache).indexData(price1);
        verify(priceCache).indexData(price2);

        verify(pricePersistenceService, twice()).storeInstrumentPrice(any(InstrumentPrice.class));
        verify(pricePersistenceService).storeInstrumentPrice(price1);
        verify(pricePersistenceService).storeInstrumentPrice(price2);
    }

    @Test
    public void should_rebuild_cache_when_rebuildPriceCache_is_called() {
        // Given
        final PriceRepositoryService service = ofPriceRepoService();
        final InstrumentPrice price1 = ofInstrumentPrice(ISIN_2, VENDOR_1, DAY);
        final InstrumentPrice price2 = ofInstrumentPrice(ISIN_3, VENDOR_2, DAY);
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
    public void should_retrieve_prices_from_cache_when_retrieveVendorPrices_is_called() {
        // Given
        final PriceRepositoryService service = ofPriceRepoService();

        // When
        service.retrieveVendorPrices(VENDOR_1);

        // Then
        verify(priceCache).getData(VENDOR, VENDOR_1);
    }

    @Test
    public void should_retrieve_prices_from_cache_when_retrievePricesForIsin_is_called() {
        // Given
        final PriceRepositoryService service = ofPriceRepoService();

        // When
        final Collection<InstrumentPrice> prices = service.retrievePricesForIsin(ISIN_1);

        // Then
        verify(priceCache).getData(ISIN, ISIN_1);
        assertThat(prices).isNotNull();
    }

    private static VerificationMode twice() {
        return times(2);
    }

    private PriceRepositoryService ofPriceRepoService() {
        doReturn(RETENTION_PERIOD).when(priceCache).getRetentionPeriod();
        return new PriceRepositoryService(priceCache, pricePersistenceService);
    }
}
