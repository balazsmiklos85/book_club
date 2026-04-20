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