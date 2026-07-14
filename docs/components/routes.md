# Routes

## Overview

Routes map HTTP requests to controller actions.

## Reference

### Authentication Routes

**GET /login** - `Login::Index`
: Display login form

**POST /session** - `Session::Create`
: Process login submission

### Registration Routes

**GET /register** - `Register::New`
: Display registration form

**POST /register** - `Register::Create`
: Process registration submission

### Session Configuration

- **Session type**: Cookie-based
- **Session key**: `book_club.session`
- **Session secret**: Configured in application settings
- **Session data**: Stores `external_id` for authenticated users