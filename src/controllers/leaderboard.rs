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
    let mut rows: Vec<serde_json::Value> = books
        .into_iter()
        .filter(|b| suggesters_by_book.contains_key(&b.id))
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
    rows.sort_by_key(|r| std::cmp::Reverse(r["votes"].as_i64().unwrap_or(0)));
    rows
}

pub fn routes() -> Routes {
    Routes::new().add("/", get(home))
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn should_show_books_only_with_suggestions() {
        let given_user_alice = test_user(1, "Alice");

        let rows = aggregate_votes(
            given_user_alice.clone(),
            vec![test_book(10), test_book(20)],
            vec![test_vote(20, 1)],
            vec![given_user_alice.clone()],
            vec![test_suggestion(10, 1)],
        );

        then_book_lists_suggesters(&rows, "Book 10", &["Alice"]);
        then_book_is_hidden(&rows, "Book 20");
    }

    #[test]
    fn should_sort_books_by_votes() {
        let given_user_alice = test_user(1, "Alice");
        let given_user_bob = test_user(2, "Bob");

        let rows = aggregate_votes(
            given_user_alice.clone(),
            vec![test_book(20), test_book(10)],
            vec![test_vote(10, 1), test_vote(10, 2), test_vote(20, 2)],
            vec![given_user_alice.clone(), given_user_bob.clone()],
            vec![test_suggestion(10, 1), test_suggestion(20, 2)],
        );

        then_books_are_ordered_by_most_votes(&rows);
        then_book_shows_votes(&rows, "Book 10", 2);
        then_book_shows_votes(&rows, "Book 20", 1);
        then_alice_voted_for(&rows, "Book 10");
        then_alice_can_still_vote_for(&rows, "Book 20");
        then_book_lists_suggesters(&rows, "Book 10", &["Alice"]);
        then_book_lists_suggesters(&rows, "Book 20", &["Bob"]);
    }

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

    fn find_book<'a>(rows: &'a [serde_json::Value], title: &str) -> &'a serde_json::Value {
        rows.iter()
            .find(|r| r["title"] == title)
            .expect("book should appear on leaderboard")
    }

    fn then_books_are_ordered_by_most_votes(rows: &[serde_json::Value]) {
        let counts: Vec<i64> = rows
            .iter()
            .map(|r| r["votes"].as_i64().unwrap_or(-1))
            .collect();
        assert_eq!(
            rows[0]["title"],
            serde_json::json!("Book 10"),
            "Book 10 got most votes so it should come first"
        );
        assert!(
            counts.windows(2).all(|w| w[0] >= w[1]),
            "most voted book should come first, got counts {counts:?}"
        );
    }

    fn then_book_shows_votes(rows: &[serde_json::Value], title: &str, expected: i64) {
        assert_eq!(
            find_book(rows, title)["votes"],
            serde_json::json!(expected),
            "{title} got {expected} votes so it should show {expected}"
        );
    }

    fn then_alice_voted_for(rows: &[serde_json::Value], title: &str) {
        assert_eq!(
            find_book(rows, title)["voted"],
            serde_json::json!(true),
            "Alice voted for {title} so it should show voted"
        );
    }

    fn then_alice_can_still_vote_for(rows: &[serde_json::Value], title: &str) {
        assert_eq!(
            find_book(rows, title)["voted"],
            serde_json::json!(false),
            "Alice did not vote for {title} yet so she can still vote"
        );
    }

    fn then_book_lists_suggesters(rows: &[serde_json::Value], title: &str, expected: &[&str]) {
        let names: Vec<String> = find_book(rows, title)["suggesters"]
            .as_array()
            .cloned()
            .unwrap_or_default()
            .iter()
            .map(|s| s["name"].as_str().unwrap_or("").to_string())
            .collect();
        assert_eq!(
            names, expected,
            "{title} was suggested by {expected:?} so it should list them"
        );
    }

    fn then_book_is_hidden(rows: &[serde_json::Value], title: &str) {
        assert!(
            rows.iter().all(|r| r["title"] != title),
            "{title} has no active suggestions so it should stay hidden, got {rows:?}"
        );
    }
}
