package com.mizuho.matsuri.pricestore.data.impl;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;
import com.mizuho.matsuri.pricestore.data.IPricePersistenceService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static com.mizuho.matsuri.pricestore.utils.Util.getCutOffDate;
import static com.mizuho.matsuri.pricestore.utils.Util.isStale;
import static java.util.stream.Collectors.toList;

/**
 * Dummy persistence service implementation for illustration purposes.
 *
 * A real implementation could make use of a RDBMS or a DataLake with data being
 * stored in Parquet/ORC... files according to the needs.
 */
public class PricePersistenceService implements IPricePersistenceService {
    private final Collection<InstrumentPrice> priceStore = new ArrayList<>();

    @Override
    public void storeInstrumentPrice(InstrumentPrice instrumentPrice) {
        priceStore.add(instrumentPrice);
    }

    @Override
    public Collection<InstrumentPrice> retrieveInstrumentPrices(int maxAgeInDays) {
        final LocalDateTime cutOff = getCutOffDate(maxAgeInDays);

        return priceStore.stream()
                .filter(price -> !isStale(price, cutOff))
                .collect(toList());
    }
}
