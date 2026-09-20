use book_club::{
    app::App,
    models::{book_suggestions, books},
};
use loco_rs::testing::prelude::*;
use sea_orm::{ActiveModelTrait, ActiveValue::Set, DatabaseConnection, EntityTrait};
use serial_test::serial;

#[tokio::test]
#[serial]
async fn cleans_up_only_target_book_suggestions() {
    let db = given_seeded_db().await;
    let (target, other) = given_books_with_suggestions(&db).await;

    when_cleaning_up_book(&db, target).await;

    then_target_book_has_no_suggestions(&db, target).await;
    then_other_book_keeps_suggestions(&db, other).await;
}

async fn given_seeded_db() -> DatabaseConnection {
    let boot = boot_test::<App>().await.unwrap();
    seed::<App>(&boot.app_context).await.unwrap();
    boot.app_context.db.clone()
}

async fn given_books_with_suggestions(db: &DatabaseConnection) -> (i64, i64) {
    let target = books::ActiveModel {
        title: Set("Target Book".to_string()),
        url: Set("https://example.com/cleanup-target-suggestions".to_string()),
        ..Default::default()
    }
    .insert(db)
    .await
    .unwrap();
    let other = books::ActiveModel {
        title: Set("Other Book".to_string()),
        url: Set("https://example.com/cleanup-other-suggestions".to_string()),
        ..Default::default()
    }
    .insert(db)
    .await
    .unwrap();
    for (book_id, user_id) in [(target.id, 1), (target.id, 2), (other.id, 1)] {
        book_suggestions::ActiveModel {
            book_id: Set(book_id),
            user_id: Set(user_id),
            ..Default::default()
        }
        .insert(db)
        .await
        .unwrap();
    }
    (target.id, other.id)
}

async fn when_cleaning_up_book(db: &DatabaseConnection, book_id: i64) {
    book_suggestions::Entity::clean_up_by_book(db, book_id)
        .await
        .unwrap();
}

async fn then_target_book_has_no_suggestions(db: &DatabaseConnection, target: i64) {
    let remaining = book_suggestions::Entity::find().all(db).await.unwrap();
    assert!(
        remaining.iter().all(|s| s.book_id != target),
        "target book suggestions should be gone, got {remaining:?}"
    );
}

async fn then_other_book_keeps_suggestions(db: &DatabaseConnection, other: i64) {
    let remaining = book_suggestions::Entity::find().all(db).await.unwrap();
    assert_eq!(
        remaining.iter().filter(|s| s.book_id == other).count(),
        1,
        "other book suggestions should be kept, got {remaining:?}"
    );
}
