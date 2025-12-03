package com.mizuho.matsuri.pricestore.service;

import java.util.Collection;

/**
 * Service andling the saving and retrieving of instrument prices
 * @param <T> instrument price class
 */
public interface IPriceRepositoryService<T> {
    void acceptPriceData(T instrumentPrice) throws PriceRepositoryValidationException;

    /**
     * Retrieve price whose ISIN is passed as a parameter
     * @param isin ISIN of the prices to retrieve
     * @return prices matching the passed ISIN
     */
    Collection<T> retrievePricesForIsin(String isin);

    /**
     * Retrieve price whose vendorId is passed as a parameter
     * @param vendorId vendorId of the prices to retrieve
     * @return prices matching the passed vendorId
     */
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
