use book_club::{
    app::App,
    models::{books, events, participants},
};
use chrono::NaiveDate;
use loco_rs::testing::prelude::*;
use sea_orm::{ActiveModelTrait, ActiveValue::Set, DatabaseConnection, EntityTrait};
use serial_test::serial;

macro_rules! configure_insta {
    ($($expr:expr),*) => {
        let mut settings = insta::Settings::clone_current();
        settings.set_prepend_module_to_snapshot(false);
        let _guard = settings.bind_to_scope();
    };
}

/// The `Participants` entity matches the table its migration created.
///
/// Selecting every column is the cheapest assertion that says something true:
/// it fails if the migration and the entity disagree — a renamed column, a
/// type that does not round-trip, a migration that never ran — which is the
/// most common way a generated model breaks. Extend it as the model grows.
#[tokio::test]
#[serial]
async fn can_query_participants() {
    configure_insta!();

    let boot = boot_test::<App>().await.unwrap();
    seed::<App>(&boot.app_context).await.unwrap();

    // The query is the assertion. Bind the result and compare it once you have
    // seed data to compare against, e.g.:
    //
    // let items = participants::Entity::find().all(&boot.app_context.db).await.unwrap();
    // assert_debug_snapshot!(items);
    participants::Entity::find()
        .all(&boot.app_context.db)
        .await
        .expect("`participants` should be queryable — entity and migration must agree");
}

#[tokio::test]
#[serial]
async fn second_participant_for_same_event_and_user_is_noop() {
    let db = given_seeded_db().await;
    let event = given_event(&db).await;

    when_adding_participant(&db, event, 1).await;
    when_adding_participant(&db, event, 1).await;

    then_participant_count_is(&db, 1).await;
}

async fn given_seeded_db() -> DatabaseConnection {
    let boot = boot_test::<App>().await.unwrap();
    seed::<App>(&boot.app_context).await.unwrap();
    boot.app_context.db.clone()
}

async fn given_event(db: &DatabaseConnection) -> i64 {
    let book = books::ActiveModel {
        title: Set("Duplicate Participant Book".to_string()),
        url: Set("https://example.com/duplicate-participant".to_string()),
        ..Default::default()
    }
    .insert(db)
    .await
    .unwrap();
    events::ActiveModel {
        book_id: Set(book.id),
        event_date: Set(NaiveDate::from_ymd_opt(2026, 12, 1)
            .unwrap()
            .and_hms_opt(0, 0, 0)
            .unwrap()),
        ..Default::default()
    }
    .insert(db)
    .await
    .unwrap()
    .id
}

async fn when_adding_participant(db: &DatabaseConnection, event_id: i64, user_id: i64) {
    participants::Entity::insert(participants::ActiveModel {
        event_id: Set(event_id),
        user_id: Set(user_id),
        ..Default::default()
    })
    .on_conflict_do_nothing_on([participants::Column::EventId, participants::Column::UserId])
    .exec(db)
    .await
    .expect("a conflicting insert should be a no-op, not an error");
}

async fn then_participant_count_is(db: &DatabaseConnection, expected: usize) {
    let rows = participants::Entity::find().all(db).await.unwrap();
    assert_eq!(rows.len(), expected, "got {rows:?}");
}
