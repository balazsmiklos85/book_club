use book_club::models::users::{self, LoginParams, RegisterParams};
use loco_rs::{app::AppContext, TestServer};
use sea_orm::{ActiveModelTrait, ActiveValue, IntoActiveModel};

const USER_EMAIL: &str = "test@loco.com";
const USER_PASSWORD: &str = "1234";

pub struct LoggedInUser {
    pub user: users::Model,
    pub token: String,
}

pub async fn init_user_login(request: &TestServer, ctx: &AppContext) -> LoggedInUser {
    request
        .post("/register")
        .form(&RegisterParams {
            email: USER_EMAIL.to_string(),
            password: USER_PASSWORD.to_string(),
            name: "loco".to_string(),
            confirm_email: USER_EMAIL.to_string(),
            confirm_password: USER_PASSWORD.to_string(),
        })
        .await;

    let login_response = request
        .post("/login")
        .form(&LoginParams {
            email: USER_EMAIL.to_string(),
            password: USER_PASSWORD.to_string(),
        })
        .await;

    let token = login_response.cookie("token").value().to_string();

    LoggedInUser {
        user: users::Entity::find_by_email(&ctx.db, USER_EMAIL)
            .await
            .unwrap(),
        token,
    }
}

/// Promotes the logged-in user to an admin so later tests can exercise the
/// `is_admin` gates. Returns a refreshed `LoggedInUser` whose `user` reflects
/// the persisted `is_admin == true` rather than the stale pre-update model.
///
/// Added ahead of the tests that use it, so it is intentionally unused for now.
#[allow(dead_code)]
pub async fn make_admin(logged_in: LoggedInUser, ctx: &AppContext) -> LoggedInUser {
    let mut active = logged_in.user.into_active_model();
    active.is_admin = ActiveValue::Set(true);
    let user = active.update(&ctx.db).await.unwrap();
    LoggedInUser {
        user,
        token: logged_in.token,
    }
}
