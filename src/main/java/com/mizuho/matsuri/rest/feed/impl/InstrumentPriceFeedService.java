package com.mizuho.matsuri.rest.feed.impl;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;
import com.mizuho.matsuri.pricestore.service.IPriceRepositoryService;
import com.mizuho.matsuri.rest.feed.exception.PriceValidationException;
import com.mizuho.matsuri.rest.query.model.InstrumentPriceUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static com.mizuho.matsuri.rest.util.ResponseUtils.ofFailedResponse;
import static com.mizuho.matsuri.rest.util.ResponseUtils.ofOKResponse;

@Service
@AllArgsConstructor
public class InstrumentPriceFeedService {
    private final IPriceRepositoryService<InstrumentPrice> priceRepositoryService;

    public ResponseEntity<String> addPriceUpdate(InstrumentPriceUpdateRequest priceUpdateRequest) {
        try {
            final InstrumentPrice price = toInstrumentPrice(priceUpdateRequest);
            priceRepositoryService.acceptPriceData(price);
            return ofOKResponse();
        } catch (PriceValidationException e) {
            return ofFailedResponse(e.getMessage());
        }
    }

    private InstrumentPrice toInstrumentPrice(InstrumentPriceUpdateRequest priceUpdateRequest) throws PriceValidationException {
        return new InstrumentPrice(
                priceUpdateRequest.getIsin(),
                priceUpdateRequest.getCurrency(),
                priceUpdateRequest.getPrice(),
                parsePriceDate(priceUpdateRequest.getPriceDate()),
                priceUpdateRequest.getVendorId()
        );
    }

    private LocalDateTime parsePriceDate(String priceDate) throws PriceValidationException {
        try {
            return LocalDateTime.parse(priceDate);
        } catch (DateTimeParseException e) {
            throw new PriceValidationException("'" + priceDate + "' is not a valid date. Check format", e);
        }
    }
}
