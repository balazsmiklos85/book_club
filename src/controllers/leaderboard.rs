#![allow(clippy::missing_errors_doc)]
use crate::controllers::session;
use crate::models::{books, votes};
use axum::response::Redirect;
use axum_extra::extract::CookieJar;
use loco_rs::prelude::*;
use std::collections::HashMap;
use std::collections::HashSet;

pub async fn home(
    cookies: CookieJar,
    ViewEngine(v): ViewEngine<TeraView>,
    State(ctx): State<AppContext>,
) -> Result<Response> {
    let Some(user) = session::current_user(&cookies, &ctx).await else {
        return Ok(Redirect::to("/login").into_response());
    };
    let books = books::Entity::find().all(&ctx.db).await?;
    let votes = votes::Entity::find().all(&ctx.db).await?;
    let mut votes_on_books: HashMap<i64, i64> = HashMap::new();
    for v in &votes {
        *votes_on_books.entry(v.book_id).or_default() += 1;
    }
    let user_votes: HashSet<i64> = votes
        .iter()
        .filter(|v| v.user_id == user.id)
        .map(|v| v.book_id)
        .collect();
    let rows: Vec<serde_json::Value> = books
        .into_iter()
        .map(|b| {
            serde_json::json!({
                "id": b.id,
                "title": b.title,
                "author": b.author,
                "votes": votes_on_books.get(&b.id).copied().unwrap_or(0),
                "voted": user_votes.contains(&b.id),
            })
        })
        .collect();
    format::render().view(&v, "leaderboard.html", serde_json::json!({ "books": rows}))
}

pub fn routes() -> Routes {
    Routes::new().add("/", get(home))
}
