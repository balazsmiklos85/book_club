#![allow(clippy::missing_errors_doc)]
#![allow(clippy::unused_async)]
use crate::models::{book_suggestions, books, users, votes};
use axum::extract::Form;
use axum::extract::Path;
use axum::response::Redirect;
use axum::Extension;
use loco_rs::prelude::*;
use sea_orm::SqlErr;
use serde::Deserialize;

#[derive(Deserialize)]
pub struct CreateBookParams {
    title: String,
    author: Option<String>,
    url: String,
}

#[debug_handler]
pub async fn create(
    Extension(user): Extension<users::Model>,
    State(ctx): State<AppContext>,
    Form(params): Form<CreateBookParams>,
) -> Result<Response> {
    let author = params.author.filter(|a| !a.trim().is_empty());
    let new_book = books::ActiveModel {
        title: Set(params.title),
        author: Set(author),
        url: Set(params.url.clone()),
        ..Default::default()
    };
    let book = match new_book.insert(&ctx.db).await {
        Ok(book) => book,
        Err(err)
            if matches!(
                err.sql_err(),
                Some(SqlErr::UniqueConstraintViolation { .. })
            ) =>
        {
            books::Entity::find()
                .filter(books::Column::Url.eq(params.url))
                .one(&ctx.db)
                .await?
                .ok_or_else(|| Error::string("unique url but no book found"))?
        }
        Err(err) => return Err(err.into()),
    };
    let suggestion = book_suggestions::ActiveModel {
        book_id: Set(book.id),
        user_id: Set(user.id),
        ..Default::default()
    };
    if let Err(err) = suggestion.insert(&ctx.db).await {
        if !matches!(
            err.sql_err(),
            Some(SqlErr::UniqueConstraintViolation { .. })
        ) {
            return Err(err.into());
        }
    }

    Ok(Redirect::to("/").into_response())
}

#[debug_handler]
pub async fn new(
    Extension(_user): Extension<users::Model>,
    ViewEngine(v): ViewEngine<TeraView>,
) -> Result<Response> {
    format::render().view(&v, "books/new.html", serde_json::json!({}))
}

#[debug_handler]
pub async fn vote(
    Extension(user): Extension<users::Model>,
    State(ctx): State<AppContext>,
    Path(book_id): Path<i64>,
) -> Result<Response> {
    votes::ActiveModel::vote(&ctx.db, book_id, user.id).await?;
    Ok(Redirect::to("/").into_response())
}

#[debug_handler]
pub async fn unvote(
    Extension(user): Extension<users::Model>,
    State(ctx): State<AppContext>,
    Path(book_id): Path<i64>,
) -> Result<Response> {
    votes::ActiveModel::unvote(&ctx.db, book_id, user.id).await?;
    Ok(Redirect::to("/").into_response())
}

pub fn routes() -> Routes {
    Routes::new()
        .prefix("books")
        .add("/", post(create))
        .add("/{id}/vote", post(vote))
        .add("/{id}/unvote", post(unvote))
        .add("/new", get(new))
}
