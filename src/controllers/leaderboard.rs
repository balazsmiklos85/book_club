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

#[cfg(test)]
mod tests {
    use super::*;

    // TODO: move to the user model module
    fn test_user(id: i64, name: &str) -> users::Model {
        users::Model {
            id,
            pid: uuid::Uuid::new_v4(),
            email: format!("user{id}@example.com"),
            password: "password".to_string(),
            api_key: format!("api-key-{id}"),
            name: name.to_string(),
            reset_token: None,
            reset_sent_at: None,
            email_verification_token: None,
            email_verification_sent_at: None,
            email_verified_at: None,
            magic_link_token: None,
            magic_link_expiration: None,
            created_at: chrono::Utc::now().into(),
            updated_at: chrono::Utc::now().into(),
        }
    }

    // TODO: move to the book model module
    fn test_book(id: i64) -> books::Model {
        books::Model {
            id,
            title: format!("Book {id}"),
            author: Some("Author".to_string()),
            url: format!("https://example.com/{id}"),
            created_at: chrono::Utc::now().into(),
            updated_at: chrono::Utc::now().into(),
        }
    }

    // TODO: move to the vote model module
    fn test_vote(book_id: i64, user_id: i64) -> votes::Model {
        votes::Model {
            id: 0,
            book_id,
            user_id,
            created_at: chrono::Utc::now().into(),
            updated_at: chrono::Utc::now().into(),
        }
    }

    // TODO: move to the suggestion model module
    fn test_suggestion(book_id: i64, user_id: i64) -> book_suggestions::Model {
        book_suggestions::Model {
            id: 0,
            book_id,
            user_id,
            created_at: chrono::Utc::now().into(),
            updated_at: chrono::Utc::now().into(),
        }
    }

    #[test]
    fn aggregates_vote_counts_voted_flag_and_suggesters() {
        let alice = test_user(1, "Alice");
        let bob = test_user(2, "Bob");

        let rows = aggregate_votes(
            alice.clone(),
            vec![test_book(10), test_book(20)],
            vec![test_vote(10, 1), test_vote(10, 2), test_vote(20, 2)],
            vec![alice.clone(), bob.clone()],
            vec![test_suggestion(10, 1), test_suggestion(20, 2)],
        );

        assert_eq!(rows[0]["votes"], serde_json::json!(2));
        assert_eq!(rows[1]["votes"], serde_json::json!(1));
        assert_eq!(rows[0]["voted"], serde_json::json!(true));
        assert_eq!(rows[1]["voted"], serde_json::json!(false));
        assert_eq!(
            rows[0]["suggesters"],
            serde_json::json!([{ "id": 1, "name": "Alice" }])
        );
        assert_eq!(
            rows[1]["suggesters"],
            serde_json::json!([{ "id": 2, "name": "Bob" }])
        );
    }
}
