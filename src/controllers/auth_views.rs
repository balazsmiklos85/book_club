#![allow(clippy::missing_errors_doc)]
use crate::models::users::{self, LoginParams, RegisterParams};
use axum::{extract::Form, response::Redirect};
use loco_rs::prelude::*;

pub async fn login_page(ViewEngine(v): ViewEngine<TeraView>) -> Result<Response> {
    format::render().view(&v, "auth/login.html", serde_json::json!({}))
}

pub async fn login(
    ViewEngine(v): ViewEngine<TeraView>,
    State(ctx): State<AppContext>,
    Form(params): Form<LoginParams>,
) -> Result<Response> {
    let user = users::Model::find_by_email(&ctx.db, &params.email).await?;
    if !user.verify_password(&params.password) {
        return format::render()
            .view(
                &v,
                "auth/login.html",
                serde_json::json!({"error": "Invalid credentials"}),
            )
            .map(IntoResponse::into_response);
    }
    let jwt_secret = ctx.config.get_jwt_config()?;
    let token = user.generate_jwt(&jwt_secret.secret, jwt_secret.expiration)?;
    Ok((
        [("SET-COOKIE", format!("token={token}; Path=/; HttpOnly"))],
        Redirect::to("/"),
    )
        .into_response())
}

pub async fn logout() -> Result<Response> {
    Ok((
        [("SET-COOKIE", "token=; Path=/; HttpOnly; Max-Age:0")],
        Redirect::to("/login"),
    )
        .into_response())
}

pub async fn register_page(ViewEngine(v): ViewEngine<TeraView>) -> Result<Response> {
    format::render().view(&v, "auth/register.html", serde_json::json!({}))
}

pub async fn register(
    State(ctx): State<AppContext>,
    Form(params): Form<RegisterParams>,
) -> Result<Response> {
    users::Model::create_with_password(&ctx.db, &params).await?;
    Ok(Redirect::to("/register").into_response())
}

pub fn routes() -> Routes {
    Routes::new()
        .add("/login", get(login_page).post(login))
        .add("/logout", get(logout))
        .add("/register", get(register_page).post(register))
}
