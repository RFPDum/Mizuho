package com.mizuho.matsuri.rest.feed.impl;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;
import com.mizuho.matsuri.pricestore.service.IPriceRepositoryService;
import com.mizuho.matsuri.rest.query.model.InstrumentPriceUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Service
@AllArgsConstructor
public class InstrumentPriceFeedService {
    private final IPriceRepositoryService priceRepositoryService;

    public Response addPriceUpdate(InstrumentPriceUpdateRequest priceUpdateRequest) {
        try {
            final InstrumentPrice price = toInstrumentPrice(priceUpdateRequest);
            priceRepositoryService.acceptPriceData(price);
            return Response.ok()
                           .build();
        } catch (DateTimeParseException e) {
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
    }

    private InstrumentPrice toInstrumentPrice(InstrumentPriceUpdateRequest priceUpdateRequest) {
        return new InstrumentPrice(
                priceUpdateRequest.getIsin(),
                priceUpdateRequest.getCurrency(),
                priceUpdateRequest.getPrice(),
                parsePriceDate(priceUpdateRequest.getPriceDate()),
                priceUpdateRequest.getProviderId()
        );
    }

    private LocalDateTime parsePriceDate(String priceDate) {
        return LocalDateTime.parse(priceDate);
    }
}
