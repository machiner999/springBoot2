package com.example.silverapi.controller;

import com.example.silverapi.dto.SilverPriceResponse;
import com.example.silverapi.service.SilverPriceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/silver")
public class SilverPriceController {
    private final SilverPriceService service;

    public SilverPriceController(SilverPriceService service) {
        this.service = service;
    }

    @GetMapping("/price")
    public SilverPriceResponse getSilverPrice() {
        return service.getCurrentPrice();
    }
}
