use book_club::app::App;
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
