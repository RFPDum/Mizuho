package com.mizuho.matsuri.rest.feed.controler;

import com.mizuho.matsuri.rest.feed.impl.InstrumentPriceFeedService;
import com.mizuho.matsuri.rest.query.model.InstrumentPriceUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(value = "matsuri/instruments/prices")
public class InstrumentPriceFeedRestController {
    private final InstrumentPriceFeedService instrumentPriceFeedService;

    @PostMapping(value = "add")
    public ResponseEntity<String> addInstrumentPrice(@RequestBody InstrumentPriceUpdateRequest priceUpdateRequest) {
        return instrumentPriceFeedService.addPriceUpdate(priceUpdateRequest);
    }
}

