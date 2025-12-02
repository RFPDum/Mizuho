package com.mizuho.matsuri.rest.feed.impl;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;
import com.mizuho.matsuri.pricestore.service.IPriceRepositoryService;
import com.mizuho.matsuri.rest.feed.exception.PriceValidationException;
import com.mizuho.matsuri.rest.query.model.InstrumentPriceUpdateRequest;
import com.mizuho.matsuri.rest.util.InstrumentPriceAdapter;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.mizuho.matsuri.rest.util.ResponseUtils.ofFailedResponse;
import static com.mizuho.matsuri.rest.util.ResponseUtils.ofOKResponse;

@Service
@AllArgsConstructor
public class InstrumentPriceFeedService {
    private final IPriceRepositoryService<InstrumentPrice> priceRepositoryService;
    private final InstrumentPriceAdapter priceAdapter;

    public ResponseEntity<String> addPriceUpdate(InstrumentPriceUpdateRequest priceUpdateRequest) {
        try {
            final InstrumentPrice price = priceAdapter.toInstrumentPrice(priceUpdateRequest);
            priceRepositoryService.acceptPriceData(price);
            return ofOKResponse();
        } catch (PriceValidationException e) {
            return ofFailedResponse(e.getMessage());
        }
    }
}
