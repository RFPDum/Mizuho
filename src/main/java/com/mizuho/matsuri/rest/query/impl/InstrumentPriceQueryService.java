package com.mizuho.matsuri.rest.query.impl;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;
import com.mizuho.matsuri.pricestore.service.IPriceRepositoryService;
import com.mizuho.matsuri.rest.feed.model.InstrumentPriceInfo;
import com.mizuho.matsuri.rest.util.ResponseUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InstrumentPriceQueryService {
    private final IPriceRepositoryService<InstrumentPrice> priceRepositoryService;

    public InstrumentPriceInfo[] getInstrumentPrices(String isin) {
        return priceRepositoryService.retrievePricesForIsin(isin)
                                     .stream()
                                     .map(this::toInstrumentPriceInfo)
                                     .toArray(InstrumentPriceInfo[]::new);
    }

    public InstrumentPriceInfo[] getVendorInstrumentPrices(String vendorId) {
        return priceRepositoryService.retrieveVendorPrices(vendorId)
                                     .stream()
                                     .map(this::toInstrumentPriceInfo)
                                     .toArray(InstrumentPriceInfo[]::new);
    }

    public ResponseEntity<String> purgeOldPrices() {
        priceRepositoryService.purgeCache();
        return ResponseUtils.ofOKResponse();
    }

    private InstrumentPriceInfo toInstrumentPriceInfo(InstrumentPrice instrumentPrice) {
        return new InstrumentPriceInfo(instrumentPrice.vendorId(),
                                       instrumentPrice.isin(),
                                       instrumentPrice.currency(),
                                       instrumentPrice.price(),
                                       instrumentPrice.priceDate());
    }

}
