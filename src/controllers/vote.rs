#![allow(clippy::missing_errors_doc)]
use std::collections::{HashMap, HashSet};

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
    let user_votes: HashMap<i64, HashSet<i64>> =
        all_votes.iter().fold(HashMap::new(), |mut acc, vote| {
            acc.entry(vote.user_id).or_default().insert(vote.book_id);
            acc
        });
    format::render().view(
        &v,
        "votes/matrix.html",
        serde_json::json!({
            "users": all_users,
            "books": all_books,
            "user_votes": user_votes,
            "is_admin": user.is_admin,
            "user_id": user.id,
            "lang": locale.to_string(),
        }),
    )
}

pub fn routes() -> Routes {
    Routes::new().prefix("/vote").add("/matrix", get(matrix))
}
