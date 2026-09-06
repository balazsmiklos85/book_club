use axum::http::{HeaderName, HeaderValue};
use book_club::app::App;
use book_club::controllers::events::CreateEventParams;
use book_club::models::{books, events as events_model};
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
        let hosted_book = books::ActiveModel {
            title: Set("Hosted Book".to_string()),
            author: Set(Some("Hosted Author".to_string())),
            url: Set("https://example.com/hosted-book".to_string()),
            ..Default::default()
        }
        .insert(&ctx.db)
        .await
        .unwrap();
        let orphan_book = books::ActiveModel {
            title: Set("Orphan Book".to_string()),
            author: Set(Some("Orphan Author".to_string())),
            url: Set("https://example.com/orphan-book".to_string()),
            ..Default::default()
        }
        .insert(&ctx.db)
        .await
        .unwrap();
        events_model::ActiveModel {
            book_id: Set(hosted_book.id),
            event_date: Set(NaiveDate::from_ymd_opt(2026, 12, 1)
                .unwrap()
                .and_hms_opt(0, 0, 0)
                .unwrap()),
            host_id: Set(Some(user.user.id)),
            ..Default::default()
        }
        .insert(&ctx.db)
        .await
        .unwrap();
        events_model::ActiveModel {
            book_id: Set(orphan_book.id),
            event_date: Set(NaiveDate::from_ymd_opt(2026, 12, 2)
                .unwrap()
                .and_hms_opt(0, 0, 0)
                .unwrap()),
            host_id: Set(None),
            ..Default::default()
        }
        .insert(&ctx.db)
        .await
        .unwrap();
        let res = request
            .get("/events")
            .add_header(
                HeaderName::from_static("cookie"),
                HeaderValue::from_str(&format!("token={}", user.token)).unwrap(),
            )
            .await;
        assert_eq!(res.status_code(), 200);
        let body = res.text();
        assert!(body.contains("Hosted Book"), "missing hosted book: {body}");
        assert!(body.contains("Orphan Book"), "missing orphan book: {body}");
        assert!(
            !body.contains("by  by"),
            "double 'by' bug present in: {body}"
        );
        assert!(
            !body.contains("None"),
            "raw None leaked into output: {body}"
        );
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
