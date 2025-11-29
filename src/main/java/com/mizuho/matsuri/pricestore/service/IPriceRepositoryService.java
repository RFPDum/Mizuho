package com.mizuho.matsuri.pricestore.service;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;

import java.util.Collection;

public interface IPriceRepositoryService {
    void acceptPriceData(InstrumentPrice instrumentPrice);

    Collection<InstrumentPrice> retrievePricesForIsin(String isin);

    Collection<InstrumentPrice> retrieveVendorPrices(String providerId);

    /**
     * Rebuild price cache from persistent data storage
     */
    void rebuildPriceCache();

    void purgeCache();
}
