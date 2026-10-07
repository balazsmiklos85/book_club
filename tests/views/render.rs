use fluent_templates::{ArcLoader, FluentLoader};
use loco_rs::controller::views::{engines, ViewRenderer};

/// Builds the same Tera view engine the app builds at boot, including the i18n
/// `t()` function the templates call.
///
/// The template engine validates function references when templates are loaded,
/// so a registration-ordering mistake fails here rather than at runtime.
fn build_view() -> engines::TeraView {
    let loader = std::sync::Arc::new(
        ArcLoader::builder("assets/i18n", unic_langid::langid!("en-US"))
            .shared_resources(Some(&["assets/shared.ftl".into()]))
            .customize(|bundle| bundle.set_use_isolating(false))
            .build()
            .expect("locales should load"),
    );

    engines::TeraView::build_with_post_process(move |tera| {
        tera.register_function("t", FluentLoader::new(loader.clone()));
        Ok(())
    })
    .expect("view engine should build")
}

/// Renders the shipped Tera view through the same view engine the app builds at
/// boot, including the i18n `t()` function the template calls.
///
/// This covers the whole server-side rendering path end to end: registering a
/// custom function, loading templates that use it, and resolving locales.
#[test]
fn renders_home_view_with_i18n() {
    let view = build_view();

    let rendered = view
        .render("home/hello.html", serde_json::json!({}))
        .expect("home view should render");

    assert!(
        rendered.contains("Hello World"),
        "expected the i18n key to resolve, got: {rendered}"
    );
}

/// The shared menu fragment shows every nav link to an admin, but hides the
/// Vote Matrix link from a regular member.
#[test]
fn renders_menu_fragment_with_admin_gate() {
    let view = build_view();

    let admin = view
        .render(
            "menu.html",
            serde_json::json!({"is_admin": true, "user_id": 1}),
        )
        .expect("menu fragment should render for an admin");
    for link in [
        "📊Leaderboard",
        "📆Events",
        "👤Profile",
        "📋Users",
        "🔧Vote Matrix",
        "🚪Logout",
    ] {
        assert!(
            admin.contains(link),
            "admin menu should contain the {link} link, got: {admin}"
        );
    }

    // Every link must point at the right URL, in the old app's menu order:
    // Leaderboard, Events, Profile, Users, Vote Matrix, Logout.
    let admin_hrefs = [
        "href=\"/\"",
        "href=\"/events/\"",
        "href=\"/users/1\"",
        "href=\"/users\"",
        "href=\"/vote/matrix\"",
        "href=\"/logout\"",
    ];
    let mut prev = 0;
    for href in admin_hrefs {
        let pos = admin
            .find(href)
            .unwrap_or_else(|| panic!("admin menu should contain {href}, got: {admin}"));
        assert!(
            pos > prev,
            "menu links should appear in order (Leaderboard, Events, Profile, Users, Vote Matrix, Logout); {href} at {pos} is not after the previous link at {prev}; got: {admin}"
        );
        prev = pos;
    }

    let member = view
        .render(
            "menu.html",
            serde_json::json!({"is_admin": false, "user_id": 1}),
        )
        .expect("menu fragment should render for a member");
    assert!(
        !member.contains("🔧Vote Matrix"),
        "member menu should omit the Vote Matrix link, got: {member}"
    );
    assert!(
        !member.contains("href=\"/vote/matrix\""),
        "member menu should omit the Vote Matrix link, got: {member}"
    );
    for link in [
        "📊Leaderboard",
        "📆Events",
        "👤Profile",
        "📋Users",
        "🚪Logout",
    ] {
        assert!(
            member.contains(link),
            "member menu should still contain the {link} link, got: {member}"
        );
    }

    // The member menu keeps the same order, minus the Vote Matrix link.
    let member_hrefs = [
        "href=\"/\"",
        "href=\"/events/\"",
        "href=\"/users/1\"",
        "href=\"/users\"",
        "href=\"/logout\"",
    ];
    let mut prev = 0;
    for href in member_hrefs {
        let pos = member
            .find(href)
            .unwrap_or_else(|| panic!("member menu should contain {href}, got: {member}"));
        assert!(
            pos > prev,
            "menu links should appear in order (Leaderboard, Events, Profile, Users, Logout); {href} at {pos} is not after the previous link at {prev}; got: {member}"
        );
        prev = pos;
    }
}
