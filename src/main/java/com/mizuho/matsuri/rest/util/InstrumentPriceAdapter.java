package com.mizuho.matsuri.rest.util;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;
import com.mizuho.matsuri.rest.feed.exception.PriceValidationException;
import com.mizuho.matsuri.rest.feed.model.InstrumentPriceInfo;
import com.mizuho.matsuri.rest.query.model.InstrumentPriceUpdateRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;

@Component
public class InstrumentPriceAdapter {

    public InstrumentPrice toInstrumentPrice(InstrumentPriceUpdateRequest priceUpdateRequest) throws PriceValidationException {
        return new InstrumentPrice(
                getMandatoryString(priceUpdateRequest.getIsin(), "ISIN"),
                getMandatoryString(priceUpdateRequest.getCurrency(), "Currency"),
                getPrice(priceUpdateRequest.getPrice()),
                parsePriceDate(priceUpdateRequest.getPriceDate()),
                getMandatoryString(priceUpdateRequest.getVendorId(), "VendorId")
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

    private static double getPrice(Double price) throws PriceValidationException {
        if (price == null) {
            throw new PriceValidationException("Missing mandatory attribute price");
        }
        return price;
    }

    private static String getMandatoryString(String attribute, String attributeName) throws PriceValidationException {
        if (attribute == null || attribute.isBlank()) {
            throw new PriceValidationException("Missing mandatory attribute " + attributeName);
        }
        return attribute;
    }

    private LocalDateTime parsePriceDate(String priceDate) throws PriceValidationException {
        try {
            return LocalDateTime.parse(priceDate);
        } catch (DateTimeParseException e) {
            throw new PriceValidationException("'" + priceDate + "' is not a valid date. Check format", e);
        }
    }
}
