package com.mizuho.matsuri.pricestore.service;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;
import com.mizuho.matsuri.pricestore.service.impl.InstrumentPriceCache.IndexType;

import java.util.Collection;

public interface IPriceIndexer {
    void indexPrice(InstrumentPrice price);
    void rebuildIndex(Collection<InstrumentPrice> prices);

    Collection<InstrumentPrice> getInstrumentPrices(IndexType indexType, String indexKey);

    void purge();

    int getRetentionPeriod();
}
