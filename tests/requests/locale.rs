use axum::http::{HeaderName, HeaderValue};
use book_club::app::App;
use loco_rs::testing::prelude::*;
use serial_test::serial;

/// The login page is the simplest render site: no auth, no DB. It exercises the
/// full per-request locale path — middleware resolves the locale, the handler
/// injects it, and the template renders in it.
///
/// Each test asserts a real translated string from the shipped `.ftl` files so
/// the assertion is meaningful (not just "it rendered").

#[tokio::test]
#[serial]
async fn renders_login_page_in_hungarian_when_accept_language_is_hu() {
    request::<App, _, _>(|request, _ctx| async move {
        let res = request
            .get("/login")
            .add_header(
                HeaderName::from_static("accept-language"),
                HeaderValue::from_static("hu"),
            )
            .await;
        assert_eq!(res.status_code(), 200);
        let body = res.text();
        assert!(
            body.contains("Bejelentkezés"),
            "login page should show the Hungarian 'Bejelentkezés', got: {body}"
        );
        assert!(
            body.contains("Regisztrálj itt"),
            "login page should show the Hungarian 'Regisztrálj itt' cross-link, got: {body}"
        );
        assert!(
            !body.contains("Register here"),
            "login page should not show the English 'Register here', got: {body}"
        );
    })
    .await;
}

#[tokio::test]
#[serial]
async fn renders_login_page_in_german_when_accept_language_is_de_de() {
    request::<App, _, _>(|request, _ctx| async move {
        let res = request
            .get("/login")
            .add_header(
                HeaderName::from_static("accept-language"),
                HeaderValue::from_static("de-DE"),
            )
            .await;
        assert_eq!(res.status_code(), 200);
        let body = res.text();
        assert!(
            body.contains("Anmelden"),
            "login page should show the German 'Anmelden', got: {body}"
        );
        assert!(
            body.contains("Hier registrieren"),
            "login page should show the German 'Hier registrieren' cross-link, got: {body}"
        );
        assert!(
            !body.contains("Register here"),
            "login page should not show the English 'Register here', got: {body}"
        );
    })
    .await;
}

#[tokio::test]
#[serial]
async fn renders_login_page_in_english_when_no_accept_language() {
    request::<App, _, _>(|request, _ctx| async move {
        let res = request.get("/login").await;
        assert_eq!(res.status_code(), 200);
        let body = res.text();
        assert!(
            body.contains("🔓Login"),
            "login page should show the English 'Login' button, got: {body}"
        );
        assert!(
            body.contains("Register here"),
            "login page should show the English 'Register here' cross-link, got: {body}"
        );
    })
    .await;
}

#[tokio::test]
#[serial]
async fn renders_login_page_in_english_when_accept_language_is_unsupported() {
    request::<App, _, _>(|request, _ctx| async move {
        let res = request
            .get("/login")
            .add_header(
                HeaderName::from_static("accept-language"),
                HeaderValue::from_static("fr-FR,es-ES"),
            )
            .await;
        assert_eq!(res.status_code(), 200);
        let body = res.text();
        assert!(
            body.contains("🔓Login"),
            "login page should fall back to the English 'Login' button, got: {body}"
        );
        assert!(
            body.contains("Register here"),
            "login page should fall back to the English 'Register here' cross-link, got: {body}"
        );
    })
    .await;
}
