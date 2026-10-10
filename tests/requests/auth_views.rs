use book_club::app::App;
use book_club::models::users;
use loco_rs::testing::prelude::*;
use serial_test::serial;

#[tokio::test]
#[serial]
async fn login_page_shows_old_thymeleaf_markup() {
    request::<App, _, _>(|request, _ctx| async move {
        let res = request.get("/login").await;
        assert_eq!(res.status_code(), 200);
        let body = res.text();
        assert!(
            body.contains(r#"href="/register""#),
            "login page should link to /register, got: {body}"
        );
        assert!(
            body.contains("Register here"),
            "login page should show the Register here cross-link, got: {body}"
        );
        assert!(
            body.contains(r#"name="email""#),
            "login page should have an e-mail input, got: {body}"
        );
        assert!(
            body.contains(r#"name="password""#),
            "login page should have a password input, got: {body}"
        );
        assert!(
            body.contains("🔓Login"),
            "login page should show the unlock Login button, got: {body}"
        );
    })
    .await;
}

#[tokio::test]
#[serial]
async fn register_page_shows_old_thymeleaf_markup() {
    request::<App, _, _>(|request, _ctx| async move {
        let res = request.get("/register").await;
        assert_eq!(res.status_code(), 200);
        let body = res.text();
        for input in [
            r#"name="name""#,
            r#"name="email""#,
            r#"name="confirm_email""#,
            r#"name="password""#,
            r#"name="confirm_password""#,
        ] {
            assert!(
                body.contains(input),
                "register page should contain input {input}, got: {body}"
            );
        }
        assert!(
            body.contains(r#"href="/login""#),
            "register page should link to /login, got: {body}"
        );
        assert!(
            body.contains("Login here"),
            "register page should show the Login here cross-link, got: {body}"
        );
        assert!(
            !body.contains("external_id"),
            "register page must not contain the dropped external_id field, got: {body}"
        );
    })
    .await;
}

#[tokio::test]
#[serial]
async fn register_rejects_mismatched_password() {
    request::<App, _, _>(|request, ctx| async move {
        let email = "mismatch-pwd@example.com";

        let res = request
            .post("/register")
            .form(&users::RegisterParams {
                email: email.to_string(),
                password: "password1".to_string(),
                name: "Mismatch Pwd".to_string(),
                confirm_email: email.to_string(),
                confirm_password: "password2".to_string(),
            })
            .await;

        // Should return 200 with the error, NOT 303 redirect
        assert_eq!(
            res.status_code(),
            200,
            "expected 200 for mismatched passwords, got {}",
            res.status_code()
        );
        let body = res.text();
        assert!(
            body.contains("Password and confirmation do not match"),
            "body should show the password-mismatch error, got: {body}"
        );

        // Core assertion: no user was created
        let found = users::Entity::find_by_email(&ctx.db, email).await;
        assert!(
            found.is_err(),
            "no user should exist for {email}, but find_by_email returned: {found:?}"
        );
    })
    .await;
}

#[tokio::test]
#[serial]
async fn register_rejects_mismatched_email() {
    request::<App, _, _>(|request, ctx| async move {
        let email = "mismatch-email@example.com";

        let res = request
            .post("/register")
            .form(&users::RegisterParams {
                email: email.to_string(),
                password: "password1".to_string(),
                name: "Mismatch Email".to_string(),
                confirm_email: "different@example.com".to_string(),
                confirm_password: "password1".to_string(),
            })
            .await;

        // Should return 200 with the error, NOT 303 redirect
        assert_eq!(
            res.status_code(),
            200,
            "expected 200 for mismatched emails, got {}",
            res.status_code()
        );
        let body = res.text();
        assert!(
            body.contains("Password and confirmation do not match"),
            "body should show the password-mismatch error, got: {body}"
        );

        // Core assertion: no user was created
        let found = users::Entity::find_by_email(&ctx.db, email).await;
        assert!(
            found.is_err(),
            "no user should exist for {email}, but find_by_email returned: {found:?}"
        );
    })
    .await;
}

#[tokio::test]
#[serial]
async fn register_accepts_matching_confirmation() {
    request::<App, _, _>(|request, ctx| async move {
        let email = "matching@example.com";

        let res = request
            .post("/register")
            .form(&users::RegisterParams {
                email: email.to_string(),
                password: "password1".to_string(),
                name: "Matching".to_string(),
                confirm_email: email.to_string(),
                confirm_password: "password1".to_string(),
            })
            .await;

        // Should redirect (303) on success
        assert_eq!(
            res.status_code(),
            303,
            "expected 303 redirect for matching confirmation, got {}",
            res.status_code()
        );

        // Core assertion: user now exists
        let found = users::Entity::find_by_email(&ctx.db, email).await;
        assert!(
            found.is_ok(),
            "user should exist for {email}, but find_by_email returned: {found:?}"
        );
    })
    .await;
}
