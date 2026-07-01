# Repository Memory

## Build and test
- Build command: `mvn clean verify`
- Docker is unavailable in this environment; tests use H2 in-memory DB mode when persistence tests exist.

## Project structure
- Main package: `com.example.unitconv`
- API layer: `api.controller`, `api.request`, `api.response`, `api.advice`
- Application layer: `application` and `application.exception`

## Current domain rules
- Stateless conversion service; nothing is persisted.
- Supported unit pairs only: metres <-> feet, kilometres <-> miles, litres <-> gallons.
- Cross-group conversions must fail with HTTP 400.
- Supported unit list must expose canonical names and measurement systems.

## Implementation notes
- Request/response DTOs are Java records.
- Centralized error handling lives in `api.advice.ApiExceptionHandler`.
- Error responses always include a `message` field and a generated `id`.
- Conversion factors are implemented with BigDecimal and a fixed scale for reverse-conversion tolerance.
