package com.example.silverapi.service;

import com.example.silverapi.dto.FxRatesResponse;
import com.example.silverapi.dto.SilverApiComResponse;
import com.example.silverapi.dto.SilverPriceResponse;
import com.example.silverapi.exception.SilverApiUnavailableException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.format.DateTimeParseException;

@Service
public class SilverPriceService {
    private static final BigDecimal TROY_OUNCE_IN_GRAMS = new BigDecimal("31.1034768");
    private static final String METAL_SYMBOL = "XAG";
    private static final String TARGET_CURRENCY = "JPY";
    private static final String FX_BASE_CURRENCY = "USD";

    private final RestClient silverApiRestClient;
    private final RestClient fxRestClient;

    public SilverPriceService(RestClient silverApiRestClient, RestClient fxRestClient) {
        this.silverApiRestClient = silverApiRestClient;
        this.fxRestClient = fxRestClient;
    }

    public SilverPriceResponse getCurrentPrice() {
        SilverApiComResponse silverResponse;
        try {
            silverResponse = silverApiRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/price/" + METAL_SYMBOL)
                            .build())
                    .retrieve()
                    .body(SilverApiComResponse.class);
        } catch (RestClientException ex) {
            throw new SilverApiUnavailableException("Failed to fetch silver price from Gold API.", ex);
        }

        if (silverResponse == null || silverResponse.getPrice() == null) {
            throw new SilverApiUnavailableException("Gold API returned an empty response.", null);
        }

        FxRatesResponse fxResponse;
        try {
            fxResponse = fxRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/latest")
                            .queryParam("base", FX_BASE_CURRENCY)
                            .queryParam("symbols", TARGET_CURRENCY)
                            .build())
                    .retrieve()
                    .body(FxRatesResponse.class);
        } catch (RestClientException ex) {
            throw new SilverApiUnavailableException("Failed to fetch FX rate from upstream API.", ex);
        }

        if (fxResponse == null || fxResponse.getRates() == null
                || fxResponse.getRates().get(TARGET_CURRENCY) == null) {
            throw new SilverApiUnavailableException("FX API returned an empty response.", null);
        }

        BigDecimal usdToJpy = fxResponse.getRates().get(TARGET_CURRENCY);
        BigDecimal pricePerOunce = silverResponse.getPrice()
                .multiply(usdToJpy)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal pricePerGram = silverResponse.getPrice()
                .multiply(usdToJpy)
                .divide(TROY_OUNCE_IN_GRAMS, 4, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);

        Instant timestamp = parseTimestamp(silverResponse.getUpdatedAt());

        return new SilverPriceResponse(
                METAL_SYMBOL,
                TARGET_CURRENCY,
                pricePerOunce,
                pricePerGram,
                timestamp,
                "gold-api.com + exchangerate.host"
        );
    }

    private static Instant parseTimestamp(String raw) {
        if (raw == null || raw.isBlank()) {
            return Instant.now();
        }
        try {
            return Instant.parse(raw);
        } catch (DateTimeParseException ex) {
            return Instant.now();
        }
    }
}
