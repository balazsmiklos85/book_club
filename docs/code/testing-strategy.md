# Testing Strategy

We follow the testing pyramid:

1. Unit tests cover as much as possible. Pure functions, no app boot, no database.
2. Integration tests are few and narrow. One integration point each. Never assert business logic.
3. End-to-end tests are extremely rare. At most one per use case.

## Test Types

### Unit

Tests business rules and validation.
Lives in `#[cfg(test)]` next to the code.
Plain `#[test]` functions.

### Data Layer

Tests one specific query.
Lives in `tests/models/`.
`boot_test::<App>()` + `seed::<App>()`

### HTTP Contract

Tests routing, status codes, redirects, auth.
Lives in `tests/requests`.
`request::<App, _, _>()`

### View Rendering

Tests rendered HTML structure.
Lives in `tests/views`.
Selector assertions.

### Workers and Tasks

Note: `config/test.yaml` runs workers `ForegroundBlocking`, so `perform_later`
executes synchronously — no sleeps, no polling.

### E2E Journey

Tests one full use case, multistep.
Lives in `tests/requests/`.
`request::<App, _, _>()`, multistep.

## Code Organization

Business logic lives in pure functions: plain data in, data or `Result` out. No `AppContext`, no `DatabaseConnection` parameters.
Database access stays a thin shell. Fetch rows, call pure function, save.
Controllers stay thin: extract parameters, call logic, respond.
Logic testable only through the database is a design bug: extract the decision from the fetch.

## Conventions

- Name test functions by the behavior they verify: `can_login_with_valid_credentials`, `returns_401_without_token`!
- One behavior per test! Shared setup becomes a small helper function, not a copy-paste block.
- Integration test files mirror the component they exercise. `tests/models/books.rs` tests `src/models/books.rs`.

## Test Data

Static YAML fixtures in `src/fixtures`. One file per table, loaded through `Hooks::seed`. Integration tests call `seed::<App>()` after boot; include every `NOT NULL` column, omit nullable ones. Fixtures are the shared baseline for all tests. When a test needs beyond that, insert extra rows via `ActiveModel` in the test body.

## Running Tests

`cargo test` the whole suite.

To filter by module: `cargo test models::`, `cargo test requests::books`.

Snapshot changes are reviewed with `cargo insta review` and accepted deliberately, never blindly.
