# Use SQLite for Persistence

* Status: accepted
* Date: 2026-08-23

## Context

The Java original of this application serves around 20 users from Heroku, backed by Postgres. This rewrite replaces it: [the VPS](0004-host-on-a-vps.md) keeps state and compute on one machine, where a single database serves both app data and the job queue. [An earlier record](0002-use-mvc.md) already priced a managed database out of the hosting budget. What remained open was the engine on that machine: SQLite or self-hosted Postgres.

## Decision

We will use SQLite for all persistence: app data and job queue alike, in every environment of the rewrite.

### Self-Hosted Postgres

Lost on operations: it adds a second daemon to install, patch, and keep alive on the VPS, and its backups mean scheduled dumps, where backing up SQLite is copying one file.

## Consequences

### Easier

* One process to run on the VPS instead of two: no database daemon to install, patch, or monitor.
* Backups are copying one file, restoring is copying it back.
* Every environment speaks the same engine, so nothing behaves differently only in production.
* The data migration provides a possibility to fix the problematic database structure. The original database structure was created based on an old Excel file, then additional tables and columns were added on demand. Unnecessary columns were never cleaned up, some data still have multiple places to live, all of which overcomplicates the business logic. If I need to selectively copy data from the old database, then I might as well do it into the right structure.

### Harder

* Bringing the existing data over becomes a cross-engine migration, where Postgres would have needed only a dump restored on the new machine.
* Writes serialize through a single writer. Fine for around 20 users, a real ceiling if the club ever grows.
* The database answers only on its own machine: nothing remote can query it directly, everything goes through the app or an SSH session.


