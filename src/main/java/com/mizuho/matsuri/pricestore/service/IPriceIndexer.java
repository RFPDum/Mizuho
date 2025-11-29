package com.mizuho.matsuri.pricestore.service;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;

import java.util.Collection;

public interface IPriceIndexer {
    void indexPrice(InstrumentPrice price);
    void rebuildIndex(Collection<InstrumentPrice> prices);

    Collection<InstrumentPrice> getInstrumentPrices(String isin);
    Collection<InstrumentPrice> getVendorPrices(String vendorId);

    void purge();

    int getRetentionPeriod();

    boolean isEmpty();
}
