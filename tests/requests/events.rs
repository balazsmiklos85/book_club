use axum::http::{HeaderName, HeaderValue};
use book_club::app::App;
use book_club::controllers::events::CreateEventParams;
use book_club::models::books;
use chrono::NaiveDate;
use loco_rs::testing::prelude::*;
use sea_orm::{ActiveModelTrait, ActiveValue::Set};
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

#[tokio::test]
#[serial]
async fn can_create_event() {
    request::<App, _, _>(|request, ctx| async move {
        let user = prepare_data::init_user_login(&request, &ctx).await;
        books::ActiveModel {
            title: Set("Test Book".to_string()),
            url: Set("https://example.com/test-book".to_string()),
            ..Default::default()
        }
        .insert(&ctx.db)
        .await
        .unwrap();
        let res = request
            .post("/events")
            .add_header(
                HeaderName::from_static("cookie"),
                HeaderValue::from_str(&format!("token={}", user.token)).unwrap(),
            )
            .form(&CreateEventParams {
                book_id: 1,
                event_date: NaiveDate::from_ymd_opt(2026, 12, 1).unwrap(),
                host_id: None,
            })
            .await;
        assert_eq!(res.status_code(), 303);
    })
    .await;
}
