use loco_rs::prelude::*;
use axum::{extract::Form, response::Redirect};
use crate::models::users::{self, RegisterParams};

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
        .add("/register", get(register_page).post(register))
}
