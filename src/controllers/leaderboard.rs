#![allow(clippy::missing_errors_doc)]
use crate::controllers::session;
use crate::models::books;
use axum::response::Redirect;
use axum_extra::extract::CookieJar;
use loco_rs::prelude::*;

pub async fn home(
    cookies: CookieJar,
    ViewEngine(v): ViewEngine<TeraView>,
    State(ctx): State<AppContext>,
) -> Result<Response> {
    let Some(user) = session::current_user(&cookies, &ctx).await else {
        return Ok(Redirect::to("/login").into_response());
    };
    let books = books::Entity::find().all(&ctx.db).await?;
    format::render().view(
        &v,
        "leaderboard.html",
        serde_json::json!({ "books": books }),
    )
}

pub fn routes() -> Routes {
    Routes::new().add("/", get(home))
}
