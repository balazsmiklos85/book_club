# Routes

## Overview

Routes map HTTP requests to controller actions.

## Reference

### Authentication Routes

**GET /login** - `Login::Index`
: Display login form

**POST /session** - `Session::Create`
: Process login submission

### Session Configuration

- **Session type**: Cookie-based
- **Session key**: `book_club.session`
- **Session secret**: Configured in application settings
- **Session data**: Stores `external_id` for authenticated users