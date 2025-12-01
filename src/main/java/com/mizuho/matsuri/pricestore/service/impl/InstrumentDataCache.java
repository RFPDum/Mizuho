package com.mizuho.matsuri.pricestore.service.impl;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;
import com.mizuho.matsuri.pricestore.service.IDataCache;
import com.mizuho.matsuri.pricestore.service.InstrumentPriceCacheProperties;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.mizuho.matsuri.pricestore.utils.Util.getCutOffDate;
import static com.mizuho.matsuri.pricestore.utils.Util.isStale;

@Service
public class InstrumentDataCache implements IDataCache<InstrumentPrice> {
    private final int retentionPeriodInDays;

    private final List<Map<String, PriceSet>> prices;

    /**
     * Constructor creates Instrument Price Cache
     * @param properties cache properties
     */
    public InstrumentDataCache(InstrumentPriceCacheProperties properties) {
        retentionPeriodInDays = properties.getRetentionPeriodInDays();
        prices                = initialiseIndexes();
    }

    private List<Map<String, PriceSet>> initialiseIndexes() {
        final List<Map<String, PriceSet>> prices;
        prices = new ArrayList<>(IndexType.values().length);
        for (IndexType ignored : IndexType.values()) {
            prices.add(new ConcurrentHashMap<>());
        }
        return prices;
    }

    @Override
    public void indexData(InstrumentPrice price) {

        for (IndexType indexType : IndexType.values()) {
            addPrice(price, indexType);
        }
    }

    private void addPrice(InstrumentPrice price, IndexType indexType) {
        getPriceSet(indexType, indexType.keyExtractor.apply(price)).add(price);
    }

    @Override
    public void rebuildIndex(Collection<InstrumentPrice> prices) {
        prices.forEach(this::indexData);
    }

    @Override
    public Collection<InstrumentPrice> getData(IndexType indexType, String indexKey) {
        return getPriceSet(indexType, indexKey).getPrices();
    }

    private PriceSet getPriceSet(IndexType indexType, String key) {
        return getIndex(indexType).computeIfAbsent(key, k -> new PriceSet());
    }

    private Map<String, PriceSet> getIndex(IndexType indexType) {
        return prices.get(indexType.indexNumber);
    }

    @Override
    public void purge() {
        for (IndexType indexType : IndexType.values()) {
            getIndex(indexType).values().forEach(ps -> ps.purge(retentionPeriodInDays));
        }
    }

    @Override
    public int getRetentionPeriod() {
        return retentionPeriodInDays;
    }

    public static class PriceSet {
        private final Set<InstrumentPrice> prices = new HashSet<>();

        public void add(InstrumentPrice price) {
            prices.add(price);
        }

        public Collection<InstrumentPrice> getPrices() {
            return new TreeSet<>(prices);
        }

        public void purge(int ageInDays) {
            final LocalDateTime cutOff = getCutOffDate(ageInDays);
            prices.removeIf(price -> isStale(price, cutOff));
        }
    }

    public enum IndexType {
        ISIN(0, InstrumentPrice::isin),
        VENDOR(1, InstrumentPrice::vendorId);

        private final Function<InstrumentPrice, String> keyExtractor;
        private final int indexNumber;

        IndexType(int indexNumber, Function<InstrumentPrice, String> keyExtractor) {
            this.keyExtractor = keyExtractor;
            this.indexNumber  = indexNumber;
        }
    }
}
