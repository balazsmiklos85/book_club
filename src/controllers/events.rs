#![allow(clippy::missing_errors_doc)]
#![allow(clippy::unnecessary_struct_initialization)]
#![allow(clippy::unused_async)]
use crate::controllers::session;
use crate::models::{book_suggestions, books, events, users, votes};
use axum::extract::Form;
use axum::response::Redirect;
use axum_extra::extract::CookieJar;
use chrono::NaiveDate;
use loco_rs::prelude::*;
use sea_orm::{FromQueryResult, JoinType, QuerySelect};
use serde::{Deserialize, Serialize};

#[derive(Deserialize, Serialize)]
pub struct CreateEventParams {
    pub book_id: i64,
    pub event_date: NaiveDate,
    pub host_id: Option<i64>,
}

#[derive(Debug, FromQueryResult, Serialize)]
pub struct EventRow {
    pub book_title: String,
    pub book_author: Option<String>,
    pub date: DateTime,
    pub host: Option<String>,
}

#[debug_handler]
pub async fn create(
    cookies: CookieJar,
    State(ctx): State<AppContext>,
    Form(params): Form<CreateEventParams>,
) -> Result<Response> {
    let Some(_) = session::current_user(&cookies, &ctx).await else {
        return Ok(Redirect::to("/login").into_response());
    };

    let event_date = params
        .event_date
        .and_hms_opt(0, 0, 0)
        .ok_or_else(|| Error::string("invalid event date"))?;

    tokio::try_join!(
        events::ActiveModel {
            book_id: Set(params.book_id),
            event_date: Set(event_date),
            host_id: Set(params.host_id),
            ..Default::default()
        }
        .insert(&ctx.db),
        book_suggestions::Entity::delete_many()
            .filter(crate::models::book_suggestions::Column::BookId.eq(params.book_id))
            .exec(&ctx.db),
        votes::Entity::delete_many()
            .filter(crate::models::votes::Column::BookId.eq(params.book_id))
            .exec(&ctx.db)
    )?;

    Ok(Redirect::to("/").into_response())
}

#[debug_handler]
pub async fn list(
    cookies: CookieJar,
    ViewEngine(v): ViewEngine<TeraView>,
    State(ctx): State<AppContext>,
) -> Result<Response> {
    let Some(_user) = session::current_user(&cookies, &ctx).await else {
        return Ok(Redirect::to("/login").into_response());
    };

    let rows = events::Entity::find()
        .inner_join(books::Entity)
        .join(
            JoinType::LeftJoin,
            events::Entity::belongs_to(users::Entity)
                .from(events::Column::HostId)
                .to(users::Column::Id)
                .into(),
        )
        .select_only()
        .column_as(books::Column::Title, "book_title")
        .column_as(books::Column::Author, "book_author")
        .column_as(events::Column::EventDate, "date")
        .column_as(users::Column::Name, "host")
        .into_model::<EventRow>()
        .all(&ctx.db)
        .await?;

    format::render().view(&v, "events/list.html", serde_json::json!({"events": rows}))
}

#[derive(Deserialize)]
pub struct NewEventParams {
    book_id: Option<i64>,
    host_id: Option<i64>,
}

#[debug_handler]
pub async fn new(
    cookies: CookieJar,
    Query(params): Query<NewEventParams>,
    ViewEngine(v): ViewEngine<TeraView>,
    State(ctx): State<AppContext>,
) -> Result<Response> {
    let Some(_) = session::current_user(&cookies, &ctx).await else {
        return Ok(Redirect::to("/login").into_response());
    };

    if params.book_id.is_none() || params.host_id.is_none() {
        return bad_request("book_id and host_id are required");
    }
    let users = users::Entity::find().all(&ctx.db).await?;
    let rows: Vec<serde_json::Value> = users
        .into_iter()
        .map(|u| serde_json::json!({ "id": u.id, "name": u.name }))
        .collect();
    format::render().view(
        &v,
        "events/new.html",
        serde_json::json!({
            "users": rows,
            "book": params.book_id.expect("The book ID was already checked to be `Some`"),
            "host": params.host_id.expect("The host ID was already checked to be `Some`"),
        }),
    )
}

pub fn routes() -> Routes {
    Routes::new()
        .prefix("/events")
        .add("/", get(list))
        .add("/", post(create))
        .add("/new", get(new))
}
