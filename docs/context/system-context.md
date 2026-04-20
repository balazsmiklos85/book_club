# System Context

## Overview

This document describes the system context, use cases, and external dependencies.

## Use Cases

### How to Log In

1. Navigate to `/login` in a browser
2. Enter email address in the email field
3. Enter password in the password field
4. Submit the form

On success, the system redirects to the homepage and creates an authenticated session.

On failure, the system redirects back to `/login` with an error message displayed.

### Session Behavior

- The session persists across browser requests
- The session stores the user's `external_id`
- Unauthenticated requests to protected routes redirect to `/login`

## External Systems

[Document external systems and integrations here]