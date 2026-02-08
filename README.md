# Spring Boot Silver Price API

## Overview
Spring Boot 3.5 REST API that returns the current silver price in JPY.

## Requirements
- Java 21+
- Gradle (wrapper included)
- API key for Gold API (set as env `GOLDAPI_API_KEY`)

## Run
```bash
export GOLDAPI_API_KEY=your_key
./gradlew bootRun
```

## Quality Checks
```bash
./gradlew clean check
./gradlew jacocoTestReport
```

## Dependency Check (Local Only)
```bash
export NVD_API_KEY=your_key
./gradlew dependencyCheckAnalyze
```

Reports are generated under:
- `build/reports/tests/test/index.html`
- `build/reports/jacoco/test/html/index.html`
- `build/reports/checkstyle/`
- `build/reports/spotbugs/`
- `build/reports/dependency-check-report.html`

## GitHub Settings
- Protect `main` and require the `ci` workflow to pass on pull requests.
- Add `GOLDAPI_API_KEY` in GitHub Actions Secrets if needed for live API tests.

## Endpoint
`GET /api/silver/price`

Example response:
```json
{
  "metal": "XAG",
  "currency": "JPY",
  "priceJpyPerOunce": 4567.89,
  "priceJpyPerGram": 146.87,
  "timestamp": "2026-02-07T00:00:00Z",
  "source": "gold-api.com + exchangerate.host"
}
```

## Notes
- Silver price (USD) from `https://api.gold-api.com`
- FX rate (USD → JPY) from `https://open.er-api.com/v6`
- Override via `silverapi.base-url`, `silverapi.fx-base-url`, `silverapi.api-key` in `application.yml`
