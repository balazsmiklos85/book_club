pub use super::_entities::books::{ActiveModel, Column, Entity, Model};
use loco_rs::model::{ModelError, ModelResult};
use sea_orm::entity::prelude::*;
pub type Books = Entity;

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
    /// Inserts a new book, returning the existing one for a duplicate URL.
    ///
    /// # Errors
    ///
    /// When the database insert or lookup fails.
    pub async fn ensure(self, db: &DatabaseConnection, url: &String) -> ModelResult<Model> {
        return match self.insert(db).await {
            Ok(book) => Ok(book),
            Err(err)
                if matches!(
                    err.sql_err(),
                    Some(SqlErr::UniqueConstraintViolation { .. })
                ) =>
            {
                Entity::find_by_url(db, url).await
            }
            Err(err) => return Err(err.into()),
        };
    }
}

impl Entity {
    /// Finds a `book` by the provided URL.
    ///
    /// # Errors
    ///
    /// When could not find `book` by the given URL or database query error.
    pub async fn find_by_url(db: &DatabaseConnection, url: &String) -> ModelResult<Model> {
        Entity::find()
            .filter(Column::Url.eq(url))
            .one(db)
            .await?
            .ok_or(ModelError::EntityNotFound)
    }
}
