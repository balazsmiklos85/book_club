use axum::http::{HeaderName, HeaderValue, StatusCode};
use book_club::app::App;
use book_club::models::users;
use loco_rs::testing::prelude::*;
use loco_rs::{app::AppContext, TestServer};
use sea_orm::EntityTrait;
use serial_test::serial;

use crate::requests::prepare_data;

const USER_EMAIL: &str = "test@loco.com";
const OLD_PASSWORD: &str = "1234";
const NEW_PASSWORD: &str = "new-password";
const RESET_PASSWORD: &str = "12345678";

#[tokio::test]
#[serial]
async fn profile_shows_the_id_name_email_and_change_password_form() {
    request::<App, _, _>(|request, ctx| async move {
        let logged_in = prepare_data::init_user_login(&request, &ctx).await;

        let body = when_getting_profile(&request, &logged_in, logged_in.user.id).await;

        assert!(
            body.contains(&format!("<span>{}</span>", logged_in.user.id)),
            "the profile should render the user id, got: {body}"
        );
        assert!(
            body.contains("<span>loco</span>"),
            "the profile should render the user's name, got: {body}"
        );
        assert!(
            body.contains(&format!("<span>{USER_EMAIL}</span>")),
            "the profile should render the user's e-mail address, got: {body}"
        );
        assert!(
            body.contains(&format!(r#"action="/users/{}/password""#, logged_in.user.id)),
            "the profile should render the change-password form, got: {body}"
        );
        for field in ["old_password", "password", "confirm_password"] {
            assert!(
                body.contains(&format!(r#"name="{field}""#)),
                "the change-password form should have a {field} input, got: {body}"
            );
        }
        assert!(
            body.contains("🔑Change"),
            "the change-password form should carry the old change button, got: {body}"
        );
        assert!(
            body.contains("📊Leaderboard"),
            "the profile should include the shared menu, got: {body}"
        );
    })
    .await;
}

#[tokio::test]
#[serial]
async fn profile_of_another_user_redirects_to_the_user_list() {
    request::<App, _, _>(|request, ctx| async move {
        let logged_in = prepare_data::init_user_login(&request, &ctx).await;
        let other = given_user(&ctx, "Other Reader").await;

        let (name, value) = cookie(&logged_in.token);
        let res = request
            .get(&format!("/users/{}", other.id))
            .add_header(name, value)
            .await;

        assert_eq!(
            res.status_code(),
            StatusCode::SEE_OTHER,
            "asking for someone else's profile should redirect, got: {}",
            res.text()
        );
        assert_eq!(
            res.maybe_header(axum::http::header::LOCATION)
                .and_then(|v| v.to_str().ok().map(str::to_string)),
            Some("/users".to_string()),
            "the redirect should point at the user list, got: {}",
            res.text()
        );
    })
    .await;
}

#[tokio::test]
#[serial]
async fn change_password_rejects_a_wrong_old_password() {
    request::<App, _, _>(|request, ctx| async move {
        let logged_in = prepare_data::init_user_login(&request, &ctx).await;

        let body = when_changing_password(
            &request,
            &logged_in,
            "not-the-old-password",
            NEW_PASSWORD,
            NEW_PASSWORD,
            StatusCode::OK,
        )
        .await;

        assert!(
            body.contains("Invalid old password"),
            "the profile should report the wrong old password, got: {body}"
        );
        then_password_is(&ctx, OLD_PASSWORD).await;
    })
    .await;
}

#[tokio::test]
#[serial]
async fn change_password_rejects_a_mismatched_confirmation() {
    request::<App, _, _>(|request, ctx| async move {
        let logged_in = prepare_data::init_user_login(&request, &ctx).await;

        let body = when_changing_password(
            &request,
            &logged_in,
            OLD_PASSWORD,
            NEW_PASSWORD,
            "something-else",
            StatusCode::OK,
        )
        .await;

        assert!(
            body.contains("Password and confirmation do not match"),
            "the profile should report the mismatched confirmation, got: {body}"
        );
        then_password_is(&ctx, OLD_PASSWORD).await;
    })
    .await;
}

#[tokio::test]
#[serial]
async fn change_password_stores_the_new_password() {
    request::<App, _, _>(|request, ctx| async move {
        let logged_in = prepare_data::init_user_login(&request, &ctx).await;

        let _body = when_changing_password(
            &request,
            &logged_in,
            OLD_PASSWORD,
            NEW_PASSWORD,
            NEW_PASSWORD,
            StatusCode::SEE_OTHER,
        )
        .await;

        then_password_is(&ctx, NEW_PASSWORD).await;
        assert!(
            !password_matches(&ctx, OLD_PASSWORD).await,
            "the old password must stop working after a change",
        );
    })
    .await;
}

/// Whether the password the test registered with, or the one it last set, still
/// opens the account. The stored value is a hash, so this is the only way to
/// assert what a user would experience at the login form.
async fn password_matches(ctx: &AppContext, password: &str) -> bool {
    users::Entity::find_by_email(&ctx.db, USER_EMAIL)
        .await
        .unwrap()
        .verify_password(password)
}

async fn then_password_is(ctx: &AppContext, password: &str) {
    assert!(
        password_matches(ctx, password).await,
        "the stored password should be {password}",
    );
}

async fn given_user(ctx: &AppContext, name: &str) -> users::Model {
    let email = format!("{}@example.com", name.to_lowercase().replace(' ', "-"));
    users::ActiveModel::create_with_password(
        &ctx.db,
        &users::RegisterParams {
            email: email.clone(),
            password: OLD_PASSWORD.to_string(),
            name: name.to_string(),
            confirm_email: email.clone(),
            confirm_password: OLD_PASSWORD.to_string(),
        },
    )
    .await
    .unwrap()
}

fn cookie(token: &str) -> (HeaderName, HeaderValue) {
    (
        HeaderName::from_static("cookie"),
        HeaderValue::from_str(&format!("token={token}")).unwrap(),
    )
}

async fn when_getting_profile(
    request: &TestServer,
    user: &prepare_data::LoggedInUser,
    id: i64,
) -> String {
    let (name, value) = cookie(&user.token);
    let res = request
        .get(&format!("/users/{id}"))
        .add_header(name, value)
        .await;
    assert_eq!(
        res.status_code(),
        StatusCode::OK,
        "the profile page should be served, got: {}",
        res.text()
    );
    res.text()
}

async fn when_changing_password(
    request: &TestServer,
    user: &prepare_data::LoggedInUser,
    old_password: &str,
    new_password: &str,
    confirm_password: &str,
    expected: StatusCode,
) -> String {
    let (name, value) = cookie(&user.token);
    let res = request
        .post(&format!("/users/{}/password", user.user.id))
        .add_header(name, value)
        .form(&serde_json::json!({
            "old_password": old_password,
            "password": new_password,
            "confirm_password": confirm_password,
        }))
        .await;
    assert_eq!(
        res.status_code(),
        expected,
        "unexpected change-password response, got: {}",
        res.text()
    );
    res.text()
}

// ── Admin user-list tests ──────────────────────────────────────────────────

#[tokio::test]
#[serial]
async fn admin_user_list_shows_all_users_and_password_reset_buttons() {
    request::<App, _, _>(|request, ctx| async move {
        let admin = prepare_data::init_user_login(&request, &ctx).await;
        let admin = prepare_data::make_admin(admin, &ctx).await;
        let _other = given_user(&ctx, "Other Reader").await;

        let body = when_getting_user_list(&request, &admin).await;

        // body should contain the names and emails of every user
        assert!(
            body.contains("loco"),
            "the user list should contain the admin's name, got: {body}",
        );
        assert!(
            body.contains(USER_EMAIL),
            "the user list should contain the admin's e-mail, got: {body}",
        );
        assert!(
            body.contains("other-reader"),
            "the user list should contain the other user's name, got: {body}",
        );
        assert!(
            body.contains("@example.com"),
            "the user list should contain the other user's e-mail, got: {body}",
        );
        // password-reset button should be visible
        assert!(
            body.contains("🔓"),
            "the user list should contain the password-reset button, got: {body}",
        );
        assert!(
            body.contains("Password reset"),
            "the user list should contain the password-reset label, got: {body}",
        );
    })
    .await;
}

#[tokio::test]
#[serial]
async fn non_admin_gets_redirected_when_accessing_user_list() {
    request::<App, _, _>(|request, ctx| async move {
        let logged_in = prepare_data::init_user_login(&request, &ctx).await;

        let (name, value) = cookie(&logged_in.token);
        let res = request
            .get("/users")
            .add_header(name, value)
            .await;

        assert_eq!(
            res.status_code(),
            StatusCode::SEE_OTHER,
            "non-admin GET /users should redirect, got: {}",
            res.text(),
        );
        assert_eq!(
            res.maybe_header(axum::http::header::LOCATION)
                .and_then(|v| v.to_str().ok().map(str::to_string)),
            Some("/".to_string()),
            "the redirect should point at /, got: {}",
            res.text(),
        );
    })
    .await;
}

#[tokio::test]
#[serial]
async fn admin_reset_password_sets_the_new_password() {
    request::<App, _, _>(|request, ctx| async move {
        let admin = prepare_data::init_user_login(&request, &ctx).await;
        let admin = prepare_data::make_admin(admin, &ctx).await;
        let target = given_user(&ctx, "Reset Target").await;

        let (name, value) = cookie(&admin.token);
        let res = request
            .post(&format!("/users/{}/password-reset", target.id))
            .add_header(name, value)
            .await;

        assert_eq!(
            res.status_code(),
            StatusCode::SEE_OTHER,
            "admin POST /users/:id/password-reset should redirect, got: {}",
            res.text(),
        );

        // fetch the target user fresh and verify its password directly
        let fresh_target = users::Entity::find_by_id(target.id)
            .one(&ctx.db)
            .await
            .unwrap()
            .unwrap();

        // old password should no longer work
        assert!(
            !fresh_target.verify_password(OLD_PASSWORD),
            "the old password should no longer work after reset",
        );
        // new literal password should work
        assert!(
            fresh_target.verify_password(RESET_PASSWORD),
            "the new password should work after reset",
        );
    })
    .await;
}

#[tokio::test]
#[serial]
async fn non_admin_cannot_reset_password() {
    request::<App, _, _>(|request, ctx| async move {
        let logged_in = prepare_data::init_user_login(&request, &ctx).await;
        let target = given_user(&ctx, "Another Target").await;

        let (name, value) = cookie(&logged_in.token);
        let res = request
            .post(&format!("/users/{}/password-reset", target.id))
            .add_header(name, value)
            .await;

        assert_eq!(
            res.status_code(),
            StatusCode::SEE_OTHER,
            "non-admin POST /users/:id/password-reset should redirect, got: {}",
            res.text(),
        );
        assert_eq!(
            res.maybe_header(axum::http::header::LOCATION)
                .and_then(|v| v.to_str().ok().map(str::to_string)),
            Some("/".to_string()),
            "the redirect should point at /, got: {}",
            res.text(),
        );

        // password must be unchanged
        then_password_is(&ctx, OLD_PASSWORD).await;
    })
    .await;
}

async fn when_getting_user_list(
    request: &TestServer,
    user: &prepare_data::LoggedInUser,
) -> String {
    let (name, value) = cookie(&user.token);
    let res = request
        .get("/users")
        .add_header(name, value)
        .await;
    assert_eq!(
        res.status_code(),
        StatusCode::OK,
        "the user list page should be served, got: {}",
        res.text(),
    );
    res.text()
}
