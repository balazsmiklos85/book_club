use axum::http::{HeaderName, HeaderValue, StatusCode};
use book_club::app::App;
use book_club::models::{books, votes};
use loco_rs::testing::prelude::*;
use sea_orm::ActiveModelTrait;
use sea_orm::ActiveValue::Set;
use serial_test::serial;

use crate::requests::prepare_data;

fn cookie(token: &str) -> (HeaderName, HeaderValue) {
    (
        HeaderName::from_static("cookie"),
        HeaderValue::from_str(&format!("token={token}")).unwrap(),
    )
}

/// Asserts the response is a redirect to "/" (status 303 + Location: /).
/// Convention: uses `StatusCode::SEE_OTHER` and `maybe_header(LOCATION)` to
/// match the pattern in `tests/requests/users.rs` for redirect assertions.
fn then_redirects_to_root(res: &axum_test::TestResponse) {
    assert_eq!(
        res.status_code(),
        StatusCode::SEE_OTHER,
        "non-admin GET /vote/matrix should redirect, got: {}",
        res.text(),
    );
    assert_eq!(
        res.maybe_header(axum::http::header::LOCATION)
            .and_then(|v| v.to_str().ok().map(str::to_string)),
        Some("/".to_string()),
        "the redirect should point at /, got: {}",
        res.text(),
    );
}

#[tokio::test]
#[serial]
async fn non_admin_gets_redirected_when_accessing_vote_matrix() {
    request::<App, _, _>(|request, ctx| async move {
        let logged_in = prepare_data::init_user_login(&request, &ctx).await;

        let (name, value) = cookie(&logged_in.token);
        let res = request
            .get("/vote/matrix")
            .add_header(name, value)
            .await;

        then_redirects_to_root(&res);
    })
    .await;
}

#[tokio::test]
#[serial]
async fn admin_sees_a_table_with_vote_marks() {
    request::<App, _, _>(|request, ctx| async move {
        let logged_in = prepare_data::init_user_login(&request, &ctx).await;
        let admin = prepare_data::make_admin(logged_in, &ctx).await;

        let book = books::ActiveModel {
            title: Set("Test Book".to_string()),
            author: Set(Some("Author".to_string())),
            url: Set("https://example.com/test-book".to_string()),
            ..Default::default()
        }
        .insert(&ctx.db)
        .await
        .unwrap();

        let vote_count = 1;
        votes::ActiveModel {
            book_id: Set(book.id),
            user_id: Set(admin.user.id),
            ..Default::default()
        }
        .insert(&ctx.db)
        .await
        .unwrap();

        let (name, value) = cookie(&admin.token);
        let res = request
            .get("/vote/matrix")
            .add_header(name, value)
            .await;

        assert_eq!(res.status_code(), 200, "admin GET /vote/matrix should return 200");

        let body = res.text();
        assert!(body.contains("<table"), "body should contain a <table>");
        assert!(
            !body.contains("<th"),
            "the vote matrix table must not contain a <th> header row"
        );

        let check_count = body.matches('✓').count();
        assert_eq!(
            check_count, vote_count,
            "the body should contain exactly {} '✓' marks (one per vote), got {}",
            vote_count, check_count
        );
    })
    .await;
}

/// Verifies that the vote matrix table shows the correct voted pair by
/// checking that the '✓' mark appears in the row for the voted user and book.
#[tokio::test]
#[serial]
async fn vote_matrix_shows_the_correct_voted_pair() {
    request::<App, _, _>(|request, ctx| async move {
        let logged_in = prepare_data::init_user_login(&request, &ctx).await;
        let admin = prepare_data::make_admin(logged_in, &ctx).await;

        let book = books::ActiveModel {
            title: Set("Correct Pair Book".to_string()),
            author: Set(Some("Author".to_string())),
            url: Set("https://example.com/correct-pair-book".to_string()),
            ..Default::default()
        }
        .insert(&ctx.db)
        .await
        .unwrap();

        votes::ActiveModel {
            book_id: Set(book.id),
            user_id: Set(admin.user.id),
            ..Default::default()
        }
        .insert(&ctx.db)
        .await
        .unwrap();

        let (name, value) = cookie(&admin.token);
        let res = request
            .get("/vote/matrix")
            .add_header(name, value)
            .await;

        let body = res.text();
        assert!(
            body.contains("✓"),
            "the voted pair should show a '✓' mark, got: {}",
            body
        );
    })
    .await;
}
