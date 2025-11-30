package com.mizuho.matsuri.rest.feed.controler;

import com.mizuho.matsuri.rest.feed.impl.InstrumentPriceFeedService;
import com.mizuho.matsuri.rest.query.model.InstrumentPriceUpdateRequest;
import com.mizuho.matsuri.rest.util.ResponseUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class InstrumentPriceFeedRestControllerTest {
    public static final String ERROR = "Error";

    private final InstrumentPriceFeedService instrumentPriceFeedService = mock(InstrumentPriceFeedService.class);

    @Test
    void should_call_instrument_price_feed_service_addPriceUpdate_when_addInstrumentPrice_is_called() {
        // Given
        final InstrumentPriceFeedRestController restController = ofInstrumentPriceFeedRestController(true);
        final InstrumentPriceUpdateRequest priceUpdateRequest  = ofInstrumentPriceUpdateRequest("Bloomberg", "US41545456");

        // When
        restController.addInstrumentPrice(priceUpdateRequest);

        // Then
        verify(instrumentPriceFeedService).addPriceUpdate(priceUpdateRequest);
    }

    @Test
    void should_return_response_OK_when_instrumentPriceFeedService_returns_OK() {
        // Given
        final InstrumentPriceFeedRestController restController = ofInstrumentPriceFeedRestController(true);
        final InstrumentPriceUpdateRequest priceUpdateRequest  = ofInstrumentPriceUpdateRequest("Barclays", "US98155422");
        final ResponseEntity<String> expectedResponse          = ResponseUtils.ofOKResponse();

        // When
        final ResponseEntity<String> response = restController.addInstrumentPrice(priceUpdateRequest);

        // Then
        Assertions.assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void should_return_response_fail_when_instrumentPriceFeedService_returns_fail() {
        // Given
        final InstrumentPriceFeedRestController restController = ofInstrumentPriceFeedRestController(false);
        final InstrumentPriceUpdateRequest priceUpdateRequest  = ofInstrumentPriceUpdateRequest("Barclays", "US98155422");
        final ResponseEntity<String> expectedResponse          = ResponseUtils.ofFailedResponse(ERROR);

        // When
        final ResponseEntity<String> response = restController.addInstrumentPrice(priceUpdateRequest);

        // Then
        Assertions.assertThat(response).isEqualTo(expectedResponse);
    }

    private InstrumentPriceFeedRestController ofInstrumentPriceFeedRestController(boolean isResponseOK) {
        doReturn(isResponseOK ? ResponseUtils.ofOKResponse() : ResponseUtils.ofFailedResponse("Error"))
                .when(instrumentPriceFeedService).addPriceUpdate(any(InstrumentPriceUpdateRequest.class));
        return new InstrumentPriceFeedRestController(instrumentPriceFeedService);
    }

    private InstrumentPriceUpdateRequest ofInstrumentPriceUpdateRequest(String vendorId, String isin) {
        return new InstrumentPriceUpdateRequest(vendorId, isin, "GBP", 1000d, LocalDateTime.now().toString());
    }
}