# Local Development Setup

This guide explains how to set up the Book Club Voting System for local development.

## Prerequisites

- Ruby 4.0
- Node.js
- PostgreSQL

## Installation

Follow these steps to install and configure the application.

### Clone the repository

```bash
git clone git@codeberg.org:balazsmiklos85/book_club.git
cd book_club
```

### Install dependencies

Install Ruby and Node.js dependencies.

```bash
bundle install
npm install
```

### Configure environment variables

The application uses environment variables for configuration. The repository includes a `.env` file with non-sensitive defaults. Create a `.env.local` file for sensitive values.

Set the following required variables:

- `SESSION_SECRET` - Secret key for session cookies
- `DATABASE_URL` - PostgreSQL connection string

### Set up the database

Create the database and run all pending migrations.

```bash
bundle exec hanami db create
bundle exec hanami db migrate
```

### Compile assets

Compile frontend assets for development.

```bash
bundle exec hanami assets compile
```

## Running the application

Start the development server.

```bash
bundle exec hanami server
```

Run asset watching in a separate terminal for live updates.

```bash
bundle exec hanami assets watch
```

The server binds to port 2300 by default. Override the port by setting the `HANAMI_PORT` environment variable.

## Running tests

Run the test suite. See [How to Write Tests](../code/testing-strategy.md) for test database setup and detailed instructions.

```bash
bundle exec rspec
```

## Additional configuration

- **Puma server**: Override server settings in `config/puma.rb` if needed.
- **Database seeds**: Add seed data in `config/db/seeds.rb` and load with `bundle exec hanami db seed`.
