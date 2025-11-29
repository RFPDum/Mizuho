package com.mizuho.matsuri.service.impl;

import com.mizuho.matsuri.model.InstrumentPrice;
import com.mizuho.matsuri.service.IPriceIndexer;
import com.mizuho.matsuri.data.IPricePersistenceService;
import com.mizuho.matsuri.service.IPriceRepositoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

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
        return priceCache.getInstrumentPrices(isin);
    }

    @Override
    public Collection<InstrumentPrice>  retrieveProviderPrices(String providerId) {
        return priceCache.getProviderPrices(providerId);
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
