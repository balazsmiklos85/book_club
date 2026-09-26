use axum::http::{HeaderName, HeaderValue};
use book_club::app::App;
use book_club::controllers::events::CreateEventParams;
use book_club::models::{book_suggestions, books, events as events_model, votes};
use chrono::NaiveDate;
use loco_rs::testing::prelude::*;
use loco_rs::{app::AppContext, TestServer};
use sea_orm::{ActiveModelTrait, ActiveValue::Set};
use serial_test::serial;

use crate::requests::prepare_data;

#[tokio::test]
#[serial]
async fn can_get_events() {
    request::<App, _, _>(|request, ctx| async move {
        let user = prepare_data::init_user_login(&request, &ctx).await;
        let hosted_book = books::ActiveModel {
            title: Set("Hosted Book".to_string()),
            author: Set(Some("Hosted Author".to_string())),
            url: Set("https://example.com/hosted-book".to_string()),
            ..Default::default()
        }
        .insert(&ctx.db)
        .await
        .unwrap();
        let orphan_book = books::ActiveModel {
            title: Set("Orphan Book".to_string()),
            author: Set(Some("Orphan Author".to_string())),
            url: Set("https://example.com/orphan-book".to_string()),
            ..Default::default()
        }
        .insert(&ctx.db)
        .await
        .unwrap();
        let hosted_event = events_model::ActiveModel {
            book_id: Set(hosted_book.id),
            event_date: Set(NaiveDate::from_ymd_opt(2026, 12, 1)
                .unwrap()
                .and_hms_opt(0, 0, 0)
                .unwrap()),
            host_id: Set(Some(user.user.id)),
            ..Default::default()
        }
        .insert(&ctx.db)
        .await
        .unwrap();
        let orphan_event = events_model::ActiveModel {
            book_id: Set(orphan_book.id),
            event_date: Set(NaiveDate::from_ymd_opt(2026, 12, 2)
                .unwrap()
                .and_hms_opt(0, 0, 0)
                .unwrap()),
            host_id: Set(None),
            ..Default::default()
        }
        .insert(&ctx.db)
        .await
        .unwrap();
        let res = request
            .get("/events")
            .add_header(
                HeaderName::from_static("cookie"),
                HeaderValue::from_str(&format!("token={}", user.token)).unwrap(),
            )
            .await;
        assert_eq!(res.status_code(), 200);
        let body = res.text();
        assert!(body.contains("Hosted Book"), "missing hosted book: {body}");
        assert!(body.contains("Orphan Book"), "missing orphan book: {body}");
        assert!(
            body.contains(&format!("/events/{}", hosted_event.id)),
            "hosted book should link to its detail page: {body}"
        );
        assert!(
            body.contains(&format!("/events/{}", orphan_event.id)),
            "orphan book should link to its detail page: {body}"
        );
        assert!(
            !body.contains("by  by"),
            "double 'by' bug present in: {body}"
        );
        assert!(
            !body.contains("None"),
            "raw None leaked into output: {body}"
        );
    })
    .await;
}

#[tokio::test]
#[serial]
async fn can_get_event_details() {
    request::<App, _, _>(|request, ctx| async move {
        let host = given_logged_in_user(&request, &ctx).await;
        let event = given_hosted_event(&ctx, host.user.id).await;

        let body = when_getting_event_details(&request, &host, event.id).await;

        then_details_show_the_book(&body, "Detail Book");
        then_details_show_the_date(&body, "2026-12-01");
        then_details_show_the_host(&body, "loco");
    })
    .await;
}

async fn given_hosted_event(ctx: &AppContext, host_id: i64) -> events_model::Model {
    let book = books::ActiveModel {
        title: Set("Detail Book".to_string()),
        author: Set(Some("Detail Author".to_string())),
        url: Set("https://example.com/detail-book".to_string()),
        ..Default::default()
    }
    .insert(&ctx.db)
    .await
    .unwrap();
    events_model::ActiveModel {
        book_id: Set(book.id),
        event_date: Set(NaiveDate::from_ymd_opt(2026, 12, 1)
            .unwrap()
            .and_hms_opt(0, 0, 0)
            .unwrap()),
        host_id: Set(Some(host_id)),
        ..Default::default()
    }
    .insert(&ctx.db)
    .await
    .unwrap()
}

async fn when_getting_event_details(
    request: &TestServer,
    user: &prepare_data::LoggedInUser,
    event_id: i64,
) -> String {
    let res = request
        .get(&format!("/events/{event_id}"))
        .add_header(
            HeaderName::from_static("cookie"),
            HeaderValue::from_str(&format!("token={}", user.token)).unwrap(),
        )
        .await;
    assert_eq!(
        res.status_code(),
        200,
        "expected the event details page to be served, got: {}",
        res.text()
    );
    res.text()
}

fn then_details_show_the_book(body: &str, title: &str) {
    assert!(
        body.contains(title),
        "the details page should show the book {title}, got: {body}"
    );
}

fn then_details_show_the_date(body: &str, date: &str) {
    assert!(
        body.contains(date),
        "the details page should show the date {date}, got: {body}"
    );
}

fn then_details_show_the_host(body: &str, host: &str) {
    assert!(
        body.contains(host),
        "the details page should show the host {host}, got: {body}"
    );
}

#[tokio::test]
#[serial]
async fn can_get_create() {
    request::<App, _, _>(|request, ctx| async move {
        let user = prepare_data::init_user_login(&request, &ctx).await;
        let res = request
            .get("/events/new?book_id=1&host_id=1")
            .add_header(
                HeaderName::from_static("cookie"),
                HeaderValue::from_str(&format!("token={}", user.token)).unwrap(),
            )
            .await;
        assert_eq!(res.status_code(), 200);
    })
    .await;
}

#[tokio::test]
#[serial]
async fn can_create_event() {
    request::<App, _, _>(|request, ctx| async move {
        let user = prepare_data::init_user_login(&request, &ctx).await;
        let book = books::ActiveModel {
            title: Set("Test Book".to_string()),
            url: Set("https://example.com/test-book".to_string()),
            ..Default::default()
        }
        .insert(&ctx.db)
        .await
        .unwrap();
        let res = request
            .post("/events")
            .add_header(
                HeaderName::from_static("cookie"),
                HeaderValue::from_str(&format!("token={}", user.token)).unwrap(),
            )
            .form(&CreateEventParams {
                book_id: book.id,
                event_date: NaiveDate::from_ymd_opt(2026, 12, 1).unwrap(),
                host_id: None,
            })
            .await;
        assert_eq!(res.status_code(), 303);
    })
    .await;
}

#[tokio::test]
#[serial]
async fn event_clears_leaderboard_and_votes() {
    request::<App, _, _>(|request, ctx| async move {
        let user = given_logged_in_user(&request, &ctx).await;
        let book_id = given_book_with_suggestion_and_vote(&ctx, user.user.id).await;

        then_leaderboard_is(&request, &user, "Cleanup Book", Visibility::Visible).await;

        when_creating_event(&request, &user, book_id).await;

        then_leaderboard_is(&request, &user, "Cleanup Book", Visibility::Hidden).await;

        when_resuggesting_book(&ctx, book_id, user.user.id).await;

        then_resuggested_book_has_no_votes(&request, &user, "Cleanup Book").await;
    })
    .await;
}

async fn given_logged_in_user(
    request: &TestServer,
    ctx: &AppContext,
) -> prepare_data::LoggedInUser {
    prepare_data::init_user_login(request, ctx).await
}

async fn given_book_with_suggestion_and_vote(ctx: &AppContext, user_id: i64) -> i64 {
    let book = books::ActiveModel {
        title: Set("Cleanup Book".to_string()),
        url: Set("https://example.com/cleanup-book".to_string()),
        ..Default::default()
    }
    .insert(&ctx.db)
    .await
    .unwrap();
    book_suggestions::ActiveModel {
        book_id: Set(book.id),
        user_id: Set(user_id),
        ..Default::default()
    }
    .insert(&ctx.db)
    .await
    .unwrap();
    votes::ActiveModel {
        book_id: Set(book.id),
        user_id: Set(user_id),
        ..Default::default()
    }
    .insert(&ctx.db)
    .await
    .unwrap();
    book.id
}

async fn leaderboard_body(request: &TestServer, user: &prepare_data::LoggedInUser) -> String {
    let res = request
        .get("/")
        .add_header(
            HeaderName::from_static("cookie"),
            HeaderValue::from_str(&format!("token={}", user.token)).unwrap(),
        )
        .await;
    assert_eq!(res.status_code(), 200);
    res.text()
}

#[derive(Debug, PartialEq)]
enum Visibility {
    Visible,
    Hidden,
}

async fn then_leaderboard_is(
    request: &TestServer,
    user: &prepare_data::LoggedInUser,
    title: &str,
    visibility: Visibility,
) {
    let body = leaderboard_body(request, user).await;
    assert_eq!(
        body.contains(title),
        visibility == Visibility::Visible,
        "expected {title} to be {visibility:?} on leaderboard, got: {body}"
    );
}

async fn when_creating_event(
    request: &TestServer,
    user: &prepare_data::LoggedInUser,
    book_id: i64,
) {
    let res = request
        .post("/events")
        .add_header(
            HeaderName::from_static("cookie"),
            HeaderValue::from_str(&format!("token={}", user.token)).unwrap(),
        )
        .form(&CreateEventParams {
            book_id,
            event_date: NaiveDate::from_ymd_opt(2026, 12, 1).unwrap(),
            host_id: Some(user.user.id),
        })
        .await;
    assert_eq!(res.status_code(), 303);
}

async fn when_resuggesting_book(ctx: &AppContext, book_id: i64, user_id: i64) {
    book_suggestions::ActiveModel {
        book_id: Set(book_id),
        user_id: Set(user_id),
        ..Default::default()
    }
    .insert(&ctx.db)
    .await
    .unwrap();
}

async fn then_resuggested_book_has_no_votes(
    request: &TestServer,
    user: &prepare_data::LoggedInUser,
    title: &str,
) {
    let body = leaderboard_body(request, user).await;
    assert!(
        body.contains(title),
        "{title} should be back on leaderboard, got: {body}"
    );
    assert!(
        body.contains("0 votes"),
        "re-suggested {title} should carry no old votes, got: {body}"
    );
}
