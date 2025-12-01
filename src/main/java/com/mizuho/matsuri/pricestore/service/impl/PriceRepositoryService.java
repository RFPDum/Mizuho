package com.mizuho.matsuri.pricestore.service.impl;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;
import com.mizuho.matsuri.pricestore.service.IPriceIndexer;
import com.mizuho.matsuri.pricestore.data.IPricePersistenceService;
import com.mizuho.matsuri.pricestore.service.IPriceRepositoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static com.mizuho.matsuri.pricestore.service.impl.InstrumentPriceCache.IndexType.ISIN;
import static com.mizuho.matsuri.pricestore.service.impl.InstrumentPriceCache.IndexType.VENDOR;

@Service
@AllArgsConstructor
public class PriceRepositoryService implements IPriceRepositoryService {
    private final IPriceIndexer            priceCache;
    private final IPricePersistenceService pricePersistenceService;

    @Override
    public void acceptPriceData(InstrumentPrice instrumentPrice) {
        priceCache.indexPrice(instrumentPrice);
        pricePersistenceService.storeInstrumentPrice(instrumentPrice);
    }

    @Override
    public Collection<InstrumentPrice> retrievePricesForIsin(String isin) {
        return priceCache.getInstrumentPrices(ISIN, isin);
    }

    @Override
    public Collection<InstrumentPrice> retrieveVendorPrices(String vendorId) {
        return priceCache.getInstrumentPrices(VENDOR, vendorId);
    }

    @Override
    public void rebuildPriceCache() {
        final int retentionPeriod                = priceCache.getRetentionPeriod();
        final Collection<InstrumentPrice> prices = pricePersistenceService.retrieveInstrumentPrices(retentionPeriod);

        priceCache.rebuildIndex(prices);
    }

    @Override
    public void purgeCache() {
        priceCache.purge();
    }
}
