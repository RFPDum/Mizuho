package com.mizuho.matsuri.rest.query.impl;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;
import com.mizuho.matsuri.pricestore.service.IPriceRepositoryService;
import com.mizuho.matsuri.rest.feed.model.InstrumentPriceInfo;
import com.mizuho.matsuri.rest.util.ResponseUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static com.mizuho.matsuri.testutils.InstrumentPriceUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class InstrumentPriceQueryServiceTest {
    private static final InstrumentPriceInfo[]       INSTRUMENT_PRICE_INFOS = ofInstrumentPriceInfo();
    private static final Collection<InstrumentPrice> INSTRUMENT_PRICES      = ofInstrumentPrices();
    private static final double                      PRICE_AMOUNT           = 789.44d;
    private static final LocalDateTime               DATE_TIME              = LocalDateTime.now();

    private final IPriceRepositoryService priceRepositoryService = mock(IPriceRepositoryService.class);

    @Test
    void should_call_price_repository_service_purgeCache_when_purgeOldPrices_is_called() {
        // Given
        final InstrumentPriceQueryService service     = ofInstrumentPriceQueryService();
        final ResponseEntity<String>      expResponse = ResponseUtils.ofOKResponse();

        // When
        final ResponseEntity<String> response = service.purgeOldPrices();

        // Then
        verify(priceRepositoryService).purgeCache();
        assertThat(response).isEqualTo(expResponse);
    }

    @Test
    void should_call_price_repository_service_retrievePricesForIsin_when_getInstrumentPrices_is_called() {
        // Given
        final InstrumentPriceQueryService service = ofInstrumentPriceQueryService();

        // When
        final InstrumentPriceInfo[] instrumentPrices = service.getInstrumentPrices(ISIN_1);

        // Then
        verify(priceRepositoryService).retrievePricesForIsin(ISIN_1);
        assertThat(instrumentPrices).isEqualTo(INSTRUMENT_PRICE_INFOS);
    }

    @Test
    void should_call_price_repository_service_retrieveVendorPrices_when_getVendorInstrumentPrices_is_called() {
        // Given
        final InstrumentPriceQueryService service = ofInstrumentPriceQueryService();

        // When
        final InstrumentPriceInfo[] instrumentPrices = service.getVendorInstrumentPrices(VENDOR_1);

        // Then
        verify(priceRepositoryService).retrieveVendorPrices(VENDOR_1);
        assertThat(instrumentPrices).isEqualTo(INSTRUMENT_PRICE_INFOS);
    }

    private static InstrumentPriceInfo[] ofInstrumentPriceInfo() {
        return new InstrumentPriceInfo[]{new InstrumentPriceInfo(VENDOR_1, ISIN_1, USD_CCY, PRICE_AMOUNT, DATE_TIME)};
    }

    private InstrumentPriceQueryService ofInstrumentPriceQueryService() {
        doReturn(INSTRUMENT_PRICES).when(priceRepositoryService).retrieveVendorPrices(VENDOR_1);
        doReturn(INSTRUMENT_PRICES).when(priceRepositoryService).retrievePricesForIsin(ISIN_1);
        return new InstrumentPriceQueryService(priceRepositoryService);
    }

    private static Collection<InstrumentPrice> ofInstrumentPrices() {
        return List.of(new InstrumentPrice(ISIN_1, USD_CCY, PRICE_AMOUNT, DATE_TIME, VENDOR_1));
    }
}