use book_club::models::users::{self, LoginParams, RegisterParams};
use loco_rs::{app::AppContext, TestServer};

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
        user: users::Model::find_by_email(&ctx.db, USER_EMAIL)
            .await
            .unwrap(),
        token,
    }
}
