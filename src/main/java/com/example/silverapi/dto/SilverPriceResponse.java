package com.example.silverapi.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record SilverPriceResponse(
        String metal,
        String currency,
        BigDecimal priceJpyPerOunce,
        BigDecimal priceJpyPerGram,
        Instant timestamp,
        String source
) {
}
