package com.mizuho.matsuri.rest.util;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;
import com.mizuho.matsuri.rest.feed.exception.PriceValidationException;
import com.mizuho.matsuri.rest.feed.model.InstrumentPriceInfo;
import com.mizuho.matsuri.rest.query.impl.InstrumentPriceQueryService;
import com.mizuho.matsuri.rest.query.model.InstrumentPriceUpdateRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;

@Component
public class InstrumentPriceAdapter {

    public InstrumentPrice toInstrumentPrice(InstrumentPriceUpdateRequest priceUpdateRequest) throws PriceValidationException {
        return new InstrumentPrice(
                priceUpdateRequest.getIsin(),
                priceUpdateRequest.getCurrency(),
                priceUpdateRequest.getPrice(),
                parsePriceDate(priceUpdateRequest.getPriceDate()),
                priceUpdateRequest.getVendorId()
        );
    }

    public InstrumentPriceInfo[] toInstrumentPriceInfos(Collection<InstrumentPrice> instrumentPrices) {
        return instrumentPrices
                .stream()
                .map(this::toInstrumentPriceInfo)
                .toArray(InstrumentPriceInfo[]::new);
    }

    public InstrumentPriceInfo toInstrumentPriceInfo(InstrumentPrice instrumentPrice) {
        return new InstrumentPriceInfo(instrumentPrice.vendorId(),
                instrumentPrice.isin(),
                instrumentPrice.currency(),
                instrumentPrice.price(),
                instrumentPrice.priceDate());
    }

    private LocalDateTime parsePriceDate(String priceDate) throws PriceValidationException {
        try {
            return LocalDateTime.parse(priceDate);
        } catch (DateTimeParseException e) {
            throw new PriceValidationException("'" + priceDate + "' is not a valid date. Check format", e);
        }
    }
}
