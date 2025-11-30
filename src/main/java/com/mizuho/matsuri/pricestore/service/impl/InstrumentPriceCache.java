package com.mizuho.matsuri.pricestore.service.impl;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;
import com.mizuho.matsuri.pricestore.service.IPriceIndexer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.mizuho.matsuri.pricestore.utils.Util.getCutOffDate;
import static com.mizuho.matsuri.pricestore.utils.Util.isStale;

@Service
@AllArgsConstructor
public class InstrumentPriceCache implements IPriceIndexer {
    private final int retentionPeriodInDays;

    private final Map<String, PriceSet> priceByVendor     = new ConcurrentHashMap<>();
    private final Map<String, PriceSet> priceByInstrument = new ConcurrentHashMap<>();


    @Override
    public void indexPrice(InstrumentPrice price) {
        getPriceSet(priceByVendor, price.vendorId()).add(price);
        getPriceSet(priceByInstrument, price.isin()).add(price);
    }

    @Override
    public void rebuildIndex(Collection<InstrumentPrice> prices) {
        prices.forEach(this::indexPrice);
    }

    @Override
    public Collection<InstrumentPrice> getInstrumentPrices(String isin) {
        return getPriceSet(priceByInstrument, isin).getPrices();
    }

    @Override
    public Collection<InstrumentPrice> getVendorPrices(String vendorId) {
        return getPriceSet(priceByVendor, vendorId).getPrices();
    }

    private PriceSet getPriceSet(Map<String, PriceSet> priceMap, String key) {
        return priceMap.computeIfAbsent(key, k -> new PriceSet());
    }

    @Override
    public void purge() {
        priceByVendor.values().forEach(ps -> ps.purge(retentionPeriodInDays));
        priceByInstrument.values().forEach(ps -> ps.purge(retentionPeriodInDays));
    }

    @Override
    public int getRetentionPeriod() {
        return retentionPeriodInDays;
    }

    @Override
    public boolean isEmpty() {
        return priceByVendor.isEmpty() && priceByInstrument.isEmpty();
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
}
