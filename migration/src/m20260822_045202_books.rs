use loco_rs::schema::*;
use sea_orm_migration::prelude::*;

#[derive(DeriveMigrationName)]
pub struct Migration;

#[derive(DeriveIden)]
enum Books {
    Table,
    Url,
}

#[async_trait::async_trait]
impl MigrationTrait for Migration {
    async fn up(&self, m: &SchemaManager) -> Result<(), DbErr> {
        create_table(
            m,
            "books",
            &[
                ("id", ColType::PkAuto),
                ("title", ColType::String),
                ("author", ColType::StringNull),
                ("url", ColType::String),
            ],
            &[],
        )
        .await?;

        m.create_index(
            Index::create()
                .name("idx-books-url-unique")
                .table(Books::Table)
                .col(Books::Url)
                .unique()
                .to_owned(),
        )
        .await
    }

    async fn down(&self, m: &SchemaManager) -> Result<(), DbErr> {
        drop_table(m, "books").await
    }
}
