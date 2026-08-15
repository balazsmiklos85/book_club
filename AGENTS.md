## Project Structure

```
book_club/
├── Cargo.toml
├── templates/
│   ├── layouts/
│   │   └── base.html          # full-page layout with HTMX script
│   └── books/
│       ├── index.html         # full page: layout + list
│       ├── _list.html         # fragment (HTMX target)
│       ├── new.html
│       └── _form.html
├── src/
│   ├── main.rs                # rocket::build().attach(...).manage(...)
│   ├── lib.rs                 # (optional)
│   ├── error.rs               # AppError implementing Responder/RedirectTo
│   ├── routes/                # C
│   │   ├── books.rs
│   │   └── mod.rs
│   ├── models/                # M
│   │   ├── book.rs
│   │   ├── member.rs
│   │   └── mod.rs
│   ├── repositories/          # data access
│   │   ├── book_repo.rs
│   │   └── ...
│   └── views/ (or render.rs)  # V helpers
├── migrations/ (if using SQLx/SeaORM)
└── books.db (sqlite
```
