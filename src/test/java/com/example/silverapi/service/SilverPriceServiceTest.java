package com.example.silverapi.service;

import com.example.silverapi.config.SilverApiProperties;
import com.example.silverapi.dto.SilverPriceResponse;
import com.example.silverapi.exception.SilverApiUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class SilverPriceServiceTest {

    private SilverPriceService createService(RestClient silverClient, RestClient fxClient) {
        SilverApiProperties properties = new SilverApiProperties();
        return new SilverPriceService(silverClient, fxClient, properties);
    }

    @Test
    void getCurrentPrice_success() {
        RestClient.Builder silverBuilder = RestClient.builder().baseUrl("https://silver.example");
        RestClient.Builder fxBuilder = RestClient.builder().baseUrl("https://fx.example");
        MockRestServiceServer silverServer = MockRestServiceServer.bindTo(silverBuilder).build();
        MockRestServiceServer fxServer = MockRestServiceServer.bindTo(fxBuilder).build();
        RestClient silverClient = silverBuilder.build();
        RestClient fxClient = fxBuilder.build();

        silverServer.expect(requestTo("https://silver.example/price/XAG"))
                .andRespond(withSuccess("{" +
                        "\"symbol\":\"XAG\"," +
                        "\"price\":25.5," +
                        "\"updatedAt\":\"2024-01-01T00:00:00Z\"" +
                        "}", MediaType.APPLICATION_JSON));

        fxServer.expect(requestTo("https://fx.example/latest?base=USD&symbols=JPY"))
                .andRespond(withSuccess("{" +
                        "\"base\":\"USD\"," +
                        "\"rates\":{\"JPY\":150.0}" +
                        "}", MediaType.APPLICATION_JSON));

        SilverPriceService service = createService(silverClient, fxClient);
        SilverPriceResponse response = service.getCurrentPrice();

        BigDecimal expectedPerOunce = new BigDecimal("25.5")
                .multiply(new BigDecimal("150.0"))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedPerGram = new BigDecimal("25.5")
                .multiply(new BigDecimal("150.0"))
                .divide(new BigDecimal("31.1034768"), 4, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);

        assertEquals("XAG", response.metal());
        assertEquals("JPY", response.currency());
        assertEquals(expectedPerOunce, response.priceJpyPerOunce());
        assertEquals(expectedPerGram, response.priceJpyPerGram());
        assertEquals(Instant.parse("2024-01-01T00:00:00Z"), response.timestamp());

        silverServer.verify();
        fxServer.verify();
    }

    @Test
    void getCurrentPrice_silverApiServerError() {
        RestClient.Builder silverBuilder = RestClient.builder().baseUrl("https://silver.example");
        RestClient.Builder fxBuilder = RestClient.builder().baseUrl("https://fx.example");
        MockRestServiceServer silverServer = MockRestServiceServer.bindTo(silverBuilder).build();
        RestClient silverClient = silverBuilder.build();
        RestClient fxClient = fxBuilder.build();

        silverServer.expect(requestTo("https://silver.example/price/XAG"))
                .andRespond(withServerError());

        SilverPriceService service = createService(silverClient, fxClient);

        assertThrows(SilverApiUnavailableException.class, service::getCurrentPrice);
        silverServer.verify();
    }

    @Test
    void getCurrentPrice_silverApiEmptyPrice() {
        RestClient.Builder silverBuilder = RestClient.builder().baseUrl("https://silver.example");
        RestClient.Builder fxBuilder = RestClient.builder().baseUrl("https://fx.example");
        MockRestServiceServer silverServer = MockRestServiceServer.bindTo(silverBuilder).build();
        RestClient silverClient = silverBuilder.build();
        RestClient fxClient = fxBuilder.build();

        silverServer.expect(requestTo("https://silver.example/price/XAG"))
                .andRespond(withSuccess("{" +
                        "\"symbol\":\"XAG\"" +
                        "}", MediaType.APPLICATION_JSON));

        SilverPriceService service = createService(silverClient, fxClient);

        assertThrows(SilverApiUnavailableException.class, service::getCurrentPrice);
        silverServer.verify();
    }

    @Test
    void getCurrentPrice_fxApiServerError() {
        RestClient.Builder silverBuilder = RestClient.builder().baseUrl("https://silver.example");
        RestClient.Builder fxBuilder = RestClient.builder().baseUrl("https://fx.example");
        MockRestServiceServer silverServer = MockRestServiceServer.bindTo(silverBuilder).build();
        MockRestServiceServer fxServer = MockRestServiceServer.bindTo(fxBuilder).build();
        RestClient silverClient = silverBuilder.build();
        RestClient fxClient = fxBuilder.build();

        silverServer.expect(requestTo("https://silver.example/price/XAG"))
                .andRespond(withSuccess("{" +
                        "\"symbol\":\"XAG\"," +
                        "\"price\":25.5" +
                        "}", MediaType.APPLICATION_JSON));

        fxServer.expect(requestTo("https://fx.example/latest?base=USD&symbols=JPY"))
                .andRespond(withServerError());

        SilverPriceService service = createService(silverClient, fxClient);

        assertThrows(SilverApiUnavailableException.class, service::getCurrentPrice);
        silverServer.verify();
        fxServer.verify();
    }

    @Test
    void getCurrentPrice_fxMissingRate() {
        RestClient.Builder silverBuilder = RestClient.builder().baseUrl("https://silver.example");
        RestClient.Builder fxBuilder = RestClient.builder().baseUrl("https://fx.example");
        MockRestServiceServer silverServer = MockRestServiceServer.bindTo(silverBuilder).build();
        MockRestServiceServer fxServer = MockRestServiceServer.bindTo(fxBuilder).build();
        RestClient silverClient = silverBuilder.build();
        RestClient fxClient = fxBuilder.build();

        silverServer.expect(requestTo("https://silver.example/price/XAG"))
                .andRespond(withSuccess("{" +
                        "\"symbol\":\"XAG\"," +
                        "\"price\":25.5" +
                        "}", MediaType.APPLICATION_JSON));

        fxServer.expect(requestTo("https://fx.example/latest?base=USD&symbols=JPY"))
                .andRespond(withSuccess("{" +
                        "\"base\":\"USD\"," +
                        "\"rates\":{}" +
                        "}", MediaType.APPLICATION_JSON));

        SilverPriceService service = createService(silverClient, fxClient);

        assertThrows(SilverApiUnavailableException.class, service::getCurrentPrice);
        silverServer.verify();
        fxServer.verify();
    }

    @Test
    void getCurrentPrice_fxNullRates() {
        RestClient.Builder silverBuilder = RestClient.builder().baseUrl("https://silver.example");
        RestClient.Builder fxBuilder = RestClient.builder().baseUrl("https://fx.example");
        MockRestServiceServer silverServer = MockRestServiceServer.bindTo(silverBuilder).build();
        MockRestServiceServer fxServer = MockRestServiceServer.bindTo(fxBuilder).build();
        RestClient silverClient = silverBuilder.build();
        RestClient fxClient = fxBuilder.build();

        silverServer.expect(requestTo("https://silver.example/price/XAG"))
                .andRespond(withSuccess("{" +
                        "\"symbol\":\"XAG\"," +
                        "\"price\":25.5" +
                        "}", MediaType.APPLICATION_JSON));

        fxServer.expect(requestTo("https://fx.example/latest?base=USD&symbols=JPY"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        SilverPriceService service = createService(silverClient, fxClient);

        assertThrows(SilverApiUnavailableException.class, service::getCurrentPrice);
        silverServer.verify();
        fxServer.verify();
    }

    @Test
    void getCurrentPrice_blankTimestampUsesNow() {
        RestClient.Builder silverBuilder = RestClient.builder().baseUrl("https://silver.example");
        RestClient.Builder fxBuilder = RestClient.builder().baseUrl("https://fx.example");
        MockRestServiceServer silverServer = MockRestServiceServer.bindTo(silverBuilder).build();
        MockRestServiceServer fxServer = MockRestServiceServer.bindTo(fxBuilder).build();
        RestClient silverClient = silverBuilder.build();
        RestClient fxClient = fxBuilder.build();

        silverServer.expect(requestTo("https://silver.example/price/XAG"))
                .andRespond(withSuccess("{" +
                        "\"symbol\":\"XAG\"," +
                        "\"price\":25.5," +
                        "\"updatedAt\":\"\"" +
                        "}", MediaType.APPLICATION_JSON));

        fxServer.expect(requestTo("https://fx.example/latest?base=USD&symbols=JPY"))
                .andRespond(withSuccess("{" +
                        "\"base\":\"USD\"," +
                        "\"rates\":{\"JPY\":150.0}" +
                        "}", MediaType.APPLICATION_JSON));

        SilverPriceService service = createService(silverClient, fxClient);

        Instant before = Instant.now();
        SilverPriceResponse response = service.getCurrentPrice();
        Instant after = Instant.now();

        assertTrue(!response.timestamp().isBefore(before) && !response.timestamp().isAfter(after));
        silverServer.verify();
        fxServer.verify();
    }

    @Test
    void getCurrentPrice_invalidTimestampUsesNow() {
        RestClient.Builder silverBuilder = RestClient.builder().baseUrl("https://silver.example");
        RestClient.Builder fxBuilder = RestClient.builder().baseUrl("https://fx.example");
        MockRestServiceServer silverServer = MockRestServiceServer.bindTo(silverBuilder).build();
        MockRestServiceServer fxServer = MockRestServiceServer.bindTo(fxBuilder).build();
        RestClient silverClient = silverBuilder.build();
        RestClient fxClient = fxBuilder.build();

        silverServer.expect(requestTo("https://silver.example/price/XAG"))
                .andRespond(withSuccess("{" +
                        "\"symbol\":\"XAG\"," +
                        "\"price\":25.5," +
                        "\"updatedAt\":\"not-a-timestamp\"" +
                        "}", MediaType.APPLICATION_JSON));

        fxServer.expect(requestTo("https://fx.example/latest?base=USD&symbols=JPY"))
                .andRespond(withSuccess("{" +
                        "\"base\":\"USD\"," +
                        "\"rates\":{\"JPY\":150.0}" +
                        "}", MediaType.APPLICATION_JSON));

        SilverPriceService service = createService(silverClient, fxClient);

        Instant before = Instant.now();
        SilverPriceResponse response = service.getCurrentPrice();
        Instant after = Instant.now();

        assertTrue(!response.timestamp().isBefore(before) && !response.timestamp().isAfter(after));
        silverServer.verify();
        fxServer.verify();
    }
}
