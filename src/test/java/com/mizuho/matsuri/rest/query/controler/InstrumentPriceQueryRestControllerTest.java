package com.mizuho.matsuri.rest.query.controler;


import com.mizuho.matsuri.rest.feed.model.InstrumentPriceInfo;
import com.mizuho.matsuri.rest.query.impl.InstrumentPriceQueryService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.mizuho.matsuri.testutils.InstrumentPriceUtils.*;
import static org.mockito.Mockito.*;

class InstrumentPriceQueryRestControllerTest {
    private final InstrumentPriceQueryService queryService = mock(InstrumentPriceQueryService.class);

    @Test
    void should_call_query_service_getInstrumentPrices_when_getInstrumentPrices_is_called() {
        // Given
        final InstrumentPriceQueryRestController restController = ofInstrumentPriceQueryRestController();

        // When
        restController.getInstrumentPrices(ISIN_1);

        // Then
        verify(queryService).getInstrumentPrices(ISIN_1);
    }

    @Test
    void should_call_query_service_retrievePricesForIsin_when_getVendorInstrumentPrices_is_called() {
        // Given
        final InstrumentPriceQueryRestController restController = ofInstrumentPriceQueryRestController();

        // When
        restController.getVendorInstrumentPrices(VENDOR_1);

        // Then
        verify(queryService).getVendorInstrumentPrices(VENDOR_1);
    }

    @Test
    void should_call_purge() {
        // Given
        final InstrumentPriceQueryRestController restController = ofInstrumentPriceQueryRestController();

        // When
        restController.purgeOldPrices();

        // Then
        verify(queryService).purgeOldPrices();
    }

    private InstrumentPriceQueryRestController ofInstrumentPriceQueryRestController() {
        doReturn(ofInstrumentPriceInfo()).when(queryService).getVendorInstrumentPrices(VENDOR_1);
        doReturn(ofInstrumentPriceInfo()).when(queryService).getInstrumentPrices(ISIN_1);
        return new InstrumentPriceQueryRestController(queryService);
    }

    private InstrumentPriceInfo[] ofInstrumentPriceInfo() {
        return new InstrumentPriceInfo[]{new InstrumentPriceInfo(VENDOR_1, ISIN_1, USD_CCY, 789.44d, LocalDateTime.now())};
    }
}
