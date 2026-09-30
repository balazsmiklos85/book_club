use loco_rs::schema::*;
use sea_orm_migration::prelude::*;

#[derive(DeriveMigrationName)]
pub struct Migration;

#[derive(DeriveIden)]
enum Participants {
    Table,
    EventId,
    UserId,
}

#[async_trait::async_trait]
impl MigrationTrait for Migration {
    async fn up(&self, m: &SchemaManager) -> Result<(), DbErr> {
        create_table(
            m,
            "participants",
            &[("id", ColType::PkAuto), ("user_id", ColType::BigInteger)],
            &[("event", "")],
        )
        .await?;
        m.create_index(
            Index::create()
                .name("idx-participants-event-user-unique")
                .table(Participants::Table)
                .col(Participants::EventId)
                .col(Participants::UserId)
                .unique()
                .to_owned(),
        )
        .await
    }

    async fn down(&self, m: &SchemaManager) -> Result<(), DbErr> {
        drop_table(m, "participants").await
    }
}
