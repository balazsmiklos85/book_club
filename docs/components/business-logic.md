# Business Logic

## Overview

Operations encapsulate business rules and orchestrate data flow between repositories and other components.

## How Operations Work

Operations use a pipeline pattern where each step returns either a success or failure result. The `step` method halts execution and returns early if a step fails.

Operations delegate data access to repositories, keeping business logic separate from persistence concerns.

## Login Operation

The Login operation authenticates a user by validating credentials against stored data.

### Authentication Flow

1. **Load user data** - Query the database for the user associated with the provided email address
2. **Verify password** - Compare the submitted password against the stored password hash
3. **Return result** - On success, return the user's external_id; on failure, return an appropriate error

### Error Handling

The operation handles several failure scenarios:

- **User not found** - No user exists with the provided email address
- **Invalid credentials** - The password does not match the stored hash
- **Unexpected error** - A database or other runtime error occurred

All errors are logged for audit purposes.

### Dependencies

The operation depends on:
- `email_repo` - Repository for finding users by email address
- `logger` - For recording authentication events

## Registration Operation

The Registration operation creates a new user account by validating input, persisting the user record, hashing the password, and creating an email record — all within a database transaction.

### Registration Pipeline

1. **Validate input** - Runs the `RegisterContract`; returns `Failure` with errors or `Success` with validated attributes
2. **Create user** - Inserts a user record (name, is_admin: false, external_id) via the users repository; returns the user's `id`
3. **Create password** - Hashes the password via BCrypt and inserts it into the `user_passwords` relation
4. **Create email** - Inserts an email record linked to the user via the emails repository

If any step fails, the transaction is rolled back and an appropriate failure symbol is returned (`:user_creation_failed`, `:password_creation_failed`, or `:email_creation_failed`).

### Validation Contract

`BookClub::Contracts::RegisterContract` enforces the following rules:

- All fields are required: `name`, `email`, `confirm_email`, `password`, `confirm_password`, `external_id`
- `email` and `confirm_email` must match
- `password` and `confirm_password` must match
- `password` must be at least 8 characters
- `email` must match a valid email format

### Dependencies

The operation depends on:
- `repos.users` - Repository for creating user records
- `repos.emails` - Repository for creating email records
- `logger` - For recording registration events
