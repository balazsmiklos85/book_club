use axum_extra::extract::CookieJar;
use axum::response::Redirect;
use crate::models::users;
use loco_rs::prelude::*;

pub async fn home(
    cookies: CookieJar,
    ViewEngine(v): ViewEngine<TeraView>,
    State(ctx): State<AppContext>,
) -> Result<Response> {
    let Some(cookie) = cookies.get("token") else {
        return Ok(Redirect::to("/login").into_response());
    };

    let jwt_secret = ctx.config.get_jwt_config()?;
    let jwt = loco_rs::auth::jwt::JWT::new(&jwt_secret.secret);
    let Ok(token_data) = jwt.validate(cookie.value()) else {
        return Ok(Redirect::to("/login").into_response());
    };

    let Ok(user) = users::Model::find_by_pid(&ctx.db, &token_data.claims.pid).await else {
        return Ok(Redirect::to("/login").into_response());
    };
    format::render().view(&v, "leaderboard.html", serde_json::json!({}))
}

pub fn routes() -> Routes {
    Routes::new().add("/", get(home))
}
