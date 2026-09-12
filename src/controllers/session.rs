use crate::models::users::{self, Model};
use axum::{body::Body, http::Request, middleware::Next, response::Redirect};
use axum_extra::extract::CookieJar;
use loco_rs::prelude::*;

pub async fn require_login(
    State(ctx): State<AppContext>,
    cookies: CookieJar,
    request: Request<Body>,
    next: Next,
) -> Response {
    let Some(user) = load_user(cookies, ctx).await else {
        return Redirect::to("/login").into_response();
    };
    let mut request = request;
    request.extensions_mut().insert(user);
    next.run(request).await
}

async fn load_user(cookies: CookieJar, ctx: AppContext) -> Option<Model> {
    let cookie = cookies.get("token")?;
    let jwt_secret = ctx.config.get_jwt_config().ok()?;
    let jwt = loco_rs::auth::jwt::JWT::new(&jwt_secret.secret);
    let token_data = jwt.validate(cookie.value()).ok()?;
    users::Model::find_by_pid(&ctx.db, &token_data.claims.pid)
        .await
        .ok()
}
