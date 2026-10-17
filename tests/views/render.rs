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
            serde_json::json!({"is_admin": true, "user_id": 1, "lang": "en-US"}),
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
            serde_json::json!({"is_admin": false, "user_id": 1, "lang": "en-US"}),
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

/// Guard: every template that reads `lang` from the context (`lang=lang`) must
/// actually REQUIRE it — rendering without `lang` must fail, and rendering with
/// `lang` must succeed.
///
/// This is what makes a render site that forgets to inject `lang` a runtime
/// error (Tera "variable not found") rather than a silent fallback. Walking the
/// templates (rather than testing each route) means a new template that adopts
/// `lang=lang` is covered automatically, without a new test.
#[test]
fn templates_using_lang_require_lang_in_context() {
    use std::fs;
    use std::path::Path;

    let view = build_view();
    let views_dir = Path::new("assets/views");

    /// Recursively collect all `.html` files under `dir`.
    fn collect_html_files(dir: &Path, out: &mut Vec<std::path::PathBuf>) {
        let entries = fs::read_dir(dir).expect("views dir should be readable");
        for entry in entries.flatten() {
            let path = entry.path();
            if path.is_dir() {
                collect_html_files(&path, out);
            } else if path.extension().is_some_and(|ext| ext == "html") {
                out.push(path);
            }
        }
    }

    let mut html_files = Vec::new();
    collect_html_files(views_dir, &mut html_files);

    let mut lang_templates: Vec<(std::path::PathBuf, String)> = Vec::new();
    for path in &html_files {
        let content = fs::read_to_string(path).expect("template should be readable");
        if content.contains("lang=lang") {
            lang_templates.push((path.clone(), content));
        }
    }

    assert!(
        !lang_templates.is_empty(),
        "expected at least one template to use lang=lang, but found none — \
         the guard would be vacuous"
    );

    /// Matches the sequence a `{% for x in y %}` tag loops over, capturing `y`.
    static LOOP_SOURCE: std::sync::LazyLock<regex::Regex> = std::sync::LazyLock::new(|| {
        regex::Regex::new(r"\{%-?\s*for\s+.*?\s+in\s+([A-Za-z_]\w*(?:\.[A-Za-z_]\w*)*)")
            .expect("loop pattern should compile")
    });

    /// Matches a field read off an object, capturing the object and the field.
    static FIELD_ACCESS: std::sync::LazyLock<regex::Regex> = std::sync::LazyLock::new(|| {
        regex::Regex::new(r"(?:\{\{-?\s*|\{%-?\s*if\s+)([A-Za-z_]\w*)\.([A-Za-z_]\w*)")
            .expect("field-access pattern should compile")
    });

    /// The context a template needs to render besides `lang`. Derived from the
    /// template's own source, so a future template is covered without naming
    /// it here: whatever it loops over gets an empty list, the objects it reads
    /// fields off get those fields stubbed as null, and the menu fragment gets
    /// the `user_id` and `is_admin` it reads.
    fn set_loop_source(ctx: &mut serde_json::Value, path: &str) {
        let mut parts = path.split('.').collect::<Vec<_>>();
        let leaf = parts.pop().expect("loop source should not be empty");
        let mut node = ctx;
        for part in parts {
            node = &mut node[part];
        }
        node[leaf] = serde_json::json!([]);
    }

    fn context_for(relative: &str, content: &str, with_lang: bool) -> serde_json::Value {
        let mut ctx = serde_json::json!({});
        if with_lang {
            ctx["lang"] = serde_json::json!("en-US");
        }
        for name in LOOP_SOURCE
            .captures_iter(content)
            .filter_map(|caps| caps.get(1))
            .map(|m| m.as_str())
        {
            set_loop_source(&mut ctx, name);
        }
        for caps in FIELD_ACCESS.captures_iter(content) {
            let (Some(object), Some(field)) = (caps.get(1), caps.get(2)) else {
                continue;
            };
            let object = ctx
                .as_object_mut()
                .expect("context should be a json object")
                .entry(object.as_str().to_string())
                .or_insert_with(|| serde_json::json!({}));
            if let Some(map) = object.as_object_mut() {
                map.insert(field.as_str().to_string(), serde_json::Value::Null);
            }
        }
        if relative == "menu.html" || content.contains("menu.html") {
            ctx["is_admin"] = serde_json::json!(true);
            ctx["user_id"] = serde_json::json!(1);
        }
        ctx
    }

    for (template, content) in &lang_templates {
        let relative = template
            .strip_prefix(views_dir)
            .expect("template should be under the views dir")
            .display()
            .to_string();

        // Without `lang`, the template must fail — this is what turns a render
        // site that forgets to inject `lang` into a loud runtime error.
        let without_lang = view.render(&relative, context_for(&relative, content, false));
        assert!(
            without_lang.is_err(),
            "template {relative} uses lang=lang but rendered WITHOUT lang — \
             a render site that forgets to inject `lang` would silently render \
             in the wrong locale instead of failing loudly"
        );

        // With `lang`, the template must render successfully.
        view.render(&relative, context_for(&relative, content, true))
            .unwrap_or_else(|e| panic!(
                "template {relative} uses lang=lang but failed to render with lang provided: {e}"
            ));
    }
}
