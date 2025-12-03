package com.mizuho.matsuri.pricestore.service.impl;


import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ScheduledTasksTest {
    private final InstrumentPriceCache instrumentPriceCache = mock(InstrumentPriceCache.class);

    @Test
    void should_call_purge_when_purgeCache_is_called() {
        // Given
        final ScheduledCachePurge task = new ScheduledCachePurge(instrumentPriceCache);

        // When
        task.purgeCache();

        // Then
        verify(instrumentPriceCache).purge();
    }
}