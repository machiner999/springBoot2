# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 3.5 REST API that returns the current silver price in JPY per troy ounce and per gram. Fetches silver price (USD) from gold-api.com and FX rate (USD→JPY) from exchangerate.host, then combines them.

## Build & Run Commands

```bash
# Build
./gradlew clean build

# Run (requires GOLDAPI_API_KEY env var)
export GOLDAPI_API_KEY=your_key
./gradlew bootRun

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests ClassName

# Run a single test method
./gradlew test --tests ClassName.methodName
```

Java 17+ required. Gradle wrapper included (`./gradlew`).

## Architecture

Single-module Gradle project. All classes are in `com.example.silverapi`, organized by layer:

```
silverapi/
├── config/        # Configuration properties + RestClient beans
├── controller/    # REST endpoints
├── dto/           # Request/response DTOs (records for outgoing, classes for external API)
├── exception/     # Custom exceptions + GlobalExceptionHandler
└── service/       # Business logic
```

**Request flow:** `SilverPriceController` (`GET /api/silver/price`) → `SilverPriceService` → calls gold-api.com for XAG/USD price + open.er-api.com for USD→JPY rate → combines and returns `SilverPriceResponse`

- Uses synchronous `RestClient` (not WebClient/WebFlux)
- Two `RestClient` beans: `silverApiRestClient` (metal price) and `fxRestClient` (FX rates)
- `GlobalExceptionHandler` maps `SilverApiConfigurationException` → 503, `SilverApiUnavailableException` → 502
- Response DTOs use Java `record`; external API DTOs use classes with Jackson annotations

## Configuration

`src/main/resources/application.yml` — properties under `silverapi`:
- `base-url` — metal price API (default: `https://api.gold-api.com`)
- `fx-base-url` — FX rate API (default: `https://open.er-api.com/v6`)
- `api-key` — resolved from `${GOLDAPI_API_KEY:}` env var

Server runs on port 8080.
