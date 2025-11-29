package com.mizuho.matsuri.service.impl;

import com.mizuho.matsuri.model.InstrumentPrice;
import com.mizuho.matsuri.service.IPriceIndexer;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
public class PriceIndexer implements IPriceIndexer {
    private final int retentionPeriodInDays;

    private final Map<String, PriceSet> priceByProvider   = new ConcurrentHashMap<>();
    private final Map<String, PriceSet> priceByInstrument = new ConcurrentHashMap<>();


    @Override
    public void indexPrice(InstrumentPrice price) {
        getPriceSet(priceByProvider, price.providerId()).add(price);
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
    public Collection<InstrumentPrice> getProviderPrices(String providerId) {
        return getPriceSet(priceByProvider, providerId).getPrices();
    }

    private PriceSet getPriceSet(Map<String, PriceSet> priceMap, String key) {
        return priceMap.computeIfAbsent(key, k -> new PriceSet());
    }

    @Override
    public void purge() {
        priceByProvider.values().forEach(ps -> ps.purge(retentionPeriodInDays));
        priceByInstrument.values().forEach(ps -> ps.purge(retentionPeriodInDays));
    }

    @Override
    public boolean isEmpty() {
        return priceByProvider.isEmpty() && priceByInstrument.isEmpty();
    }

    public static class PriceSet {
        private final Set<InstrumentPrice> prices = new HashSet<>();

        public void add(InstrumentPrice price) {
            prices.add(price);
        }

        public Collection<InstrumentPrice> getPrices() {
            return Set.copyOf(prices);
        }

        public void purge(int ageInDays) {
            final LocalDateTime cutOff = LocalDateTime.now().minusDays(ageInDays);
            prices.removeIf(price -> isTooOld(price, cutOff));
        }

        private static boolean isTooOld(InstrumentPrice price, LocalDateTime cutOff) {
            return price.priceDate().isBefore(cutOff);
        }
    }
}
