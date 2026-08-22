#![allow(clippy::missing_errors_doc)]
#![allow(clippy::unused_async)]
use crate::controllers::session;
use crate::models::{book_suggestions, books};
use axum::extract::Form;
use axum::response::Redirect;
use axum_extra::extract::CookieJar;
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
    cookies: CookieJar,
    State(ctx): State<AppContext>,
    Form(params): Form<CreateBookParams>,
) -> Result<Response> {
    let Some(user) = session::current_user(&cookies, &ctx).await else {
        return Ok(Redirect::to("/login").into_response());
    };
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
pub async fn new(ViewEngine(v): ViewEngine<TeraView>) -> Result<Response> {
    format::render().view(&v, "books/new.html", serde_json::json!({}))
}

pub fn routes() -> Routes {
    Routes::new()
        .prefix("books")
        .add("/", post(create))
        .add("/new", get(new))
}
