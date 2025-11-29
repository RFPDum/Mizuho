package com.mizuho.matsuri.service;

import com.mizuho.matsuri.model.InstrumentPrice;

import java.util.Collection;

public interface IPriceRepositoryService {
    void acceptPriceData(InstrumentPrice instrumentPrice);

    Collection<InstrumentPrice> retrievePricesForIsin(String isin);

    Collection<InstrumentPrice>  retrieveProviderPrices(String providerId);

    /**
     * Rebuild price cache from persistent data storage
     */
    void rebuildPriceCache();

    void purgeCache();
}
