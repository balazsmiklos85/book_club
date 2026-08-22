use crate::models::users;
use axum_extra::extract::CookieJar;
use loco_rs::prelude::*;

/// Returns the logged-in user, or `None` if the token is missing or invalid.
pub async fn current_user(cookies: &CookieJar, ctx: &AppContext) -> Option<users::Model> {
    let cookie = cookies.get("token")?;
    let jwt_secret = ctx.config.get_jwt_config().ok()?;
    let jwt = loco_rs::auth::jwt::JWT::new(&jwt_secret.secret);
    let token_data = jwt.validate(cookie.value()).ok()?;
    users::Model::find_by_pid(&ctx.db, &token_data.claims.pid)
        .await
        .ok()
}
