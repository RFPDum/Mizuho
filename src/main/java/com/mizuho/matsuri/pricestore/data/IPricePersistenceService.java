package com.mizuho.matsuri.pricestore.data;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;

import java.util.Collection;

/**
 * Instrument price storage service.
 */
public interface IPricePersistenceService {
    /**
     * Persist instrument price information to store
     * @param instrumentPrice instrument price information
     */
    void storeInstrumentPrice(InstrumentPrice instrumentPrice);

    /**
     * Retrieve all instrument prices from permanent storage with a timestamp more recent
     * than the passed number of days.
     *
     * @param numberOfDays maximum age of the prices we want to retrieve
     * @return price data from permanent storage.
     */
    Collection<InstrumentPrice> retrieveInstrumentPrices(int numberOfDays);
}
