package com.mizuho.matsuri.rest.feed.impl;

import com.mizuho.matsuri.pricestore.model.InstrumentPrice;
import com.mizuho.matsuri.pricestore.service.PriceRepositoryValidationException;
import com.mizuho.matsuri.pricestore.service.impl.PriceRepositoryService;
import com.mizuho.matsuri.rest.query.model.InstrumentPriceUpdateRequest;
import com.mizuho.matsuri.rest.util.InstrumentPriceAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static com.mizuho.matsuri.rest.util.ResponseUtils.ofFailedResponse;
import static com.mizuho.matsuri.rest.util.ResponseUtils.ofOKResponse;
import static com.mizuho.matsuri.testutils.InstrumentPriceUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class InstrumentPriceFeedServiceTest {
    private static final double        PRICE_AMOUNT = 8450.56d;
    private static final LocalDateTime DATE_TIME    = LocalDateTime.now();
    private static final String        PRICE_DATE   = DATE_TIME.toString();

    private final InstrumentPriceAdapter priceAdapter           = new InstrumentPriceAdapter();
    private final PriceRepositoryService priceRepositoryService = mock(PriceRepositoryService.class);

    @Test
    void should_call_price_repository_service_acceptPriceData_when_calling_addPriceUpdate() throws PriceRepositoryValidationException {
        // Given
        final InstrumentPriceFeedService   service            = ofInstrumentPriceFeedService();
        final InstrumentPriceUpdateRequest priceUpdateRequest = ofPriceUpdateRequest(PRICE_DATE);
        final InstrumentPrice              newInstrumentPrice = ofInstrumentPrice();

        // When
        service.addPriceUpdate(priceUpdateRequest);

        // Then
        verify(priceRepositoryService).acceptPriceData(newInstrumentPrice);
    }

    @Test
    void should_return_ok_response_when_calling_accept_data_with_correct_data() {
        // Given
        final InstrumentPriceFeedService   service            = ofInstrumentPriceFeedService();
        final InstrumentPriceUpdateRequest priceUpdateRequest = ofPriceUpdateRequest(PRICE_DATE);
        final ResponseEntity<String>       expectedResponse   = ofOKResponse();

        // When
        final ResponseEntity<String> response = service.addPriceUpdate(priceUpdateRequest);

        // Then
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void should_return_FailedResponse_response_when_calling_accept_data_with_incorrect_data() {
        // Given
        final InstrumentPriceFeedService   service            = ofInstrumentPriceFeedService();
        final String                       invalidDate        = "Second of November";
        final InstrumentPriceUpdateRequest priceUpdateRequest = ofPriceUpdateRequest(invalidDate);
        final ResponseEntity<String>       expectedResponse   = ofFailedResponse("'Second of November' is not a valid date. Check format");

        // When
        final ResponseEntity<String> response = service.addPriceUpdate(priceUpdateRequest);

        // Then
        assertThat(response).isEqualTo(expectedResponse);
    }


    private InstrumentPriceUpdateRequest ofPriceUpdateRequest(String priceDate) {
        final InstrumentPriceUpdateRequest request = new InstrumentPriceUpdateRequest();
        request.setVendorId(VENDOR_1);
        request.setPrice(PRICE_AMOUNT);
        request.setPriceDate(priceDate);
        request.setCurrency(GBP_CCY);
        request.setIsin(ISIN_3);
        return request;
    }

    private InstrumentPrice ofInstrumentPrice() {
        return new InstrumentPrice(ISIN_3, GBP_CCY, PRICE_AMOUNT, DATE_TIME, VENDOR_1);
    }

    private InstrumentPriceFeedService ofInstrumentPriceFeedService() {
        return new InstrumentPriceFeedService(priceRepositoryService, priceAdapter);
    }
}