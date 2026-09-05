use axum::http::{HeaderName, HeaderValue};
use book_club::app::App;
use loco_rs::testing::prelude::*;
use serial_test::serial;

use crate::requests::prepare_data;

#[tokio::test]
#[serial]
async fn can_get_events() {
    request::<App, _, _>(|request, ctx| async move {
        let user = prepare_data::init_user_login(&request, &ctx).await;
        let res = request
            .get("/events")
            .add_header(
                HeaderName::from_static("cookie"),
                HeaderValue::from_str(&format!("token={}", user.token)).unwrap(),
            )
            .await;
        assert_eq!(res.status_code(), 200);
    })
    .await;
}

#[tokio::test]
#[serial]
async fn can_get_create() {
    request::<App, _, _>(|request, ctx| async move {
        let user = prepare_data::init_user_login(&request, &ctx).await;
        let res = request
            .get("/events/new?book_id=1&host_id=1")
            .add_header(
                HeaderName::from_static("cookie"),
                HeaderValue::from_str(&format!("token={}", user.token)).unwrap(),
            )
            .await;
        assert_eq!(res.status_code(), 200);
    })
    .await;
}
