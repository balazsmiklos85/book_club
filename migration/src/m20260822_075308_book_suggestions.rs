use loco_rs::schema::*;
use sea_orm_migration::prelude::*;

#[derive(DeriveMigrationName)]
pub struct Migration;

#[derive(DeriveIden)]
enum BookSuggestions {
    Table,
    BookId,
    UserId,
}

#[async_trait::async_trait]
impl MigrationTrait for Migration {
    async fn up(&self, m: &SchemaManager) -> Result<(), DbErr> {
        create_table(
            m,
            "book_suggestions",
            &[("id", ColType::PkAuto)],
            &[("book", "")],
        )
        .await?;
        m.create_index(
            Index::create()
                .name("idx-suggestions-book-user-unique")
                .table(BookSuggestions::Table)
                .col(BookSuggestions::BookId)
                .col(BookSuggestions::UserId)
                .unique()
                .to_owned(),
        )
        .await
    }

    async fn down(&self, m: &SchemaManager) -> Result<(), DbErr> {
        drop_table(m, "book_suggestions").await
    }
}
