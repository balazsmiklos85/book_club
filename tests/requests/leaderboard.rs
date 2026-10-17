use axum::http::{HeaderName, HeaderValue};
use book_club::app::App;
use book_club::models::{book_suggestions, books, users, votes};
use loco_rs::testing::prelude::*;
use loco_rs::{app::AppContext, TestServer};
use sea_orm::ActiveModelTrait;
use sea_orm::ActiveValue::Set;
use serial_test::serial;

use crate::requests::prepare_data;

/// The gravatar hash is the MD5 of the lowercased, trimmed e-mail address, so the
/// rendered `img src` proves the controller hashed the voter's e-mail.
fn gravatar_hash(email: &str) -> String {
    format!("{:x}", md5::compute(email.trim().to_lowercase()))
}

fn cookie(token: &str) -> (HeaderName, HeaderValue) {
    (
        HeaderName::from_static("cookie"),
        HeaderValue::from_str(&format!("token={token}")).unwrap(),
    )
}

#[tokio::test]
#[serial]
async fn leaderboard_shows_a_gravatar_per_voter() {
    request::<App, _, _>(|request, ctx| async move {
        let logged_in = prepare_data::init_user_login(&request, &ctx).await;
        let voter = given_user(&ctx, "Gravatar Voter", "  Gravatar.Voter@Example.COM ").await;
        let book = given_book(&ctx, "Voted Book").await;
        given_suggestion(&ctx, book.id, voter.id).await;
        votes::ActiveModel {
            book_id: Set(book.id),
            user_id: Set(voter.id),
            ..Default::default()
        }
        .insert(&ctx.db)
        .await
        .unwrap();

        let body = when_getting_leaderboard(&request, &logged_in.token).await;

        let expected = gravatar_hash("  Gravatar.Voter@Example.COM ");
        assert!(
            body.contains(&format!("gravatar.com/avatar/{expected}")),
            "the leaderboard should render a gravatar for {expected}, got: {body}"
        );
    })
    .await;
}

#[tokio::test]
#[serial]
async fn leaderboard_marks_a_recent_book_as_new() {
    request::<App, _, _>(|request, ctx| async move {
        let logged_in = prepare_data::init_user_login(&request, &ctx).await;
        let recent = given_book_created_days_ago(&ctx, "Recent Book", 5).await;
        let old = given_book_created_days_ago(&ctx, "Old Book", 45).await;
        given_suggestion(&ctx, recent.id, logged_in.user.id).await;
        given_suggestion(&ctx, old.id, logged_in.user.id).await;

        let body = when_getting_leaderboard(&request, &logged_in.token).await;

        then_marks_as_new(&body, "Recent Book");
        then_does_not_mark_as_new(&body, "Old Book");
    })
    .await;
}

#[tokio::test]
#[serial]
async fn leaderboard_shows_the_unvote_form_for_a_voted_book() {
    request::<App, _, _>(|request, ctx| async move {
        let logged_in = prepare_data::init_user_login(&request, &ctx).await;
        let mine = given_book(&ctx, "My Book").await;
        let other = given_book(&ctx, "Other Book").await;
        given_suggestion(&ctx, mine.id, logged_in.user.id).await;
        given_suggestion(&ctx, other.id, logged_in.user.id).await;
        votes::ActiveModel {
            book_id: Set(mine.id),
            user_id: Set(logged_in.user.id),
            ..Default::default()
        }
        .insert(&ctx.db)
        .await
        .unwrap();

        let body = when_getting_leaderboard(&request, &logged_in.token).await;

        assert!(
            body.contains(&format!(r#"action="/books/{}/unvote""#, mine.id)),
            "the book the user voted for should render the unvote form, got: {body}"
        );
        assert!(
            body.contains(&format!(r#"action="/books/{}/vote""#, other.id)),
            "the book the user has not voted for should render the vote form, got: {body}"
        );
        assert!(
            !body.contains(&format!(r#"action="/books/{}/vote""#, mine.id)),
            "a voted book must not also render a vote form, got: {body}"
        );
    })
    .await;
}

#[tokio::test]
#[serial]
async fn leaderboard_shows_the_old_book_and_recommender_markup() {
    request::<App, _, _>(|request, ctx| async move {
        let logged_in =
            prepare_data::make_admin(prepare_data::init_user_login(&request, &ctx).await, &ctx)
                .await;
        let book = given_book(&ctx, "Markup Book").await;
        given_suggestion(&ctx, book.id, logged_in.user.id).await;

        let body = when_getting_leaderboard(&request, &logged_in.token).await;

        assert!(
            body.contains("🗓️Schedule new event"),
            "an admin should see the schedule button, got: {body}"
        );
        assert!(
            body.contains(&format!(
                r#"<input type="hidden" name="host_id" value="{}""#,
                logged_in.user.id
            )),
            "the suggester's schedule form should pre-select the suggester as host, got: {body}"
        );
        assert!(
            body.contains(&format!(r#"<a href="{}""#, book.url)),
            "the book link should point at the book's url, got: {body}"
        );
        assert!(
            body.contains(&format!("{}: {}", "Author", "Markup Book")),
            "the book link text should be 'author: title', got: {body}"
        );
        assert!(
            body.contains("📚Add new book"),
            "the leaderboard should offer the add-new-book action, got: {body}"
        );
        assert!(
            body.contains("📊Leaderboard"),
            "the leaderboard should include the shared menu, got: {body}"
        );
    })
    .await;
}

#[tokio::test]
#[serial]
async fn leaderboard_hides_the_schedule_button_from_a_member() {
    request::<App, _, _>(|request, ctx| async move {
        let logged_in = prepare_data::init_user_login(&request, &ctx).await;
        let book = given_book(&ctx, "Member View Book").await;
        given_suggestion(&ctx, book.id, logged_in.user.id).await;

        let body = when_getting_leaderboard(&request, &logged_in.token).await;

        assert!(
            !body.contains("🗓️Schedule new event"),
            "a non-admin should not see the per-book schedule button, got: {body}"
        );
        assert!(
            !body.contains("🔧Vote Matrix"),
            "a non-admin should not see the Vote Matrix menu link, got: {body}"
        );
    })
    .await;
}

async fn when_getting_leaderboard(request: &TestServer, token: &str) -> String {
    let (name, value) = cookie(token);
    let res = request.get("/").add_header(name, value).await;
    assert_eq!(res.status_code(), 200, "the leaderboard should be served");
    res.text()
}

fn then_marks_as_new(body: &str, title: &str) {
    let row = row_of(body, title);
    assert!(
        row.matches('🆕').count() == 2,
        "the old template wraps a new book in two 🆕 marks, got: {row}"
    );
}

fn then_does_not_mark_as_new(body: &str, title: &str) {
    let row = row_of(body, title);
    assert!(
        !row.contains('🆕'),
        "a book older than 30 days must not be marked new, got: {row}"
    );
}

/// The `<li>` of a single book, so a per-book assertion cannot be satisfied by
/// another row on the same page.
fn row_of(body: &str, title: &str) -> String {
    let start = body
        .find(&format!("{title}</a>"))
        .unwrap_or_else(|| panic!("the leaderboard should list {title}, got: {body}"));
    let open = body[..start].rfind("<li>").expect("the book should sit in an <li>");
    let end = body[start..]
        .find("</li>")
        .map(|offset| start + offset)
        .unwrap_or(body.len());
    body[open..end].to_string()
}

async fn given_user(ctx: &AppContext, name: &str, email: &str) -> users::Model {
    users::ActiveModel::create_with_password(
        &ctx.db,
        &users::RegisterParams {
            email: email.trim().to_lowercase(),
            password: "1234".to_string(),
            name: name.to_string(),
            confirm_email: email.trim().to_lowercase(),
            confirm_password: "1234".to_string(),
        },
    )
    .await
    .unwrap()
}

async fn given_book(ctx: &AppContext, title: &str) -> books::Model {
    books::ActiveModel {
        title: Set(title.to_string()),
        author: Set(Some("Author".to_string())),
        url: Set(format!("https://example.com/{}", title.replace(' ', "-").to_lowercase())),
        ..Default::default()
    }
    .insert(&ctx.db)
    .await
    .unwrap()
}

async fn given_book_created_days_ago(ctx: &AppContext, title: &str, days: i64) -> books::Model {
    let created: chrono::DateTime<chrono::Utc> = chrono::Utc::now() - chrono::Duration::days(days);
    books::ActiveModel {
        title: Set(title.to_string()),
        author: Set(Some("Author".to_string())),
        url: Set(format!("https://example.com/{}", title.replace(' ', "-").to_lowercase())),
        created_at: Set(created.into()),
        updated_at: Set(created.into()),
        ..Default::default()
    }
    .insert(&ctx.db)
    .await
    .unwrap()
}

async fn given_suggestion(ctx: &AppContext, book_id: i64, user_id: i64) {
    book_suggestions::ActiveModel {
        book_id: Set(book_id),
        user_id: Set(user_id),
        ..Default::default()
    }
    .insert(&ctx.db)
    .await
    .unwrap();
}