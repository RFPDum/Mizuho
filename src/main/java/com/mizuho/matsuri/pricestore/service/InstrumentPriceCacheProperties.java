package com.mizuho.matsuri.pricestore.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "price-store.instrument-price-cache")
@Getter
@Setter
@ToString
public class InstrumentPriceCacheProperties {
    private int retentionPeriodInDays = 30;
}
