pub use super::_entities::votes::{ActiveModel, Column, Entity, Model};
use sea_orm::entity::prelude::*;
pub type Votes = Entity;

#[async_trait::async_trait]
impl ActiveModelBehavior for ActiveModel {
    async fn before_save<C>(self, _db: &C, insert: bool) -> std::result::Result<Self, DbErr>
    where
        C: ConnectionTrait,
    {
        if !insert && self.updated_at.is_unchanged() {
            let mut this = self;
            this.updated_at = sea_orm::ActiveValue::Set(chrono::Utc::now().into());
            Ok(this)
        } else {
            Ok(self)
        }
    }
}

impl Model {}

impl ActiveModel {
    pub async fn vote(db: &DatabaseConnection, book_id: i64, user_id: i64) -> loco_rs::Result<()> {
        let vote = ActiveModel {
            book_id: sea_orm::ActiveValue::Set(book_id),
            user_id: sea_orm::ActiveValue::Set(user_id),
            ..Default::default()
        };
        match vote.insert(db).await {
            Err(err)
                if matches!(
                    err.sql_err(),
                    Some(SqlErr::UniqueConstraintViolation { .. })
                ) =>
            {
                Ok(())
            }
            other => Ok(other.map(|_| ())?),
        }
    }

    pub async fn unvote(
        db: &DatabaseConnection,
        book_id: i64,
        user_id: i64,
    ) -> loco_rs::Result<()> {
        Entity::delete_many()
            .filter(Column::BookId.eq(book_id))
            .filter(Column::UserId.eq(user_id))
            .exec(db)
            .await?;
        Ok(())
    }
}

impl Entity {}
