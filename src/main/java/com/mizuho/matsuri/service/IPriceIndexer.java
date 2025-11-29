package com.mizuho.matsuri.service;

import com.mizuho.matsuri.model.InstrumentPrice;

import java.util.Collection;

public interface IPriceIndexer {
    void indexPrice(InstrumentPrice price);
    void rebuildIndex(Collection<InstrumentPrice> prices);

    Collection<InstrumentPrice> getInstrumentPrices(String isin);
    Collection<InstrumentPrice> getProviderPrices(String providerId);

    public void purge();

    boolean isEmpty();
}
