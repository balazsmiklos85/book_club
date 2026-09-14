#![allow(clippy::missing_errors_doc)]
use crate::models::{book_suggestions, books, users, votes};
use axum::Extension;
use loco_rs::prelude::*;
use std::collections::HashMap;
use std::collections::HashSet;

pub async fn home(
    Extension(user): Extension<users::Model>,
    ViewEngine(v): ViewEngine<TeraView>,
    State(ctx): State<AppContext>,
) -> Result<Response> {
    let (books, votes, suggestions, users) = tokio::try_join!(
        books::Entity::find().all(&ctx.db),
        votes::Entity::find().all(&ctx.db),
        book_suggestions::Entity::find().all(&ctx.db),
        users::Entity::find().all(&ctx.db)
    )?;
    let rows = aggregate_votes(user, books, votes, users, suggestions);
    format::render().view(&v, "leaderboard.html", serde_json::json!({ "books": rows}))
}

fn aggregate_suggestions(
    users: Vec<crate::models::users::Model>,
    suggestions: Vec<crate::models::book_suggestions::Model>,
) -> HashMap<i64, Vec<(i64, String)>> {
    let user_names: HashMap<i64, String> = users.into_iter().map(|u| (u.id, u.name)).collect();
    suggestions.into_iter().fold(HashMap::new(), |mut acc, s| {
        let display_name = user_names
            .get(&s.user_id)
            .map_or_else(|| format!("[{}]", s.user_id), |name| name.clone());
        acc.entry(s.book_id)
            .or_default()
            .push((s.user_id, display_name));
        acc
    })
}

fn aggregate_votes(
    user: crate::models::users::Model,
    books: Vec<books::Model>,
    votes: Vec<votes::Model>,
    users: Vec<users::Model>,
    suggestions: Vec<book_suggestions::Model>,
) -> Vec<sea_orm::prelude::Json> {
    let suggesters_by_book = aggregate_suggestions(users, suggestions);
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
