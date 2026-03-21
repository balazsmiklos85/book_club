# Contributing Guidelines

## Development Setup

Clone the repository and follow the [installation instructions](./docs/context/deployment.md)!

## Coding Standards

- The code style is enforced by [Rubocop](./.rubocop.yml).
- Use object-oriented design when applicable!
- Do not use inline code comments! If you need any inline code comments, it's a good indicator that you need to structure your code better or you need better names for your methods/classes.
- Add RDoc comments to all public methods and classes!
- Keep code clean, readable, and idiomatic Ruby!
- TODO: Define naming conventions for files and classes.
- TODO: Document error handling patterns used in the application.
- TODO: Describe how dependencies are injected and managed.
- TODO: Define logging standards and practices.
- TODO: Specify API design conventions for endpoints.

## Commit Messages

Write clear, descriptive commit messages. One commit should contain just one change. Rule of thumb: if you need more than one gitmoji to represent your change, it is too big.

## Pull Requests

1. Fork the repository!
2. Create a feature branch from `main`!
3. Make your changes and write tests!
4. Ensure all tests pass!
5. Submit a pull request with a clear description of the changes!
6. Merge, not squash! This keeps the individual changes retracable in the future.

## Testing

- Write exhaustive unit tests for business logic in the domain layer!
- Write integration tests for the happy path of the implemented feature!
- Strive to increase test coverage!
- Run the full test suite locally before submitting pull requests!

