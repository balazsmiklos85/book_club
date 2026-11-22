#![allow(clippy::missing_errors_doc)]
use crate::models::{books, users, votes};
use axum::response::Redirect;
use axum::Extension;
use loco_rs::prelude::*;
use unic_langid::LanguageIdentifier;

#[debug_handler]
pub async fn matrix(
    Extension(user): Extension<users::Model>,
    Extension(locale): Extension<LanguageIdentifier>,
    ViewEngine(v): ViewEngine<TeraView>,
    State(ctx): State<AppContext>,
) -> Result<Response> {
    if !user.is_admin {
        return Ok(Redirect::to("/").into_response());
    }

    let (all_users, all_books, all_votes) = tokio::try_join!(
        users::Entity::find().all(&ctx.db),
        books::Entity::find().all(&ctx.db),
        votes::Entity::find().all(&ctx.db),
    )?;

    let users_list: Vec<serde_json::Value> = all_users
        .into_iter()
        .map(|u| serde_json::json!({ "id": u.id, "name": u.name }))
        .collect();
    let books_list: Vec<serde_json::Value> = all_books
        .into_iter()
        .map(|b| serde_json::json!({ "id": b.id, "title": b.title }))
        .collect();
    let voted_pairs: Vec<serde_json::Value> = all_votes
        .into_iter()
        .map(|v| serde_json::json!({ "user_id": v.user_id, "book_id": v.book_id }))
        .collect();

    format::render().view(
        &v,
        "votes/matrix.html",
        serde_json::json!({
            "users": users_list,
            "books": books_list,
            "voted_pairs": voted_pairs,
            "is_admin": user.is_admin,
            "user_id": user.id,
            "lang": locale.to_string(),
        }),
    )
}

pub fn routes() -> Routes {
    Routes::new().prefix("/vote").add("/matrix", get(matrix))
}
