//! Locale negotiation: pick which registered locale to render in, from the
//! raw `Accept-Language` header value.
//!
//! The core `resolve_locale` is pure logic — no `AppContext`, no I/O — so it is
//! unit-tested right here (see `docs/code/testing-strategy.md`). This module
//! also exposes a thin axum middleware that applies `resolve_locale` to each
//! request and hands the result to handlers via the request extensions.

use axum::{
    body::Body,
    http::{header::ACCEPT_LANGUAGE, Request},
    middleware::Next,
    response::Response,
};
use unic_langid::{langid, LanguageIdentifier};

/// The locales we actually ship translations for, and the one we fall back to.
const EN_US: LanguageIdentifier = langid!("en-US");
const DE_DE: LanguageIdentifier = langid!("de-DE");
const HU: LanguageIdentifier = langid!("hu");
const DEFAULT_LOCALE: LanguageIdentifier = langid!("en-US");

/// Every locale we can render in. We never return anything outside this set.
const REGISTERED_LOCALES: &[LanguageIdentifier] = &[EN_US, DE_DE, HU];

/// A single entry from an `Accept-Language` header: the language tag and its
/// quality value (defaulting to `1.0` when the header omits `q=`).
#[derive(Debug)]
struct Candidate {
    tag: String,
    quality: f64,
}

/// Resolve the locale to render in from a raw `Accept-Language` header value.
///
/// `accept_language` is the raw header value, or `None` when the header is
/// absent. Only ever returns one of the registered locales (`en-US`, `de-DE`,
/// `hu`); anything else falls back to the default (`en-US`).
///
/// Matching is case-insensitive, ignores region subtags we do not ship, and
/// honours `q=` values (higher quality wins; ties keep header order).
#[must_use]
pub fn resolve_locale(accept_language: Option<&str>) -> LanguageIdentifier {
    let header = match accept_language {
        Some(header) => header.trim(),
        None => return DEFAULT_LOCALE,
    };
    if header.is_empty() {
        return DEFAULT_LOCALE;
    }

    let mut candidates = parse_accept_language(header);
    if candidates.is_empty() {
        return DEFAULT_LOCALE;
    }

    // Highest quality first; `sort_by` is stable, so ties keep header order.
    candidates.sort_by(|a, b| {
        b.quality
            .partial_cmp(&a.quality)
            .unwrap_or(std::cmp::Ordering::Equal)
    });

    candidates
        .iter()
        .find_map(|candidate| match_registered(&candidate.tag))
        .unwrap_or(DEFAULT_LOCALE)
}

/// Axum middleware that resolves the request's locale from the `Accept-Language`
/// header and stores it in the request extensions for handlers to read.
///
/// Handlers read the resolved locale with the `Extension<LanguageIdentifier>`
/// extractor and inject it into the render context (see the controllers).
pub async fn resolve_locale_middleware(request: Request<Body>, next: Next) -> Response {
    let accept_language = request
        .headers()
        .get(ACCEPT_LANGUAGE)
        .and_then(|value| value.to_str().ok());
    let locale = resolve_locale(accept_language);
    let mut request = request;
    request.extensions_mut().insert(locale);
    next.run(request).await
}

/// Split a header into its `(tag, quality)` entries, dropping empty pieces and
/// any entry the client explicitly refused (`q=0`).
fn parse_accept_language(header: &str) -> Vec<Candidate> {
    header
        .split(',')
        .filter_map(|part| {
            let part = part.trim();
            let mut pieces = part.splitn(2, ';');
            let tag = pieces.next()?.trim();
            if tag.is_empty() {
                return None;
            }
            let quality = pieces.next().and_then(parse_quality).unwrap_or(1.0);
            // `q=0` means the client refuses this language, so it is not a
            // candidate at all (RFC 9110 §12.5.2).
            if quality == 0.0 {
                return None;
            }
            Some(Candidate {
                tag: tag.to_string(),
                quality,
            })
        })
        .collect()
}

/// Pull the `q=` value out of a parameter list such as `q=0.9, foo=bar`.
fn parse_quality(params: &str) -> Option<f64> {
    params.split(',').find_map(|param| {
        param
            .trim()
            .strip_prefix("q=")
            .and_then(|value| value.trim().parse::<f64>().ok())
    })
}

/// Map a single language tag onto a registered locale, if we have one.
///
/// A bare `*` wildcard matches the default. Otherwise we match on the language
/// subtag (case-insensitively) and ignore any region subtag.
fn match_registered(tag: &str) -> Option<LanguageIdentifier> {
    if tag == "*" {
        return Some(DEFAULT_LOCALE);
    }

    let language = tag.split('-').next()?.to_lowercase();
    REGISTERED_LOCALES
        .iter()
        .find(|locale| locale_language(locale) == language)
        .cloned()
}

/// Lowercased language subtag of a locale (`en-US` -> `en`, `hu` -> `hu`).
fn locale_language(locale: &LanguageIdentifier) -> String {
    locale
        .to_string()
        .split('-')
        .next()
        .unwrap_or("")
        .to_lowercase()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn resolves_plain_en_us() {
        let given_header = Some("en-US");

        let locale = resolve_locale(given_header);

        assert_eq!(locale, EN_US, "a plain en-US header should render in en-US");
    }

    #[test]
    fn resolves_hu_without_region() {
        let given_header = Some("hu");

        let locale = resolve_locale(given_header);

        assert_eq!(locale, HU, "a bare hu header should render in hu");
    }

    #[test]
    fn resolves_hu_hu_to_hu() {
        let given_header = Some("hu-HU");

        let locale = resolve_locale(given_header);

        assert_eq!(locale, HU, "hu-HU should drop the region we lack and render in hu");
    }

    #[test]
    fn resolves_uppercase_input() {
        let given_header = Some("HU");

        let locale = resolve_locale(given_header);

        assert_eq!(locale, HU, "locale matching is case-insensitive, so HU should render in hu");
    }

    #[test]
    fn prefers_higher_quality_over_first_position() {
        let given_header = Some("de-DE;q=0.9, hu;q=1.0");

        let locale = resolve_locale(given_header);

        assert_eq!(locale, HU, "hu has the higher q value so it should win over de-DE");
    }

    #[test]
    fn falls_back_to_default_when_header_absent() {
        let given_header: Option<&str> = None;

        let locale = resolve_locale(given_header);

        assert_eq!(locale, DEFAULT_LOCALE, "no header means we fall back to the default locale");
    }

    #[test]
    fn falls_back_to_default_when_header_empty() {
        let given_header = Some("   ");

        let locale = resolve_locale(given_header);

        assert_eq!(
            locale, DEFAULT_LOCALE,
            "an empty/blank header means we fall back to the default locale"
        );
    }

    #[test]
    fn falls_back_to_default_when_only_unknown_locales() {
        let given_header = Some("fr-FR,es-ES");

        let locale = resolve_locale(given_header);

        assert_eq!(
            locale, DEFAULT_LOCALE,
            "we have no fr or es translations, so fall back to the default"
        );
    }

    #[test]
    fn falls_back_to_default_for_wildcard() {
        let given_header = Some("*");

        let locale = resolve_locale(given_header);

        assert_eq!(
            locale, DEFAULT_LOCALE,
            "a bare wildcard should fall back to the default locale"
        );
    }

    #[test]
    fn prefers_known_locale_even_when_unknown_has_higher_quality() {
        let given_header = Some("fr-FR;q=1.0, hu;q=0.5");

        let locale = resolve_locale(given_header);

        assert_eq!(
            locale, HU,
            "fr is unknown so the known hu should win despite its lower q"
        );
    }

    #[test]
    fn treats_q_zero_as_refused_not_as_a_candidate() {
        let given_header = Some("de-DE;q=0, hu;q=1.0");

        let locale = resolve_locale(given_header);

        assert_eq!(
            locale, HU,
            "q=0 means the client refuses de-DE, so hu should win"
        );
    }

    #[test]
    fn falls_back_to_default_when_only_entry_is_refused() {
        let given_header = Some("de-DE;q=0");

        let locale = resolve_locale(given_header);

        assert_eq!(
            locale, DEFAULT_LOCALE,
            "a header whose only entry is q=0 refuses every locale, so fall back to the default"
        );
    }
}
