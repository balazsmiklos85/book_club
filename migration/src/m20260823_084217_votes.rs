use loco_rs::schema::*;
use sea_orm_migration::prelude::*;

#[derive(DeriveMigrationName)]
pub struct Migration;

#[derive(DeriveIden)]
enum Votes {
    Table,
    BookId,
    UserId,
}

#[async_trait::async_trait]
impl MigrationTrait for Migration {
    async fn up(&self, m: &SchemaManager) -> Result<(), DbErr> {
        create_table(
            m,
            "votes",
            &[("id", ColType::PkAuto)],
            &[("book", ""), ("user", "")],
        )
        .await?;
        m.create_index(
            Index::create()
                .name("idx-votes-book-user-unique")
                .table(Votes::Table)
                .col(Votes::BookId)
                .col(Votes::UserId)
                .unique()
                .to_owned(),
        )
        .await
    }

    async fn down(&self, m: &SchemaManager) -> Result<(), DbErr> {
        drop_table(m, "votes").await
    }
}
