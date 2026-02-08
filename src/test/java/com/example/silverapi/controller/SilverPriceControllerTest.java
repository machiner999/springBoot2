package com.example.silverapi.controller;

import com.example.silverapi.dto.SilverPriceResponse;
import com.example.silverapi.service.SilverPriceService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SilverPriceControllerTest {

    @Test
    void getPrice_delegatesToService() {
        SilverPriceService service = mock(SilverPriceService.class);
        SilverPriceResponse expected = new SilverPriceResponse(
                "XAG",
                "JPY",
                new BigDecimal("1000.00"),
                new BigDecimal("32.15"),
                Instant.parse("2024-01-01T00:00:00Z"),
                "source"
        );
        when(service.getCurrentPrice()).thenReturn(expected);

        SilverPriceController controller = new SilverPriceController(service);
        SilverPriceResponse actual = controller.getSilverPrice();

        assertEquals(expected, actual);
        verify(service).getCurrentPrice();
    }
}
