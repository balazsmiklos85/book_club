use crate::models::book_suggestions;

pub use super::_entities::book_suggestions::{ActiveModel, Column, Entity, Model};
use loco_rs::model::{ModelError, ModelResult};
use sea_orm::entity::prelude::*;
pub type BookSuggestions = Entity;

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

impl Model {
    pub async fn find_by_book_and_user(
        db: &DatabaseConnection,
        book_id: i64,
        user_id: i64,
    ) -> ModelResult<Self> {
        Entity::find()
            .filter(Column::BookId.eq(book_id))
            .filter(Column::UserId.eq(user_id))
            .one(db)
            .await?
            .ok_or(ModelError::EntityNotFound)
    }
}

impl ActiveModel {
    pub async fn clean_up_by_book(db: &DatabaseConnection, book_id: i64) -> Result<(), DbErr> {
        Entity::delete_many()
            .filter(Column::BookId.eq(book_id))
            .exec(db)
            .await?;
        Ok(())
    }

    pub async fn suggest(self, db: &DatabaseConnection) -> ModelResult<Model> {
        return match self.clone().insert(db).await {
            Ok(suggestion) => Ok(suggestion),
            Err(err)
                if matches!(
                    err.sql_err(),
                    Some(SqlErr::UniqueConstraintViolation { .. })
                ) =>
            {
                book_suggestions::Model::find_by_book_and_user(
                    db,
                    self.book_id
                        .clone()
                        .take()
                        .ok_or(ModelError::EntityNotFound)?,
                    self.user_id
                        .clone()
                        .take()
                        .ok_or(ModelError::EntityNotFound)?,
                )
                .await
            }
            Err(err) => return Err(err.into()),
        };
    }
}

// implement your custom finders, selectors oriented logic here
impl Entity {}
