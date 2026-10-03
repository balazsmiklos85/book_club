use fluent_templates::{ArcLoader, Loader};
use serial_test::serial;

fn test_loader() -> ArcLoader {
    ArcLoader::builder("assets/i18n", unic_langid::langid!("en-US"))
        .shared_resources(Some(&["assets/shared.ftl".into()]))
        .customize(|bundle| bundle.set_use_isolating(false))
        .build()
        .expect("locales should load")
}

#[test]
#[serial]
fn i18n_keys_resolve_en_hu_de() {
    let loader = test_loader();
    let cases = vec![
        ("leaderboard", "Leaderboard", "Szavazás állása"),
        ("events", "Events", "Események"),
        ("field-recommender", "Recommender", "Ajánló"),
        ("button-vote", "Vote", "Szavazok"),
        ("button-save", "Save", "Mentés"),
        ("user-profile", "Profile", "Profil"),
        ("matrix", "Vote Matrix", "Szavazási mátrix"),
        ("password-reset", "Password reset", "Jelszóváltoztatás"),
    ];
    for (key, en_expected, hu_expected) in cases {
        let en: String = loader.lookup(&unic_langid::langid!("en-US"), key);
        assert_eq!(en, en_expected, "en-US key {key} should resolve");
        let hu: String = loader.lookup(&unic_langid::langid!("hu"), key);
        assert_eq!(hu, hu_expected, "hu key {key} should resolve");
        let de: String = loader.lookup(&unic_langid::langid!("de-DE"), key);
        assert!(
            !de.trim().is_empty() && de != key,
            "de-DE key {key} should be non-empty and not raw key"
        );
    }
}
