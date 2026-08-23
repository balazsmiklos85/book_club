# Agent Guide for This Loco App

This is a **Loco** (loco.rs) application — an all-in-one, batteries-included Rust web framework. Routing, the database (Sea-ORM), background jobs, a scheduler, mailers, tasks, storage, caching, and testing are already integrated. **Prefer Loco's built-in solutions and generators over adding external crates or wiring infrastructure by hand.**

## Where Things Live

```
.
├── src/
│   ├── app.rs                  # impl Hooks for App — registers routes/workers/tasks (the wiring hub)
│   ├── controllers/            # HTTP handlers grouped into Routes (auth, session, books, leaderboard)
│   ├── views/                  # server-side view/rendering logic
│   ├── dtos/                   # data transfer objects shared between layers
│   ├── data/                   # static in-app data
│   ├── models/
│   │   ├── _entities/          # GENERATED Sea-ORM entities — do not hand-edit
│   │   └── *.rs                # your model logic (users, books, book_suggestions)
│   ├── workers/                # background jobs (e.g. downloader)
│   ├── tasks/                  # CLI/admin tasks (`cargo loco task <name>`)
│   ├── mailers/                # mailers + Tera templates (.t) per mail
│   ├── initializers/           # custom app initializers (e.g. view engine)
│   ├── fixtures/               # YAML test fixtures
│   └── bin/                    # additional binaries
├── assets/
│   ├── views/                  # Tera HTML templates (auth, books, home, leaderboard)
│   ├── static/                 # static files served as-is
│   └── i18n/                   # Fluent translations (en-US, de-DE)
├── config/                     # per-environment YAML via LOCO_ENV: development / test / production
├── migration/                  # Sea-ORM migrations, one file per migration
├── tests/                      # request/model/task/view/worker tests + insta snapshots
├── frontend/                   # frontend sources incl. generated type bindings
└── docs/                       # project documentation — see below
```

### Documentation

Where to look things up before guessing:

```
docs/
├── architecture/
│   ├── overview.md             # big-picture architecture of the app
│   └── adr/                    # architecture decision records (why Rust, MVC, Loco)
├── components/                 # one doc per layer: routes, controllers, database,
│                               # business logic, views, supporting components
├── code/                       # class interactions, process workflows, testing strategy
└── context/                    # system context, dev setup, deployment
```

Framework-level answers live outside the repo:

- Framework agent guide: https://loco.rs/AGENTS.md
- Full single-file reference: https://loco.rs/llms-full.txt
- Docs: https://loco.rs/docs

## How to Work in This App

- **Add features with generators**, then edit: `cargo loco generate model|scaffold|controller|worker|task|mailer|migration ...`. The generators also wire new code into `src/app.rs`.
- **Everything uses `AppContext` (`ctx`)**: `ctx.db`, `ctx.config`, `ctx.mailer`, `ctx.storage`, `ctx.cache`, `ctx.queue_provider`. Don't create your own database pool, server, or job queue.
- Start every controller/model/worker/task with `use loco_rs::prelude::*;`.
- App code returns `loco_rs::Result<T>` and uses `?`.
- Configuration is YAML in `config/`; secrets come from the environment via the `get_env` Tera helper inside the YAML.
- Primary/foreign keys are `i64` (this is Loco 0.17+).
- Tests: `request::<App, _, _>(|request, ctx| async move { ... }).await;`.

## Useful Commands

```
cargo loco start            # run the app
cargo loco db migrate       # apply migrations
cargo loco routes           # list routes
cargo loco task <name>      # run a task
cargo loco doctor           # check the environment
```

