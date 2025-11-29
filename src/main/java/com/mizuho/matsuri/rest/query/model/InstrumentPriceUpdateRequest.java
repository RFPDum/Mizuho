package com.mizuho.matsuri.rest.query.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class InstrumentPriceUpdateRequest {
    private String providerId;
    private String isin;
    private String currency;
    private Double price;
    private String priceDate;
}
