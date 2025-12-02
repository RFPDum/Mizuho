package com.mizuho.matsuri.rest.query.impl;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;
import com.mizuho.matsuri.pricestore.service.IPriceRepositoryService;
import com.mizuho.matsuri.rest.feed.model.InstrumentPriceInfo;
import com.mizuho.matsuri.rest.util.InstrumentPriceAdapter;
import com.mizuho.matsuri.rest.util.ResponseUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InstrumentPriceQueryService {
    private final IPriceRepositoryService<InstrumentPrice> priceRepositoryService;
    private final InstrumentPriceAdapter                   priceAdapter;

    public InstrumentPriceInfo[] getInstrumentPrices(String isin) {
        return priceAdapter.toInstrumentPriceInfos(priceRepositoryService.retrievePricesForIsin(isin));
    }

    public InstrumentPriceInfo[] getVendorInstrumentPrices(String vendorId) {
        return priceAdapter.toInstrumentPriceInfos(priceRepositoryService.retrieveVendorPrices(vendorId));
    }

    public ResponseEntity<String> purgeOldPrices() {
        priceRepositoryService.purgeCache();
        return ResponseUtils.ofOKResponse();
    }
}
