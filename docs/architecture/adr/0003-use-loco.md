# Use Loco

* Status: accepted
* Date: 2026-08-22

## Context

After choosing [Rust](0001-use-rust.md), the rewrite needed a framework. My frame of reference is Spring: I think in Controllers, Services, Repositories: concepts that give an app its internal boundaries. A previous rewrite attempt in Ruby/Hanami had similar structure but died with its ecosystem. Rust's web ecosystem spans from single-purpose libraries like Axum, Actix Web, or Rocket, to full application frameworks, like Loco.

## Decision

We will build the app on Loco.

Loco supplies what Axum alone lacks for me: organizing concepts, like controllers, models, workers, tasks, per-environment configuration, that give an app internal boundaries, the structure that resembles what I know from Spring. It sits on Axum and SeaORM, so nothing essential is hidden: moving to bare Axum later remains a gradual swap rather than a rewrite.

The alternatives lost early:

### Axum

An excellent HTTP layer, but bare-bones. The missing piece wasn't willingness to hand-roll a database pool or auth. It was the concepts that tell me where things belong.

### Actix Web

My only exposure is a meetup talk years ago, in Rust's early days, built on its actor model, which never made sense to me as a model of the web. I never revisited it.

### Rocket

Would have been my choice: Sinatra-like, but with easy templating. But its last release is 0.5.1 from May 2024. Development appears stalled, which rules it out for a long-lived app.

### Loco

Loco calls itself "the Rails of Rust", and [the previous ADR](0001-use-rust.md) rejected Rails' design. That rejection targeted ActiveRecord welding domain objects to the database and monkey-patched core types. Neither seems to exist here. Entities are generated code kept apart from hand-written model logic, and Rust's type system leaves no room for monkey-patching. What I'm adopting is convention-over-configuration, not ActiveRecord.

## Consequences

### Easier

* Spring-like boundaries from line one: controllers, models, workers, tasks, per-environment config come prewired instead of hand-assembled.
* Generators scaffold models, controllers, and migrations and wire them into `src/app.rs`.
* Thin documentation hurts less than it does with other frameworks like Hanami: LLMs handle Loco well and answer most questions without problems.

### Harder

* The abstraction leaks. I need to know Axum anyway to some extent to get real work done.
* Documentation is nowhere near Spring's depth.
* The generators are opinionated toward API development: web views aren't generated even when asked for, so the server-rendered side stays manual work.

