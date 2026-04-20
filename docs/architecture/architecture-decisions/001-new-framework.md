# New Framework Selection

## Status

Accepted

## Context

The book club voting system requires users to register, log in, view suggested books sorted by votes, suggest new books, and vote for books. Administrators must create events from suggestions, while tracking event participation to affect user voting weights. The initial implementation used Spring 3 with Java. The rewrite targets Ruby for improved developer experience.

Available Ruby web frameworks include Rails, Sinatra, Hanami, and others. Each presents different trade-offs regarding architecture patterns, testing capabilities, and feature scope.

## Decision

Use **Hanami 2.3** as of March 2026.

### Rationale

- Hanami follows Clean Architecture principles by default, avoiding the anaemic domain model common in Spring applications
- The framework prioritizes testability over the active record approach used by Rails
- Component-based structure supports scalable growth as features expand
- Built-in web rendering eliminates the need for a separate frontend layer, unlike Sinatra

## Consequences

### Positive

- Clean Architecture enforces separation of concerns throughout the codebase
- Component isolation simplifies testing and maintenance
- Single framework handles both API logic and web rendering

### Negative

- Smaller community compared to Rails may limit available gems and third-party support
- Learning curve for developers unfamiliar with component-based architecture

## References

- [Hanami 2.3 Official Documentation](https://guides.hanamirb.org/v2.3/introduction/getting-started/)
