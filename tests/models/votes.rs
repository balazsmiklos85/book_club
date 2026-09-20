use book_club::{
    app::App,
    models::{books, votes},
};
use loco_rs::testing::prelude::*;
use sea_orm::{ActiveModelTrait, ActiveValue::Set, DatabaseConnection, EntityTrait};
use serial_test::serial;

#[tokio::test]
#[serial]
async fn cleans_up_only_target_book_votes() {
    let db = given_seeded_db().await;
    let (target, other, _) = given_books_with_votes(&db).await;

    when_cleaning_up_book(&db, target).await;

    then_target_book_has_no_votes(&db, target).await;
    then_other_book_keeps_votes(&db, other).await;
}

async fn given_seeded_db() -> DatabaseConnection {
    let boot = boot_test::<App>().await.unwrap();
    seed::<App>(&boot.app_context).await.unwrap();
    boot.app_context.db.clone()
}

async fn given_books_with_votes(db: &DatabaseConnection) -> (i64, i64, usize) {
    let target = books::ActiveModel {
        title: Set("Target Book".to_string()),
        url: Set("https://example.com/cleanup-target-votes".to_string()),
        ..Default::default()
    }
    .insert(db)
    .await
    .unwrap();
    let other = books::ActiveModel {
        title: Set("Other Book".to_string()),
        url: Set("https://example.com/cleanup-other-votes".to_string()),
        ..Default::default()
    }
    .insert(db)
    .await
    .unwrap();

    for (book_id, user_id) in [(target.id, 1), (target.id, 2), (other.id, 1)] {
        votes::ActiveModel {
            book_id: Set(book_id),
            user_id: Set(user_id),
            ..Default::default()
        }
        .insert(db)
        .await
        .unwrap();
    }
    (target.id, other.id, 3)
}

async fn when_cleaning_up_book(db: &DatabaseConnection, book_id: i64) {
    votes::Entity::clean_up_by_book(db, book_id).await.unwrap();
}

async fn then_target_book_has_no_votes(db: &DatabaseConnection, target: i64) {
    let remaining = votes::Entity::find().all(db).await.unwrap();
    assert!(
        remaining.iter().all(|v| v.book_id != target),
        "target book votes should be gone, got {remaining:?}"
    );
}

async fn then_other_book_keeps_votes(db: &DatabaseConnection, other: i64) {
    let remaining = votes::Entity::find().all(db).await.unwrap();
    assert_eq!(
        remaining.iter().filter(|v| v.book_id == other).count(),
        1,
        "other book votes should be kept, got {remaining:?}"
    );
}

#[tokio::test]
#[serial]
async fn second_vote_for_same_book_and_user_is_noop() {
    let db = given_seeded_db().await;
    let (target_book, _, vote_count) = given_books_with_votes(&db).await;

    votes::Entity::vote(&db, target_book, 1).await.unwrap();

    then_vote_count_does_not_change(&db, vote_count).await;
}

async fn then_vote_count_does_not_change(db: &DatabaseConnection, vote_count: usize) {
    let remaining = votes::Entity::find().all(db).await.unwrap();
    assert_eq!(
        remaining.len(),
        vote_count,
        "second vote should be a no-op, got {remaining:?}"
    );
}
