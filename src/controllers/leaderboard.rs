#![allow(clippy::missing_errors_doc)]
use crate::controllers::session;
use crate::models::{book_suggestions, books, users, votes};
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
    let suggestions = book_suggestions::Entity::find().all(&ctx.db).await?;
    let users = users::Entity::find().all(&ctx.db).await?;
    let user_names: HashMap<i64, String> = users.into_iter().map(|u| (u.id, u.name)).collect();
    let mut suggesters_by_book: HashMap<i64, Vec<(i64, String)>> = HashMap::new();
    for s in suggestions {
        if let Some(name) = user_names.get(&s.user_id) {
            suggesters_by_book
                .entry(s.book_id)
                .or_default()
                .push((s.user_id, name.clone()));
        } else {
            suggesters_by_book
                .entry(s.book_id)
                .or_default()
                .push((s.user_id, format!("[{}]", s.user_id)));
        }
    }
    let rows = aggregate_votes(user, books, votes, &suggesters_by_book);
    format::render().view(&v, "leaderboard.html", serde_json::json!({ "books": rows}))
}

fn aggregate_votes(
    user: crate::models::users::Model,
    books: Vec<crate::models::books::Model>,
    votes: Vec<crate::models::votes::Model>,
    suggesters_by_book: &HashMap<i64, Vec<(i64, String)>>,
) -> Vec<sea_orm::prelude::Json> {
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
                "suggesters": suggesters_by_book.get(&b.id)
                    .map(|l| l.iter().map(|(id, name)| serde_json::json!({ "id": id, "name": name })).collect::<Vec<_>>())
                    .unwrap_or_default(),
            })
        })
        .collect();
    rows
}

pub fn routes() -> Routes {
    Routes::new().add("/", get(home))
}
