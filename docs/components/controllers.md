# Controllers

## Overview

Controllers handle HTTP requests by invoking operations for business logic and rendering responses.

## Reference

### Authentication Actions

**Login::New** - `app/actions/login/new.rb`
- Route: `GET /login`
- Displays the login form

**Session::Create** - `app/actions/session/create.rb`
- Route: `POST /session`
- Processes login credentials
- On success: creates session with user external_id, redirects to homepage
- On failure: redirects to `/login` with error

### Registration Actions

**Register::New** - `app/actions/register/new.rb`
- Route: `GET /register`
- Displays the registration form

**Register::Create** - `app/actions/register/create.rb`
- Route: `POST /register`
- Processes registration by invoking the `Register` operation
- On success: stores the user's `external_id` in the session, redirects to homepage
- On failure: redirects to `/register` with flash error

### Protected Actions

Actions that require authentication include the `AuthenticatedAction` module. This module:
- Checks for `external_id` in the session
- Redirects unauthenticated users to `/login`
- Sets a flash error message before redirecting

## Tutorial: How to Protect an Action

To require authentication for an action, include the `AuthenticatedAction` module.

```ruby
module BookClub
  module Actions
    module Books
      class Index < Action
        include AuthenticatedAction

        def handle(request, response)
          # Only authenticated users reach this code
        end
      end
    end
  end
end
```

The `before` callback runs before the request handling. If the session lacks `external_id`, the callback:
1. Sets a flash error message
2. Redirects to `/login`
3. Halts further execution