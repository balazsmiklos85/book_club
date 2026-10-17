#![allow(clippy::missing_errors_doc)]
use crate::models::users;
use axum::extract::{Form, Path};
use axum::response::Redirect;
use axum::Extension;
use loco_rs::{hash, prelude::*};
use sea_orm::IntoActiveModel;
use serde::Deserialize;
use unic_langid::LanguageIdentifier;

const WRONG_OLD_PASSWORD: &str = "Invalid old password";
const PASSWORD_MISMATCH: &str = "Password and confirmation do not match";

#[derive(Deserialize)]
pub struct ChangePasswordParams {
    old_password: String,
    password: String,
    confirm_password: String,
}

/// The `users` row also carries the password hash, which must never reach a
/// template context.
#[derive(serde::Serialize)]
struct ProfileView {
    id: i64,
    name: String,
    email: String,
}

fn profile_context(
    user: &users::Model,
    locale: &LanguageIdentifier,
    error: Option<&str>,
) -> serde_json::Value {
    serde_json::json!({
        "profile": ProfileView {
            id: user.id,
            name: user.name.clone(),
            email: user.email.clone(),
        },
        "error": error,
        "is_admin": user.is_admin,
        "user_id": user.id,
        "lang": locale.to_string(),
    })
}

#[debug_handler]
pub async fn details(
    Extension(user): Extension<users::Model>,
    Extension(locale): Extension<LanguageIdentifier>,
    ViewEngine(v): ViewEngine<TeraView>,
    Path(id): Path<i64>,
) -> Result<Response> {
    if id != user.id {
        return Ok(Redirect::to("/users").into_response());
    }

    format::render().view(
        &v,
        "users/details.html",
        profile_context(&user, &locale, None),
    )
}

#[debug_handler]
pub async fn change_password(
    Extension(user): Extension<users::Model>,
    Extension(locale): Extension<LanguageIdentifier>,
    ViewEngine(v): ViewEngine<TeraView>,
    State(ctx): State<AppContext>,
    Path(id): Path<i64>,
    Form(params): Form<ChangePasswordParams>,
) -> Result<Response> {
    if id != user.id {
        return Ok(Redirect::to("/users").into_response());
    }

    let error = if !user.verify_password(&params.old_password) {
        Some(WRONG_OLD_PASSWORD)
    } else if params.password != params.confirm_password {
        Some(PASSWORD_MISMATCH)
    } else {
        None
    };
    if let Some(error) = error {
        return format::render()
            .view(
                &v,
                "users/details.html",
                profile_context(&user, &locale, Some(error)),
            )
            .map(IntoResponse::into_response);
    }

    let password_hash =
        hash::hash_password(&params.password).map_err(|e| Error::string(&e.to_string()))?;
    let mut active = user.clone().into_active_model();
    active.password = ActiveValue::Set(password_hash);
    active.update(&ctx.db).await?;

    Ok(Redirect::to(&format!("/users/{id}")).into_response())
}

pub fn routes() -> Routes {
    Routes::new()
        .prefix("/users")
        .add("/{id}", get(details))
        .add("/{id}/password", post(change_password))
}
