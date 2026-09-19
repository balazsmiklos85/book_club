#![allow(clippy::missing_errors_doc)]
#![allow(clippy::unused_async)]
use crate::models::{book_suggestions, books, users, votes};
use axum::extract::Form;
use axum::extract::Path;
use axum::response::Redirect;
use axum::Extension;
use loco_rs::prelude::*;
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
    let book = new_book.ensure(&ctx.db, &params.url).await?;
    let suggestion = book_suggestions::ActiveModel {
        book_id: Set(book.id),
        user_id: Set(user.id),
        ..Default::default()
    };
    suggestion.suggest(&ctx.db).await?;
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
    votes::Entity::vote(&ctx.db, book_id, user.id).await?;
    Ok(Redirect::to("/").into_response())
}

#[debug_handler]
pub async fn unvote(
    Extension(user): Extension<users::Model>,
    State(ctx): State<AppContext>,
    Path(book_id): Path<i64>,
) -> Result<Response> {
    votes::Entity::unvote(&ctx.db, book_id, user.id).await?;
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
