package com.mizuho.matsuri.pricestore.service.impl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@AllArgsConstructor
public class ScheduledCachePurge {
    /**
     * Purge the instrument price cache every day at 9pm
     */
    private final InstrumentPriceCache instrumentPriceCache;

	private static final Logger log = LoggerFactory.getLogger(ScheduledCachePurge.class);

    @Scheduled(cron = "0 0 21 * * *")
	public void purgeCache() {
        log.info("Purging data cache on: {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        instrumentPriceCache.purge();
	}
}