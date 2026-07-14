# Database

## Overview

### Database layer architecture

[Describe database layer architecture using ROM]

### Relations

**users** — Stores user accounts.

- `id` (UUID, primary key) — Auto-generated UUID via `Types::Uuid`
- `name` (String, 255) — User's display name
- `is_admin` (Boolean) — Admin flag, defaults to false
- `external_id` (Integer, unique) — External system identifier

Associations: `has_many :emails`, `has_many :suggestions`, `has_many :user_passwords`, `has_many :votes`

**user_passwords** — Stores password hashes for user authentication.

- `user_id` (UUID, primary key, foreign key to users) — Links to the user
- `password_hash` (String, 255) — BCrypt-hashed password
- `salt` (String, 255) — Defaults to `""`
- `hash_algorithm` (String, 255) — Defaults to `"bcrypt"`

Association: `belongs_to :users`

**emails** — Stores email addresses linked to users.

- `email_address` (String, 255, primary key) — Normalized (lowercase) email address via `Types::EmailAddress`
- `user_id` (UUID, foreign key to users) — Links to the user

Association: `belongs_to :user`

### Repositories

**Users** — `app/repos/users.rb`

Extends `Hanami::DB::Repo` and provides:

- `insert(attributes)` — Creates a user via the `users` relation's `create` command, returning the one result
- `find_by_external_id(external_id)` — Queries users by `external_id`, returning one result

**Emails** — `app/repos/emails.rb`

Extends `Hanami::DB::Repo` and provides:

- `insert(email_address, user_id)` — Inserts an email record with the given address and user_id

### Queries

[Explain query construction and optimization]
