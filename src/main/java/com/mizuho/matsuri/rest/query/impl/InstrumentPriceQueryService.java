package com.mizuho.matsuri.rest.query.impl;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;
import com.mizuho.matsuri.pricestore.service.IPriceRepositoryService;
import com.mizuho.matsuri.rest.feed.model.InstrumentPriceInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;

@Service
@AllArgsConstructor
public class InstrumentPriceQueryService {
    private final IPriceRepositoryService priceRepositoryService;

    public InstrumentPriceInfo[] getInstrumentPrices(String isin) {
        return priceRepositoryService.retrieveVendorPrices(isin)
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

    public Response purgeOldPrices() {
        priceRepositoryService.purgeCache();
        return Response.ok().build();
    }

    private InstrumentPriceInfo toInstrumentPriceInfo(InstrumentPrice instrumentPrice) {
        return new InstrumentPriceInfo(
            instrumentPrice.isin(),
            instrumentPrice.currency(),
            instrumentPrice.price(),
            instrumentPrice.priceDate(),
            instrumentPrice.providerId());
    }

}
