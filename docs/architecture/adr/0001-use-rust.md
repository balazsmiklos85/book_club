# Use Rust for the Book Club

* Status: accepted
* Date: 2026-08-22

## Context and Problem Statement

The book club app was originally written in Java with Spring: Java is the language I use daily at work, and this repo doubled as a showcase of my skills for job interviews. The showcase motivation proved moot. Almost nobody seemed to care about hobby projects, even before everybody and their mother used AI to churn out code. Also it wasn't a representation of skills that I would have liked people to see.

## Decision Drivers

1. Enjoyment. A hobby project should be written in a language that I like. Java does not qualify, I know it best because I use it at work.
2. No-change has real value. Staying on Java means maximal familiarity and zero migration cost, so it stays a serious contender.

## Considered Options

- Stay on Java
- Rewrite in Ruby
- Rewrite in Rust
- Other simple languages, like Crystal and Go crossed my mind, but where never seriously evaluated.

## Decision Outcome

Chosen option: "Rewrite in Rust", because:
- Rust is a language I enjoy
- It spares me Ruby's framework dead end:
    - Rails' design conflicts with how I want to build apps
    - Sinatra is too simplistic
    - Hanami keeps reinventing itself in mostly incompatible ways, with scarce docs and a small community.

### Positive Consequences

* The hobby stays fun: the codebase grows in a language I actually like.
* The unknown brings learning possibilities.

### Negative Consequences

* I am far less proficient in Rust than in Java, and know no Rust framework as well as Spring. Slower progress and more trial and error are expected.
* The Java implementation is obsolete and unmaintained. Users complain about minor issues with it, but those fixes will only be implemented in the new version, after the rewrite.

## Pros and Cons of the Options

### Stay on Java

* Good, because it is the language I know best: daily-work mastery, almost zero migration cost.
* Bad, because I don't use it for my own joy, and enjoyment is the point of a hobby project.
* Bad, because it does not fit my philosophy of the programming languages: that a programming language should either optimize for programmer experience, be simple, and make the programmer's life easy, like Ruby, Go, Python. Or it should optimize for restricting the programmer as much as possible, saving him from himself, like Rust. The middle ground is an illusion.

### Rewrite in Ruby

* Good, because Ruby is a language I like. I even started this rewrite in it first.
* Bad, because Rails dominates the ecosystem and I reject its design: `ActiveRecord` welds domain objects to the database, and monkey-patched core types make weak typing even weaker.
* Bad, because the alternatives fall short too: Sinatra is too simplistic; Hanami keeps reinventing itself with scarce docs, a small community, and zero tolerance for AI contributions.

### Rewrite in Rust

* Good, because Rust is a language I like, and its types honestly say what the data represents.
* Bad, because I am far less proficient in it than in Java. The language itself, but especially its frameworks.

