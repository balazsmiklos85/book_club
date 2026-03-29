# How to Write Tests

This guide shows you how to write tests for the Book Club application. Use it when you need to add tests for authentication, endpoints, or business logic.

Place tests in the `spec/` directory:
- `spec/requests/` - HTTP endpoint tests
- `spec/operations/` - Business logic tests
- `spec/features/` - Browser integration tests
- `spec/relations/` - Data layer tests
- `spec/views/` - View rendering tests
- `spec/support/` - Test helpers

## How to Write Request Specs

Request specs test HTTP endpoints. Use them to verify routing, parameter handling, redirects, and session behavior.

```ruby
RSpec.describe 'Login', type: :request do
  describe 'POST /session' do
    context 'with valid credentials' do
      let(:user) { TestFactories.create_user(email: 'user@example.com', password: 'secret123') }

      it 'creates a session and redirects to the homepage' do
        post '/session', email: 'user@example.com', password: 'secret123'
        expect(last_response.status).to eq(303)
        expect(last_response.headers['Location']).to eq('/')
      end
    end
  end
end
```

## How to Write Operation Specs

Operation specs test business logic in isolation. Use them to verify authentication, validations, and core rules.

```ruby
RSpec.describe Operations::Login do
  let(:email_repo) { instance_double(Repos::EmailRepo) }
  subject(:login) { described_class.new(email_repo: email_repo) }

  describe '#call' do
    context 'when user does not exist' do
      before do
        allow(email_repo).to receive(:find_with_user_and_password).and_return(nil)
      end

      it 'returns a failure' do
        result = login.call(email: 'bad@example.com', password: 'any')
        expect(result.failure).to eq(:user_not_found)
      end
    end
  end
end
```

## How to Write Feature Specs

Feature specs use Capybara to simulate browser interactions. Use them for critical end-to-end user flows.

```ruby
RSpec.describe 'Login Flow', type: :feature do
  let(:app) { Hanami.app }
  before { Capybara.app = app }

  scenario 'user logs in successfully' do
    TestFactories.create_user(email: 'user@example.com', password: 'secret123')
    visit '/login'
    fill_in 'Email', with: 'user@example.com'
    fill_in 'Password', with: 'secret123'
    click_button 'Sign In'
    expect(current_path).to eq('/')
  end
end
```

## How to Stub and Mock

Use doubles to isolate tests from external dependencies.

**Stubs** return predefined values:

```ruby
allow(email_repo).to receive(:find).and_return(user)
```

**Mocks** verify that a method was called:

```ruby
expect(email_repo).to receive(:find).with('user@example.com')
```

Prefer stubs for returning data. Use mocks sparingly to verify critical interactions.

## Test Data Management

Create a test factory to generate consistent test data. Place it in `spec/support/factories.rb`:

```ruby
# frozen_string_literal: true

module TestFactories
  def self.create_user(email:, password: 'password123', name: 'Test User')
    container = Hanami.app['db.rom']
    external_id = SecureRandom.uuid

    # ... insert user logic here
    OpenStruct.new(external_id: external_id, email: email)
  end
end
```

## How to Organize Test Code

### Naming Conventions

Name your tests to describe what behavior they verify. Use nested `describe` blocks for context and clear action verbs in examples:

```ruby
RSpec.describe 'Password Reset', type: :request do
  describe 'POST /password-reset' do
    context 'with invalid email' do
      it 'returns success without revealing user existence' do
        post '/password-reset', email: 'nonexistent@example.com'
        expect(last_response.status).to eq(204)
      end
    end
  end
end
```

### Using `let` Blocks

Use `let` to define reusable test data. Place it in the most specific context where it's needed:

```ruby
RSpec.describe 'User Profile', type: :request do
  let(:user) { TestFactories.create_user(email: 'user@example.com') }

  describe 'GET /profile' do
    # ...
  end
end
```

### Shared Examples

Extract common behavior into shared examples to avoid repetition. Place them in `spec/support/shared_examples`:

```ruby
# spec/support/shared_examples/authenticated_endpoint.rb
RSpec.shared_examples 'authenticated endpoint' do
  context 'without authentication' do
    it 'returns a 401 status' do
      get '/protected-endpoint'
      expect(last_response.status).to eq(401)
    end
  end
end
```

Use shared examples in your specs:

```ruby
RSpec.describe 'Protected Endpoint', type: :request do
  it_behaves_like 'authenticated endpoint'
end

## Test Database Setup

Your test database is automatically cleaned between tests tagged with `:db` (which includes all feature specs). For request specs that need database access, tag them:

```ruby
RSpec.describe 'Protected Route', type: :request do
  describe 'GET /leaderboard', :db do
    # Database is cleaned between these tests
  end
end
```

## Running Tests

Run all tests:

```bash
bundle exec rspec
```

Run only request specs:

```bash
bundle exec rspec spec/requests
```

Run only operation specs:

```bash
bundle exec rspec spec/operations
```

Run with coverage report:

```bash
bundle exec rspec --format progress --profile
```

## Recommended Test Priority

For a new project with limited testing resources, focus your efforts in this order:

1. **Operation specs for authentication** - Test the Login operation thoroughly. This is your core security logic and must be correct.

2. **Request specs for login endpoints** - Verify the HTTP contract works correctly. Test both success and failure scenarios.

3. **Request specs for session handling** - Test logout functionality and session persistence.

4. **Feature specs for critical flows** - Add browser tests for login and logout once the lower layers are solid.

5. **Expand coverage** - As you add new features, follow the same pattern: operations first, then requests, then features for critical paths.

## Adding Tests for New Features

When implementing a new feature, follow this workflow:

1. Write operation specs first to define the business logic
2. Write request specs to verify the HTTP interface
3. Add feature specs for critical user journeys

This approach gives you fast feedback during development while ensuring end-to-end coverage for the most important workflows.

## Continuous Improvement

Review your test coverage regularly:

```bash
# See which files have no tests
bundle exec rspec --format json --out coverage.json
```

Add tests for any code paths that are not covered, especially around error handling and edge cases.
