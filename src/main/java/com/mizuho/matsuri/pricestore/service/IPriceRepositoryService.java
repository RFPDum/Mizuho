package com.mizuho.matsuri.pricestore.service;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;

import java.util.Collection;

public interface IPriceRepositoryService<T> {
    void acceptPriceData(T instrumentPrice);

    Collection<T> retrievePricesForIsin(String isin);

    Collection<T> retrieveVendorPrices(String vendorId);

    /**
     * Rebuild price cache from persistent data storage
     */
    void rebuildPriceCache();

    /**
     * Remove obsolete items from the cache underlying the service
     */
    void purgeCache();
}
