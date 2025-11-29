package com.mizuho.matsuri.rest.query.controler;

import com.mizuho.matsuri.rest.query.impl.InstrumentPriceQueryService;
import com.mizuho.matsuri.rest.feed.model.InstrumentPriceInfo;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;

@RestController
@AllArgsConstructor
@RequestMapping(value = "matsuri/instruments/prices")
public class InstrumentPriceQueryRestController {
    private InstrumentPriceQueryService queryService;

    @GetMapping(value="/getbyisin/{isin}")
    public InstrumentPriceInfo[] getInstrumentPrices(@PathVariable String isin) {
        return queryService.getInstrumentPrices(isin);
    }

    @GetMapping(value="/getbyvendor/{vendorId}")
    public InstrumentPriceInfo[] getVendorInstrumentPrices(@PathVariable String vendorId) {
        return queryService.getVendorInstrumentPrices(vendorId);
    }

    @GetMapping(value ="/purgeoldprices")
    public Response purgeOldPrices() {
        return queryService.purgeOldPrices();
    }
}

