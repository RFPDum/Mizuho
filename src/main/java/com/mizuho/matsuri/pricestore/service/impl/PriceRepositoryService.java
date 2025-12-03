package com.mizuho.matsuri.pricestore.service.impl;

import com.mizuho.matsuri.pricestore.data.IPricePersistenceService;
import com.mizuho.matsuri.pricestore.model.InstrumentPrice;
import com.mizuho.matsuri.pricestore.service.IDataCache;
import com.mizuho.matsuri.pricestore.service.IPriceRepositoryService;
import com.mizuho.matsuri.pricestore.service.PriceRepositoryValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static com.mizuho.matsuri.pricestore.service.impl.InstrumentPriceCache.IndexType.ISIN;
import static com.mizuho.matsuri.pricestore.service.impl.InstrumentPriceCache.IndexType.VENDOR;

@Service
@AllArgsConstructor
public class PriceRepositoryService implements IPriceRepositoryService<InstrumentPrice> {
    private final IDataCache<InstrumentPrice, InstrumentPriceCache.IndexType> priceCache;
    private final IPricePersistenceService pricePersistenceService;

    @Override
    public void acceptPriceData(InstrumentPrice instrumentPrice) throws PriceRepositoryValidationException {
        validateInstrumentPrice(instrumentPrice);
        priceCache.indexData(instrumentPrice);
        pricePersistenceService.storeInstrumentPrice(instrumentPrice);
    }

    private void validateInstrumentPrice(InstrumentPrice instrumentPrice) throws PriceRepositoryValidationException {
        validateStringFieldNotEmpty(instrumentPrice.isin(), "ISIN");
        validateStringFieldNotEmpty(instrumentPrice.currency(), "Currency");
        validateStringFieldNotEmpty(instrumentPrice.vendorId(), "VendorId");
        if (instrumentPrice.priceDate() == null) {
            throw new PriceRepositoryValidationException("PriceDate should not be null");
        }
    }

    private void validateStringFieldNotEmpty(String value, String fieldName) throws PriceRepositoryValidationException {
        if (value == null || value.isBlank()) {
            throw new PriceRepositoryValidationException(fieldName + " should not be empty");
        }
    }

    @Override
    public Collection<InstrumentPrice> retrievePricesForIsin(String isin) {
        return priceCache.getData(ISIN, isin);
    }

    @Override
    public Collection<InstrumentPrice> retrieveVendorPrices(String vendorId) {
        return priceCache.getData(VENDOR, vendorId);
    }

    @Override
    public void rebuildPriceCache() {
        final int retentionPeriod                = priceCache.getRetentionPeriod();
        final Collection<InstrumentPrice> prices = pricePersistenceService.retrieveInstrumentPrices(retentionPeriod);

        priceCache.rebuildIndex(prices);
    }

    @Override
    public void purgeCache() {
        priceCache.purge();
    }
}
