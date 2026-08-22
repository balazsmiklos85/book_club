#![allow(elided_lifetimes_in_paths)]
#![allow(clippy::wildcard_imports)]
pub use sea_orm_migration::prelude::*;
mod m20220101_000001_users;

mod m20260822_045202_books;
mod m20260822_075308_book_suggestions;
pub struct Migrator;

#[async_trait::async_trait]
impl MigratorTrait for Migrator {
    fn migrations() -> Vec<Box<dyn MigrationTrait>> {
        vec![
            Box::new(m20220101_000001_users::Migration),
            Box::new(m20260822_045202_books::Migration),
            Box::new(m20260822_075308_book_suggestions::Migration),
            // inject-above (do not remove this comment)
        ]
    }
}
