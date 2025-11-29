package com.mizuho.matsuri;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class PriceRepoServiceTest {

    @Test
    public void should_accept_input() {
        // Given
        final PriceRepoService priceRepoService = ofPriceRepoService();
        final String testData = "test";

        // When
        priceRepoService.acceptData(testData);

        // Then
        final String data = priceRepoService.getData();
        assertThat(data).isEqualTo(testData);
    }

    private PriceRepoService ofPriceRepoService() {
        return new PriceRepoService();
    }
}