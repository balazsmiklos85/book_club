# Use MVC

* Status: accepted
* Date: 2026-08-22

## Context and Problem Statement

After I decided to do [the rewrite in Rust](0001-use-rust.md), the app needed an architecture. Its history offered three data points: the Java original was an anemic domain model in Fowler's sense; a showcase-driven Clean Architecture rewrite was started but never finished; then came a Hanami detour, which leans toward Clean Architecture as well. None of these produced a finished codebase, so the question stood again: which architecture should the Rust version use?

## Decision Drivers

1. Simplicity above all: the Java original was ~8200 lines; a fraction of that would mean real maintainability.
2. Clean Architecture felt like overkill for an app of this size.
3. In Rust, the interface-definition → implementation → mapping ceremony of layered architectures buys little.
4. Running costs should stay around the current $12–15 per month.
5. Job-interview showcase value is at most a minor concern.

## Considered Options

- Model-View-Controller
- Hexagonal
- Clean Architecture
- Serverless

## Decision Outcome

Chosen option: MVC, because it is the simplest of the viable options. Conversations with an LLM about how to structure the project led to the same verdict: for an app of this size, anything beyond MVC is ceremony without payoff. The hope is that simplicity keeps the Rust codebase at a fraction of the Java original's code size, and maintainability follows from that.

### Positive Consequences

* A small, simple codebase.
* Better maintainability as a direct result of that simplicity.

### Negative Consequences

* Weak showcase value. An MVC app screams CRUD. Nothing here is likely to excite a job interviewer. The [previous ADR](0001-use-rust.md) already concluded that the showcase motivation was an illusion though, so this hurts less than it once would have.

## Pros and Cons of the Options

### MVC

* Good, because it is the simplest architecture to choose for a web app.
* Bad, because it screams CRUD.

### Clean Architecture

* Bad, because it is overly complex with no benefits. It lives on interface definitions, which need to be implemented, just so the implementations can be used in dependency injections. And every layer has its own representation of the data that needs to be mapped every single time it crosses layer boundaries.
* Bad, because in Rust that whole mapping ceremony makes little sense to begin with.

### Hexagonal Architecture

* Bad, because it feels like Clean Architecture only slightly simplified: still plenty of interface definition → implementation → mapping.

### Serverless

* Good, because each use case becomes one AWS Lambda: simple, easy to maintain, test, and scale. Also forward-thinking enough to showcase. It was magnificent enough to nearly win.
* Bad, because there is no straightforward way to persist state: a database is required, and its cost alone would exceed the entire app's cost in any other setup.

