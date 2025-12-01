package com.mizuho.matsuri.pricestore.service.impl;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;
import com.mizuho.matsuri.pricestore.service.IDataCache;
import com.mizuho.matsuri.pricestore.data.IPricePersistenceService;
import com.mizuho.matsuri.pricestore.service.IPriceRepositoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static com.mizuho.matsuri.pricestore.service.impl.InstrumentDataCache.IndexType.ISIN;
import static com.mizuho.matsuri.pricestore.service.impl.InstrumentDataCache.IndexType.VENDOR;

@Service
@AllArgsConstructor
public class PriceRepositoryService implements IPriceRepositoryService<InstrumentPrice> {
    private final IDataCache<InstrumentPrice> priceCache;
    private final IPricePersistenceService pricePersistenceService;

    @Override
    public void acceptPriceData(InstrumentPrice instrumentPrice) {
        priceCache.indexData(instrumentPrice);
        pricePersistenceService.storeInstrumentPrice(instrumentPrice);
    }

    @Override
    public Collection<InstrumentPrice> retrievePricesForIsin(String isin) {
        return priceCache.getData(ISIN, isin);
    }

    @Override
    public Collection<InstrumentPrice> retrieveVendorPrices(String vendorId) {
        return priceCache.getData(VENDOR, vendorId);
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
